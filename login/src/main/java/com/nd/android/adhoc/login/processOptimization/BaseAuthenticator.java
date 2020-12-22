package com.nd.android.adhoc.login.processOptimization;


import android.text.TextUtils;
import android.util.Log;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceHelper;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceInfoManager;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;
import com.nd.adhoc.assistant.sdk.deviceInfo.UserLoginConfig;
import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.frame.api.user.IAdhocLoginStatusNotifier;
import com.nd.android.adhoc.basic.frame.constant.AdhocRouteConstant;
import com.nd.android.adhoc.basic.frame.factory.AdhocFrameFactory;
import com.nd.android.adhoc.basic.log.CrashAnalytics;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.ui.activity.ActivityStackManager;
import com.nd.android.adhoc.communicate.impl.MdmTransferFactory;
import com.nd.android.adhoc.communicate.push.IPushModule;
import com.nd.android.adhoc.login.basicService.data.http.ActivateUserResponse;
import com.nd.android.adhoc.login.basicService.data.http.GetActivateUserResultResponse;
import com.nd.android.adhoc.login.basicService.data.http.QueryDeviceStatusResponse;
import com.nd.android.adhoc.login.enumConst.ActivateUserType;
import com.nd.android.adhoc.login.info.AdhocLoginInfoImpl;
import com.nd.android.adhoc.login.info.AdhocUserInfoImpl;
import com.nd.android.adhoc.login.utils.DeviceActivateBroadcastUtils;
import com.nd.android.adhoc.loginapi.ISchoolGroupCodeRetriever;
import com.nd.android.adhoc.loginapi.exception.DeviceIDNotSetException;
import com.nd.android.adhoc.loginapi.exception.QueryActivateUserResultException;
import com.nd.android.adhoc.loginapi.exception.QueryActivateUserTimeoutException;
import com.nd.sdp.android.serviceloader.AnnotationServiceLoader;

import java.util.Iterator;
import java.util.Random;

import rx.Observable;
import rx.Subscriber;

public abstract class BaseAuthenticator extends BaseAbilityProvider {

    protected IDeviceStatusListener mDeviceStatusListener = null;

    private static String TAG = "BaseAuthenticator";

    public BaseAuthenticator(IDeviceStatusListener pListener) {
        mDeviceStatusListener = pListener;
    }

    protected void notifyLogin(String pAccountNum, String pNickName) {
        IAdhocLoginStatusNotifier api = (IAdhocLoginStatusNotifier) AdhocFrameFactory.getInstance().getAdhocRouter()
                .build(AdhocRouteConstant.PATH_LOGIN_STATUS_NOTIFIER).navigation();
        if (api == null) {
            return;
        }

        AdhocUserInfoImpl userInfo = new AdhocUserInfoImpl(pAccountNum, pNickName);
        AdhocLoginInfoImpl loginInfo = new AdhocLoginInfoImpl(userInfo, null);
        api.onLogin(loginInfo);
    }

    private static boolean msIsReallyQuery = false;
    //queryDeviceStatusFromServer那个方法加了其他的业务代码，这里是纯从服务端取状态
    protected Observable<DeviceStatus> reallyQueryDeviceStatusFromServer(final String pDeviceID) {
        Log.e("yhq", "reallyQueryDeviceStatusFromServer");
        return Observable
                .create(new Observable.OnSubscribe<DeviceStatus>() {
                    @Override
                    public void call(Subscriber<? super DeviceStatus> pSubscriber) {
                        if(msIsReallyQuery){
                            pSubscriber.onError(new AdhocException("is already querying"));
                            return;
                        }
                        msIsReallyQuery = true;
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        try {
                            Log.i("yhq", " start run reallyQueryDeviceStatusFromServer");
                            String serialNum = DeviceHelper.getSerialNumberThroughControl();

                            if (TextUtils.isEmpty(pDeviceID) || TextUtils.isEmpty(serialNum)) {
                                Log.i("yhq", " reallyQueryDeviceStatusFromServer error 1");
                                msIsReallyQuery = false;
                                pSubscriber.onError(new DeviceIDNotSetException());
                                return;
                            }

                            UserLoginConfig loginConfig = DeviceInfoManager.getInstance().getUserLoginConfig();
                            QueryDeviceStatusResponse result = null;
                            DeviceStatus status = null;
                            if (isAutoLogin()) {
                                Log.i("yhq", " reallyQueryDeviceStatusFromServer isAutoLogin 1");
                                // 自动登录的情况下，要把autoLogin的值1带上去
                                result = getHttpService().getDeviceStatus(pDeviceID, serialNum,
                                        loginConfig.getAutoLogin(), loginConfig.getNeedGroup());
                                Log.i("yhq", "user auto login reallyQueryDeviceStatusFromServer:"
                                        + result.toString());
                                status = result.getStatus();
                            } else {
                                result = getHttpService().getDeviceStatus(pDeviceID, serialNum);
                                Log.i("yhq", "really QueryDeviceStatusResponse:" + result.toString());
                                status = result.getStatus();
                            }
                            msIsReallyQuery = false;
                            pSubscriber.onNext(status);
                            pSubscriber.onCompleted();
                        } catch (Exception e) {
                            Log.e("yhq", "really query exception:" + e);
                            msIsReallyQuery = false;
                            pSubscriber.onError(e);
                        }
                    }
                });
    }

