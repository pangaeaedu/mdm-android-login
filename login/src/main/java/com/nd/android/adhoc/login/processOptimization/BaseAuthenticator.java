package com.nd.android.adhoc.login.processOptimization;


import android.text.TextUtils;
import android.util.Log;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceHelper;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceInfoManager;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;
import com.nd.adhoc.assistant.sdk.deviceInfo.UserLoginConfig;
import com.nd.android.adhoc.basic.frame.api.user.IAdhocLoginStatusNotifier;
import com.nd.android.adhoc.basic.frame.constant.AdhocRouteConstant;
import com.nd.android.adhoc.basic.frame.factory.AdhocFrameFactory;
import com.nd.android.adhoc.basic.log.CrashAnalytics;
import com.nd.android.adhoc.basic.log.Logger;
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


    protected Observable<QueryDeviceStatusResponse> queryDeviceStatusFromServer(final String pDeviceID) {
        Log.e("yhq", "queryDeviceStatusFromServer");
        return Observable
                .create(new Observable.OnSubscribe<QueryDeviceStatusResponse>() {
                    @Override
                    public void call(Subscriber<? super QueryDeviceStatusResponse> pSubscriber) {
                        try {
                            String serialNum = DeviceHelper.getSerialNumberThroughControl();

                            if (TextUtils.isEmpty(pDeviceID) || TextUtils.isEmpty(serialNum)) {
                                pSubscriber.onError(new DeviceIDNotSetException());
                                return;
                            }

                            UserLoginConfig loginConfig = DeviceInfoManager.getInstance().getUserLoginConfig();
                            QueryDeviceStatusResponse result = null;
                            if (isAutoLogin()) {
                                // 自动登录的情况下，要把autoLogin的值1带上去
                                result = getHttpService().getDeviceStatus(pDeviceID, serialNum,
                                        loginConfig.getAutoLogin(), loginConfig.getNeedGroup());
                                Log.e("yhq", "user auto login QueryDeviceStatusResponse:"
                                        + result.toString());

                                DeviceStatus status = result.getStatus();
                                if (DeviceStatus.isStatusUnLogin(status)) {
                                    Iterator<ISchoolGroupCodeRetriever> interceptors = AnnotationServiceLoader
                                            .load(ISchoolGroupCodeRetriever.class).iterator();
                                    if (!interceptors.hasNext()) {
                                        Log.e("yhq", "ISchoolGroupCodeRetriever not found");
                                        onQueryResultReturn(pSubscriber, result);
                                        return;
                                    }

                                    // 把取回的school groupCode放在result中，返回给下一个调用点
                                    Log.e("yhq", "retrieveGroupCode root group code:" + result.getRootCode());
                                    String schoolGroupCode = "";
                                    ISchoolGroupCodeRetriever retriever = interceptors.next();
                                    if (TextUtils.isEmpty(loginConfig.getGroupCode())) {
                                        schoolGroupCode = retriever.retrieveGroupCode(result.getRootCode());
                                    } else {
                                        schoolGroupCode = retriever.retrieveGroupCode(loginConfig
                                                .getGroupCode());
                                    }

                                    // 因为切换用户的时候，retrieveGroupCode有可能为空，这种情况下，重新拉一遍设备状态
                                    // 如果状态是已登录的，就直接进去了，未登录的，还要再走一遍retrieveGroupCode
                                    if (TextUtils.isEmpty(schoolGroupCode)) {
                                        Log.e("yhq", "school group code is empty");
                                        result = getHttpService().getDeviceStatus(pDeviceID, serialNum,
                                                loginConfig.getAutoLogin(), loginConfig.getNeedGroup());
                                        status = result.getStatus();
                                        if (!DeviceStatus.isStatusUnLogin(status)) {
                                            //已登录，直接向下走，
                                            onQueryResultReturn(pSubscriber, result);
                                            return;
                                        }

                                        // 未登录，再弹出retrieveGroupCode界面，再获取一次groupCode
                                        if (TextUtils.isEmpty(loginConfig.getGroupCode())) {
                                            schoolGroupCode = retriever.retrieveGroupCode(result.getRootCode());
                                        } else {
                                            schoolGroupCode = retriever.retrieveGroupCode(loginConfig
                                                    .getGroupCode());
                                        }
                                    }
                                    //偶发异常，强行杀进程
                                    if (schoolGroupCode.equalsIgnoreCase(result.getRootCode())) {
                                        Log.e("yhq", "retrieveGroupCode not work root " +
                                                "code:" + result.getRootCode() + " selected:" + schoolGroupCode
                                                + " quit app");
                                        System.exit(0);
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
                                    DeviceActivateBroadcastUtils.sendActivateFailedBroadcast();
                                    quitAppAfter(120);
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

    protected void quitAppAfter(int pSec) {
        try {
            Log.e("yhq", "quitAppAfter " + pSec);
            Thread.sleep(pSec * 1000);
            System.exit(0);
        } catch (Exception pE) {
        }
    }

    protected Observable<DeviceStatus> activeUser(final ActivateUserType pUserType,
                                                  final String pSchoolGroupCode,
                                                  final String pLoginToken) {
        Log.e("yhq", "activeUser:" + pUserType.getValue());
        return Observable.create(new Observable.OnSubscribe<DeviceStatus>() {
            @Override
            public void call(Subscriber<? super DeviceStatus> pSubscriber) {
                UserLoginConfig loginConfig = DeviceInfoManager.getInstance().getUserLoginConfig();
                try {
                    String deviceID = DeviceInfoManager.getInstance().getDeviceID();
                    String serialNum = DeviceHelper.getSerialNumberThroughControl();
                    if (TextUtils.isEmpty(deviceID) || TextUtils.isEmpty(serialNum)) {
                        Exception exception = new Exception("deviceID:" + deviceID + " serial " +
                                "num:" + serialNum);
                        CrashAnalytics.INSTANCE.reportException(exception);
                        pSubscriber.onError(exception);
                        return;
                    }

                    ActivateUserResponse response = null;

                    if (loginConfig != null && loginConfig.isAutoLogin()) {
                        response = retryActivateUser(deviceID, serialNum, pSchoolGroupCode,
                                pUserType, pLoginToken, loginConfig.getActivateRealType());
                    } else {
                        response = getHttpService().activateUser(deviceID, serialNum, pUserType, pLoginToken);
                    }

                    queryActivateResultUntilTimesReach(3, deviceID, response.getRequestid(),
                            pSubscriber);
                } catch (Exception e) {
                    // 如果激活用户异常，要把本地的状态更改成Init状态
                    DeviceInfoManager.getInstance().setCurrentStatus(DeviceStatus.Init);
                    Log.e("yhq", "activate user error:" + e.getMessage());
                    CrashAnalytics.INSTANCE.reportException(e);
                    if (isAutoLogin()) {
                        DeviceActivateBroadcastUtils.sendActivateFailedBroadcast();
                        quitAppAfter(120);
                    }
                    pSubscriber.onError(e);
                }
            }
        });
    }

    //自动登录的情况下，需要把realtype传上去，重试三次，因为大量请求的情况下，激活有可能失败
    private ActivateUserResponse retryActivateUser(String pDeviceID, String pSerialNum,
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
                        pSerialNum, pSchoolGroupCode, pUserType, pLoginToken, pActivateRealType);
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

    protected void queryActivateResultUntilTimesReach(int pTimes, String pDeviceID,
                                                      String pRequestID, Subscriber<? super DeviceStatus> pSubscriber) throws Exception {
        Log.e("yhq", "queryActivateResultUntilTimesReach");
        for (int i = 0; i < pTimes; i++) {
            Thread.sleep((i * 3 + 1) * 1000);

            GetActivateUserResultResponse queryResult = getHttpService()
                    .queryActivateResult(pDeviceID, pRequestID);
            if (!queryResult.isSuccess()) {
                Logger.e("yhq", "GetActivateUserResultResponse:" + queryResult.toString());
                if (queryResult.isActivateStillProcessing()) {
                    CrashAnalytics.INSTANCE.reportException(new Exception("queryActivateResult not finish, still processing"));
                    continue;
                }


                if (isAutoLogin()) {
                    DeviceActivateBroadcastUtils.sendActivateFailedBroadcast();
                    quitAppAfter(120);
                    return;
                } else {
                    Exception exception = new QueryActivateUserResultException(queryResult.getMsgcode());
                    CrashAnalytics.INSTANCE.reportException(exception);
                    pSubscriber.onError(exception);
                    return;
                }
            }

            saveLoginInfo(queryResult.getUsername(), queryResult.getNickname());
            getConfig().saveUserID(queryResult.getUserid());
            notifyLogin(queryResult.getUsername(), queryResult.getNickname());

            mDeviceStatusListener.onDeviceStatusChanged(queryResult.getStatus());

            pSubscriber.onNext(queryResult.getStatus());
            pSubscriber.onCompleted();

            if (isAutoLogin()) {
                if (DeviceStatus.isStatusUnLogin(queryResult.getStatus())) {
                    Log.e("yhq", "quit after get activate result");
                    System.exit(0);
                } else {
                    DeviceActivateBroadcastUtils.sendActivateSuccessBroadcast();
//                    Context context = AdhocBasicConfig.getInstance().getAppContext();
//                    Intent intent = new Intent();
//                    intent.setAction("com.nd.sdp.adhoc.main.ui.login.activated");
//                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            }
            return;
        }

        if (isAutoLogin()) {
            Log.e("yhq", "queryActivateResultUntilTimesReach times reached");
            DeviceActivateBroadcastUtils.sendActivateFailedBroadcast();
            quitAppAfter(120);
            return;
        }

        pSubscriber.onError(new QueryActivateUserTimeoutException());
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
