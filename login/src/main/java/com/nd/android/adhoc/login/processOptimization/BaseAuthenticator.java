package com.nd.android.adhoc.login.processOptimization;


import android.text.TextUtils;
import android.util.Log;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceHelper;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceInfoManager;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;
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

                            QueryDeviceStatusResponse result = getHttpService()
                                    .getDeviceStatus(pDeviceID, serialNum);
                            Log.e(TAG, "QueryDeviceStatusResponse:" + result.toString());
                            saveLoginInfo(result.getUsername(), result.getNickname());

                            DeviceStatus curStatus = result.getStatus();
                            if (DeviceStatus.isStatusUnLogin(curStatus)) {
                                getConfig().clearData();
                            }

                            if (curStatus == DeviceStatus.Activated) {
                                notifyLogin(getConfig().getAccountNum(), getConfig().getNickname());
                            }

                            mDeviceStatusListener.onDeviceStatusChanged(curStatus);
                            pSubscriber.onNext(result);
                            pSubscriber.onCompleted();
                        } catch (Exception e) {
                            CrashAnalytics.INSTANCE.reportException(e);
                            pSubscriber.onError(e);
                        }
                    }
                });
    }

    protected Observable<DeviceStatus> activeUser(final ActivateUserType pUserType,
                                                  final String pLoginToken) {
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

                    ActivateUserResponse response = getHttpService().activateUser(deviceID,
                            serialNum, pUserType, pLoginToken);
                    queryActivateResultUntilTimesReach(3, deviceID, response.getRequestid(),
                            pSubscriber);
                } catch (Exception e) {
                    CrashAnalytics.INSTANCE.reportException(e);
                    pSubscriber.onError(e);
                }
            }
        });
    }

    protected void saveLoginInfo(String pUserName, String pNickName) {
        getConfig().saveAccountNum(pUserName);
        getConfig().saveNickname(pNickName);
    }

    protected void queryActivateResultUntilTimesReach(int pTimes, String pDeviceID,
                                                      String pRequestID, Subscriber<? super DeviceStatus> pSubscriber) throws Exception {
        for (int i = 0; i < pTimes; i++) {
            Thread.sleep((i * 3 + 1) * 1000);

            GetActivateUserResultResponse queryResult = getHttpService()
                    .queryActivateResult(pDeviceID, pRequestID);
            if (!queryResult.isSuccess()) {
                Logger.e(TAG, "GetActivateUserResultResponse:"+queryResult.toString());
                if (queryResult.isActivateStillProcessing()) {
                    CrashAnalytics.INSTANCE.reportException(new Exception("queryActivateResult not finish, still processing"));
                    continue;
                }

                Exception exception = new Exception("queryActivateResult error," +
                        queryResult.getMsgcode());
                CrashAnalytics.INSTANCE.reportException(exception);
                pSubscriber.onError(exception);
                return;
            }

            saveLoginInfo(queryResult.getUsername(), queryResult.getNickname());
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

        Logger.e(TAG, "push id:"+pushID+" exist push id:"+existPushID);
        if (TextUtils.isEmpty(pushID)) {
            Exception exception = new Exception("get push id from push module return empty");
            CrashAnalytics.INSTANCE.reportException(exception);
            throw exception;
        }

        if (pushID.equalsIgnoreCase(existPushID)) {
            Logger.e(TAG, "notify pushid exist:"+existPushID);
            DeviceInfoManager.getInstance().notifyPushID(pushID);
            return;
        }

        //Push ID 不一样以后，要先清理掉本地的PushID
        getConfig().clearPushID();
        String deviceID = DeviceInfoManager.getInstance().getDeviceID();

        getHttpService().bindDeviceIDToPushID(deviceID, pushID);
        getConfig().savePushID(pushID);
        Logger.e(TAG, "notify pushid after bind:"+pushID);
        DeviceInfoManager.getInstance().notifyPushID(pushID);
    }
}