    protected Observable<QueryDeviceStatusResponse> queryDeviceStatusFromServer(final String pDeviceID) {
        Log.e("yhq", "queryDeviceStatusFromServer");
        return Observable
                .create(new Observable.OnSubscribe<QueryDeviceStatusResponse>() {
                    @Override
                    public void call(Subscriber<? super QueryDeviceStatusResponse> pSubscriber) {
                        try {
                            Log.e("yhq", " start run queryDeviceStatusFromServer");
                            String serialNum = DeviceHelper.getSerialNumberThroughControl();

                            if (TextUtils.isEmpty(pDeviceID) || TextUtils.isEmpty(serialNum)) {
                                Log.e("yhq", " queryDeviceStatusFromServer error 1");
                                pSubscriber.onError(new DeviceIDNotSetException());
                                return;
                            }

                            UserLoginConfig loginConfig = DeviceInfoManager.getInstance().getUserLoginConfig();
                            QueryDeviceStatusResponse result = null;
                            if (isAutoLogin()) {
                                Log.e("yhq", " queryDeviceStatusFromServer isAutoLogin 1");
                                // 自动登录的情况下，要把autoLogin的值1带上去
                                result = getHttpService().getDeviceStatus(pDeviceID, serialNum,
                                        loginConfig.getAutoLogin(), loginConfig.getNeedGroup());
                                Log.e("yhq", "user auto login QueryDeviceStatusResponse:"
                                        + result.toString());

                                DeviceStatus status = result.getStatus();
                                if (DeviceStatus.isStatusUnLogin(status)) {

                                    // 如果服务端状态是未登录的，但是本地还是登录的，那么这里需要清除一下本地的数据
                                    if (getConfig().isActivated()) {
                                        Log.d("yhq", "queryDeviceStatusFromServer, server status is unlogin, but local status is login, need clear local data ");

//                                        ILoginApi api = (ILoginApi) AdhocFrameFactory.getInstance().getAdhocRouter()
//                                                .build(LoginApiRoutePathConstants.PATH_LOGINAPI_LOGIN).navigation();
//                                        if (api != null) {
//                                            api.clearData();
//                                        }

                                        IUserAuthenticator authenticator = AssistantAuthenticSystem.getInstance()
                                                .getUserAuthenticator();
                                        if (authenticator != null) {
                                            authenticator.clearData();
                                        }

                                    }

//                                    Iterator<ISchoolGroupCodeRetriever> interceptors = AnnotationServiceLoader
//                                            .load(ISchoolGroupCodeRetriever.class).iterator();
//                                    if (!interceptors.hasNext()) {
//                                        Log.e("yhq", "ISchoolGroupCodeRetriever not found");
//                                        onQueryResultReturn(pSubscriber, result);
//                                        return;
//                                    }
//
//                                    // 把取回的school groupCode放在result中，返回给下一个调用点
//                                    Log.e("yhq", "retrieveGroupCode root group code:" + result.getRootCode());
//                                    ISchoolGroupCodeRetriever retriever = interceptors.next();
//                                    String schoolGroupCode = retriever.retrieveGroupCode(result.getRootCode());

                                    // 封装了 通过注入 回调上层 去获取 schoolcode 的代码，改为以下写法 -- by hyk 20200318
                                    String schoolGroupCode = getSchoolCode(result.getRootCode(), false);


                                    // 因为切换用户的时候，retrieveGroupCode有可能为空，这种情况下，重新拉一遍设备状态
                                    // 如果状态是已登录的，就直接进去了，未登录的，还要再走一遍retrieveGroupCode
                                    if (TextUtils.isEmpty(schoolGroupCode)) {
                                        Log.e("yhq", "school group code is empty");
                                        result = getHttpService().getDeviceStatus(pDeviceID, serialNum,
                                                loginConfig.getAutoLogin(), loginConfig.getNeedGroup());
                                        status = result.getStatus();
                                        if (!DeviceStatus.isStatusUnLogin(status)) {
                                            //已登录，直接向下走，
                                            result.setSelSchoolGroupCode(schoolGroupCode);
                                            onQueryResultReturn(pSubscriber, result);
                                            return;
                                        }

                                        // 未登录，再弹出retrieveGroupCode界面，再获取一次groupCode
//                                        if (TextUtils.isEmpty(loginConfig.getGroupCode())) {
//                                            schoolGroupCode = retriever.retrieveGroupCode(result.getRootCode());
//                                        } else {
//                                            schoolGroupCode = retriever.retrieveGroupCode(loginConfig
//                                                    .getGroupCode());
//                                        }
                                        schoolGroupCode = getSchoolCode(result.getRootCode(), false);
                                    }

                                    //偶发异常，强行杀进程
                                    if (schoolGroupCode.equalsIgnoreCase(result.getRootCode())) {
                                        Log.e("yhq", "retrieveGroupCode not work root " +
                                                "code:" + result.getRootCode() + " selected:" + schoolGroupCode
                                                + " quit app");
                                        sendFailedAndQuitApp(120);
                                        return;
                                    }

                                    result.setSelSchoolGroupCode(schoolGroupCode);
                                    onQueryResultReturn(pSubscriber, result);
                                    return;
                                }
                            } else {
                                result = getHttpService().getDeviceStatus(pDeviceID, serialNum);
                                Log.e("yhq", "QueryDeviceStatusResponse:" + result.toString());
                            }

                            onQueryResultReturn(pSubscriber, result);

                        } catch (Exception e) {
                            Log.e("yhq", "queryDeviceStatusFromServer error:" + e.getMessage());
                            CrashAnalytics.INSTANCE.reportException(e);
                            //查询设备状态时发现异常，如果是自动登录，并且是未激活的设备，退出
                            if (isAutoLogin()) {
                                DeviceStatus status = DeviceInfoManager.getInstance().getCurrentStatus();
                                if (DeviceStatus.isStatusUnLogin(status)) {
                                    Log.e("yhq", "auto login device status:" + status.toString());
                                    sendFailedAndQuitApp(120);
                                    return;
                                }
                            }

                            pSubscriber.onError(e);
                        }
                    }
                });
    }

