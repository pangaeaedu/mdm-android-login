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
import com.nd.android.adhoc.loginapi.exception.DeviceIDNotSetException;
import com.nd.android.adhoc.loginapi.exception.QueryActivateUserResultException;
import com.nd.android.adhoc.loginapi.exception.QueryActivateUserTimeoutException;

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


    protected Observable<QueryDeviceStatusResponse> queryDeviceStatusFromServer(final String pDeviceID){
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
                            if (loginConfig != null && loginConfig.isAutoLogin()) {
                                // 自动登录的情况下，要把autoLogin的值1带上去
                                result = getHttpService().getDeviceStatus(pDeviceID, serialNum, loginConfig.getAutoLogin());
                                Log.e("yhq", "user auto login QueryDeviceStatusResponse:"
                                        + result.toString());
                            } else {
                                result = getHttpService().getDeviceStatus(pDeviceID, serialNum);
                                Log.e("yhq", "QueryDeviceStatusResponse:" + result.toString());
                            }

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
                        } catch (Exception e) {
                            Log.e("yhq", "queryDeviceStatusFromServer error:"+e.getMessage());
                            CrashAnalytics.INSTANCE.reportException(e);
                            pSubscriber.onError(e);
                        }
                    }
                });
    }

    protected Observable<DeviceStatus> activeUser(final ActivateUserType pUserType,
                                                  final String pLoginToken) {
        Log.e("yhq", "activeUser:" + pUserType.getValue());
        return Observable.create(new Observable.OnSubscribe<DeviceStatus>() {
            @Override
            public void call(Subscriber<? super DeviceStatus> pSubscriber) {
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
                    UserLoginConfig loginConfig = DeviceInfoManager.getInstance().getUserLoginConfig();
                    if (loginConfig != null && loginConfig.isAutoLogin()) {
                        retryActivateUser(deviceID, serialNum, pUserType, pLoginToken,
                                loginConfig.getActivateRealType());
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
                    pSubscriber.onError(e);
                }
            }
        });
    }

    //自动登录的情况下，需要把realtype传上去，重试三次，因为大量请求的情况下，激活有可能失败
    private ActivateUserResponse retryActivateUser(String pDeviceID, String pSerialNum,
                                                   ActivateUserType pUserType, String pLoginToken,
                                                   int pActivateRealType) throws Exception {
        //自动登录的情况下，需要把realtype传上去，重试三次，因为大
        final int RetryTime = 3;
        for (int i = 0; i < RetryTime; i++) {
            try {
                ActivateUserResponse response = getHttpService().activateUser(pDeviceID,
                        pSerialNum, pUserType, pLoginToken, pActivateRealType);
                return response;
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

    protected void queryActivateResultUntilTimesReach(int pTimes, String pDeviceID,
                                                      String pRequestID, Subscriber<? super DeviceStatus> pSubscriber) throws Exception {
        Log.e("yhq", "queryActivateResultUntilTimesReach");
        for (int i = 0; i < pTimes; i++) {
            Thread.sleep((i * 3 + 1) * 1000);

            GetActivateUserResultResponse queryResult = getHttpService()
                    .queryActivateResult(pDeviceID, pRequestID);
            if (!queryResult.isSuccess()) {
                Logger.e("yhq", "GetActivateUserResultResponse:"+queryResult.toString());
                if (queryResult.isActivateStillProcessing()) {
                    CrashAnalytics.INSTANCE.reportException(new Exception("queryActivateResult not finish, still processing"));
                    continue;
                }


                Exception exception = new QueryActivateUserResultException(queryResult.getMsgcode());;
                CrashAnalytics.INSTANCE.reportException(exception);
                pSubscriber.onError(exception);
                return;
            }

            saveLoginInfo(queryResult.getUsername(), queryResult.getNickname());
            getConfig().saveUserID(queryResult.getUserid());
            notifyLogin(queryResult.getUsername(), queryResult.getNickname());

            mDeviceStatusListener.onDeviceStatusChanged(queryResult.getStatus());
            pSubscriber.onNext(queryResult.getStatus());
            pSubscriber.onCompleted();
            return;
        }

        pSubscriber.onError(new QueryActivateUserTimeoutException());
    }

    protected void bindPushIDToDeviceID() throws Exception {
        IPushModule module = MdmTransferFactory.getPushModel();
        String pushID = module.getDeviceId();
        String existPushID = getConfig().getPushID();

        Log.e("yhq", "push id:"+pushID+" exist push id:"+existPushID);
        if (TextUtils.isEmpty(pushID)) {
            Exception exception = new Exception("get push id from push module return empty");
            CrashAnalytics.INSTANCE.reportException(exception);
            throw exception;
        }

        if (pushID.equalsIgnoreCase(existPushID)) {
            Log.e("yhq", "notify pushid exist:"+existPushID);
            DeviceInfoManager.getInstance().notifyPushID(pushID);
            return;
        }

        //Push ID 不一样以后，要先清理掉本地的PushID
        getConfig().clearPushID();
        String deviceID = DeviceInfoManager.getInstance().getDeviceID();

        getHttpService().bindDeviceIDToPushID(deviceID, pushID);
        getConfig().savePushID(pushID);
        Log.e("yhq", "notify pushid after bind:"+pushID);
        DeviceInfoManager.getInstance().notifyPushID(pushID);
    }
}