    protected void onQueryResultReturn(Subscriber<? super QueryDeviceStatusResponse> pSubscriber,
                                       QueryDeviceStatusResponse result) {
        saveLoginInfo(result.getUsername(), result.getNickname());

        DeviceStatus curStatus = result.getStatus();

        //未登录状态，不要应该清除数据，会导致DeviceID被清掉
//                            if (DeviceStatus.isStatusUnLogin(curStatus)) {
//                                getConfig().clearData();
//                            }

        if (curStatus == DeviceStatus.Activated) {
            notifyLogin(getConfig().getAccountNum(), getConfig().getNickname());
        }

        mDeviceStatusListener.onDeviceStatusChanged(curStatus);
        pSubscriber.onNext(result);
        pSubscriber.onCompleted();
    }

    protected boolean isAutoLogin() {
        UserLoginConfig loginConfig = DeviceInfoManager.getInstance()
                .getUserLoginConfig();
        if (loginConfig != null && loginConfig.isAutoLogin()) {
            return true;
        }

        return false;
    }

    protected void sendFailedAndQuitApp(int pSec) {
        DeviceActivateBroadcastUtils.sendActivateFailedBroadcast();
        try {
            Log.e("yhq", "sendFailedAndQuitApp " + pSec);
            Thread.sleep(pSec * 1000);
            ActivityStackManager.INSTANCE.closeAllActivitys();
            System.exit(0);
        } catch (Exception ignored) {
        }
    }

    protected Observable<DeviceStatus> activeUser(final ActivateUserType pUserType,
                                                  final String pSchoolGroupCode,
                                                  final String pRootCode,
                                                  final String pLoginToken) {
        Log.e("yhq", "activeUser:" + pUserType.getValue());
        return Observable.create(new Observable.OnSubscribe<DeviceStatus>() {
            @Override
            public void call(Subscriber<? super DeviceStatus> pSubscriber) {
                UserLoginConfig loginConfig = DeviceInfoManager.getInstance().getUserLoginConfig();
                try {
                    String deviceID = DeviceInfoManager.getInstance().getDeviceID();
                    String serialNum = DeviceHelper.getSerialNumberThroughControl();
                    String deviceSerialNumber = DeviceHelper.getDeviceSerialNumberThroughControl();
                    if (TextUtils.isEmpty(deviceID) || TextUtils.isEmpty(serialNum)) {
                        Exception exception = new Exception("deviceID:" + deviceID + " serial " +
                                "num:" + serialNum);
                        CrashAnalytics.INSTANCE.reportException(exception);
                        pSubscriber.onError(exception);
                        return;
                    }

                    ActivateUserResponse response;

                    DeviceStatus status;
                    int retryCount = 0;

                    String schoolGroupCode = pSchoolGroupCode;

                    // 最多只试三次
                    while (true) {
                        retryCount++;
                        if (loginConfig != null && loginConfig.isAutoLogin()) {
                            response = retryActivateUser(deviceID, serialNum, deviceSerialNumber, schoolGroupCode,
                                    pUserType, pLoginToken, loginConfig.getActivateRealType());
                        } else {
                            response = getHttpService().activateUser(deviceID, serialNum, deviceSerialNumber, pUserType, pLoginToken);
                        }

                        if (response == null) {
                            Thread.sleep(5000);
                            continue;
                        }

                        // 检查 激活结果 是否正确，如果返回的不为 null ，需要通知 上层，重新激活
                        // 如果是null 说明 学校没找到
                        // 注意，这里面如果是通过抛异常上来的，就有可能直接中断了
                        status = queryActivateResult(3, deviceID, response.getRequestid());
                        if (status != null) {
                            pSubscriber.onNext(status);
                            pSubscriber.onCompleted();
                            return;
                        }

                        // 如果第四次失败，就不再去通知 选择 SchoolCode 了
                        if (retryCount > 3) {
                            break;
                        }

                        // 如果 status 是空的，表示没有查询到 学校，需要通知上层 重新选择学校，并且传递回新的 SchoolGroupCode
                        schoolGroupCode = getSchoolCode(pRootCode, true);

                        if (TextUtils.isEmpty(schoolGroupCode)) {
                            Logger.e("yhq", "activeUser getSchoolCode return empty");
                            break;
                        }

                        Thread.sleep(5000);
                    }

                    // 试了几次之后都还是失败的，那就直接报错
                    pSubscriber.onError(new Exception("activeUser unsuccessful，times reached!"));

//                    queryActivateResultUntilTimesReach(3, deviceID, response.getRequestid(),
//                            pSubscriber);

                } catch (Exception e) {
                    // 如果激活用户异常，要把本地的状态更改成Init状态
                    DeviceInfoManager.getInstance().setCurrentStatus(DeviceStatus.Init);
                    Log.e("yhq", "activate user error:" + e.getMessage());
                    CrashAnalytics.INSTANCE.reportException(e);
                    if (isAutoLogin()) {
                        sendFailedAndQuitApp(120);
                        // 这里为了保持和 queryActivateResult 里面的逻辑保持一致，所以加上 return -- by hyk 20200317
                        return;
                    }
                    pSubscriber.onError(e);
                }
            }
        });
    }

    private String getSchoolCode(String pRootCode, boolean isSchoolNotFound) throws Exception {
        Iterator<ISchoolGroupCodeRetriever> interceptors = AnnotationServiceLoader
                .load(ISchoolGroupCodeRetriever.class).iterator();
        if (!interceptors.hasNext()) {
            Logger.e("yhq", "getSchoolCode, ISchoolGroupCodeRetriever not found");
            return null;
        }

        // 把取回的school groupCode放在result中，返回给下一个调用点
        Log.e("yhq", "retrieveGroupCode root group code:" + pRootCode);
        ISchoolGroupCodeRetriever retriever = interceptors.next();
        UserLoginConfig loginConfig = DeviceInfoManager.getInstance().getUserLoginConfig();

        String realRootCode;

        if (loginConfig==null || TextUtils.isEmpty(loginConfig.getGroupCode())) {
            realRootCode = pRootCode;
        } else {
            realRootCode = loginConfig.getGroupCode();
        }

        String schoolGroupCode;
        if (isSchoolNotFound) {
            schoolGroupCode = retriever.onGroupNotFound(realRootCode);
        } else {
            schoolGroupCode = retriever.retrieveGroupCode(realRootCode);
        }


        // 把取回的school groupCode放在result中，返回给下一个调用点
//        Logger.e("yhq", "getSchoolCode, root group code:" + pRootCode);
//        ISchoolGroupCodeRetriever retriever = interceptors.next();
//        return retriever.retrieveGroupCode(pRootCode);
        return schoolGroupCode;
    }

    //自动登录的情况下，需要把realtype传上去，重试三次，因为大量请求的情况下，激活有可能失败
    private ActivateUserResponse retryActivateUser(String pDeviceID, String pSerialNum,
                                                   String pDeviceSerialNumber,
                                                   String pSchoolGroupCode,
                                                   ActivateUserType pUserType, String pLoginToken,
                                                   int pActivateRealType) throws Exception {
        //自动登录的情况下，需要把realtype传上去，重试三次，因为大
        Log.e("yhq", "retryActivateUser school group code:" + pSchoolGroupCode);
        final int RetryTime = 3;
        for (int i = 0; i < RetryTime; i++) {
            ActivateUserResponse response = null;
            try {
                response = getHttpService().activateUser(pDeviceID,
                        pSerialNum, pDeviceSerialNumber,pSchoolGroupCode, pUserType, pLoginToken, pActivateRealType);
                if (response != null && response.getErrcode() == 0) {
                    return response;
                }
            } catch (Exception pE) {
                pE.printStackTrace();
            }

            try {
                if (response != null && response.getErrcode() == -1) {
                    int delayTime = getRetrySleepSec(response.getDelayTime()) * 1000;
                    Log.e("yhq", "wait to activate :" + delayTime);
                    Thread.sleep(delayTime);
                }

            } catch (Exception pE) {
                pE.printStackTrace();
            }
        }
        ActivityStackManager.INSTANCE.closeAllActivitys();
        System.exit(0);
        return null;
    }

    protected void saveLoginInfo(String pUserName, String pNickName) {
        getConfig().saveAccountNum(pUserName);
        getConfig().addAccountNameToPreviousList(pUserName);
        getConfig().saveNickname(pNickName);
    }

    private int getRetrySleepSec(int limit) {
        Random r = new Random();
        return r.nextInt(limit) + 20;
    }

    protected DeviceStatus queryActivateResult(int pTimes, String pDeviceID, String pRequestID) throws Exception {
        Log.e("yhq", "queryActivateResultUntilTimesReach");
        for (int i = 0; i < pTimes; i++) {
            try {
                Thread.sleep((i * 3 + 1) * 1000);
            } catch (InterruptedException ignored) {
            }

            GetActivateUserResultResponse queryResult = null;
            // 去服务端查询
            try {
                queryResult = getHttpService()
                        .queryActivateResult(pDeviceID, pRequestID);
            } catch (Exception e) {
                CrashAnalytics.INSTANCE.reportException(new Exception("queryActivateResult error " + e));
            }

            // 结果为空，继续尝试
            if (queryResult == null){
                continue;
            }

            // 失败了
            if (!queryResult.isSuccess()) {
                // 自动登录的情况下，并且组织不存在，才返回空，去通知重新选择组织，否则直接通知失败
                if (isAutoLogin() && queryResult.isGroupNotFound()) {
                    // 学校不存在的错误，直接返回
                    return null;
                }

                Logger.e("yhq", "GetActivateUserResultResponse:" + queryResult);
                // 激活中，就去重试
                if (queryResult.isActivateStillProcessing()) {
                    CrashAnalytics.INSTANCE.reportException(new Exception("queryActivateResult not finish, still processing"));
                    continue;
                }

                // 其他错误，原先是在这里判断 后去反馈 和 自杀，现在统一改为抛异常给调用方 统一处理。
//                if (isAutoLogin()) {
//                    DeviceActivateBroadcastUtils.sendActivateFailedBroadcast();
//                    sendFailedAndQuitApp(120);
//                    return;
//                }

                throw new QueryActivateUserResultException(queryResult.getMsgcode());
//                CrashAnalytics.INSTANCE.reportException(exception);
//                pSubscriber.onError(exception);
//                return true;
            }

            // 如果是自动登录，并且 查询结果是成功的， 但是 status 码 又是 未登录，可能就是出现了某些奇妙的异常？
            // 这里就直接自杀
            if (isAutoLogin()) {
                // 如果登录成功了
                if (DeviceStatus.isStatusUnLogin(queryResult.getStatus())) {
                    Log.e("yhq", "quit after get activate result");
                    sendFailedAndQuitApp(120);
                    return null;
                }
                // 这里原来是去发广播通知外面成功，但是由于这个判断整体 提前了，所以 把广播 也 挪出去做了
                // 否则 发出去以后 以下的 保存数据还没执行完，OMO 就去获取 ，会有问题
//                else {
//                    DeviceActivateBroadcastUtils.sendActivateSuccessBroadcast();
////                    Context context = AdhocBasicConfig.getInstance().getAppContext();
////                    Intent intent = new Intent();
////                    intent.setAction("com.nd.sdp.adhoc.main.ui.login.activated");
////                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//                }
            }
            saveLoginInfo(queryResult.getUsername(), queryResult.getNickname());
            getConfig().saveUserID(queryResult.getUserid());
            notifyLogin(queryResult.getUsername(), queryResult.getNickname());

            // 这个广播是从上面挪下来的，是给 OMO 用的，不知道当初为什么 上面已经有 登录的通知了，这里又要加一个 广播？
            // 反正现在统一都发，OMO 那边如果没注册 也不会收到，所以没有影响，否则 可能发出去以后，
            DeviceActivateBroadcastUtils.sendActivateSuccessBroadcast();

            mDeviceStatusListener.onDeviceStatusChanged(queryResult.getStatus());

            return queryResult.getStatus();
        }

        // 试了三次还是失败的话，就抛异常
        Log.e("yhq", "queryActivateResultUntilTimesReach times reached");
        throw new QueryActivateUserTimeoutException();

//        if (isAutoLogin()) {
//            Log.e("yhq", "queryActivateResultUntilTimesReach times reached");
//            DeviceActivateBroadcastUtils.sendActivateFailedBroadcast();
//            sendFailedAndQuitApp(120);
//            return;
//        }
//
//        return false;
    }

    protected void bindPushIDToDeviceID() throws Exception {
        IPushModule module = MdmTransferFactory.getPushModel();
        String pushID = module.getDeviceId();
        String existPushID = getConfig().getPushID();

        Log.e("yhq", "push id:" + pushID + " exist push id:" + existPushID);
        if (TextUtils.isEmpty(pushID)) {
            Exception exception = new Exception("get push id from push module return empty");
            CrashAnalytics.INSTANCE.reportException(exception);
            throw exception;
        }

        if (pushID.equalsIgnoreCase(existPushID)) {
            Log.e("yhq", "notify pushid exist:" + existPushID);
            DeviceInfoManager.getInstance().notifyPushID(pushID);
            return;
        }

        //Push ID 不一样以后，要先清理掉本地的PushID
        getConfig().clearPushID();
        String deviceID = DeviceInfoManager.getInstance().getDeviceID();

        getHttpService().bindDeviceIDToPushID(deviceID, pushID);
        getConfig().savePushID(pushID);
        Log.e("yhq", "notify pushid after bind:" + pushID);
        DeviceInfoManager.getInstance().notifyPushID(pushID);
    }
}
