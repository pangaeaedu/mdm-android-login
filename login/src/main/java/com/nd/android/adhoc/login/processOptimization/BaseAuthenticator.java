package com.nd.android.adhoc.login.processOptimization;


import android.text.TextUtils;
import android.util.Log;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceHelper;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceInfoManager;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;
import com.nd.android.adhoc.basic.frame.api.user.IAdhocLoginStatusNotifier;
import com.nd.android.adhoc.basic.frame.constant.AdhocRouteConstant;
import com.nd.android.adhoc.basic.frame.factory.AdhocFrameFactory;
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
                            if (curStatus == DeviceStatus.Unknown || curStatus == DeviceStatus.Enrolled) {
                                getConfig().clearData();
                            }

                            if (curStatus == DeviceStatus.Activated) {
                                notifyLogin(getConfig().getAccountNum(), getConfig().getNickname());
                            }

                            mDeviceStatusListener.onDeviceStatusChanged(curStatus);
                            pSubscriber.onNext(result);
                            pSubscriber.onCompleted();
                        } catch (Exception e) {
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
                        pSubscriber.onError(new Exception("deviceID:" + deviceID + " serial num:" + serialNum));
                        return;
                    }

                    ActivateUserResponse response = getHttpService().activateUser(deviceID,
                            serialNum, pUserType, pLoginToken);
                    queryActivateResultUntilTimesReach(3, deviceID, response.getRequestid(),
                            pSubscriber);
                } catch (Exception e) {
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
            Log.e(TAG, "GetActivateUserResultResponse:"+queryResult.toString());
            if (!queryResult.isSuccess()) {
                if (queryResult.isActivateStillProcessing()) {
                    //TODO 加上日志上报，不应该经常出现这个Processing
                    Log.e(TAG, "query not finish, still processing");
                    continue;
                }

                Log.e(TAG, "query error:"+queryResult.getMsgcode());
                //TODO 报日志
                pSubscriber.onError(new Exception("active user error:" + queryResult.getMsgcode()));
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

        Log.e(TAG, "push id:"+pushID+" exist push id:"+existPushID);
        if (TextUtils.isEmpty(pushID)) {
            // 加日志上报
            throw new Exception("get push id from push module return empty");
        }

        if (pushID.equalsIgnoreCase(existPushID)) {
            Log.e(TAG, "notify pushid exist:"+existPushID);
            DeviceInfoManager.getInstance().notifyPushID(pushID);
            return;
        }

        //Push ID 不一样以后，要先清理掉本地的PushID
        getConfig().clearPushID();
        String deviceID = DeviceInfoManager.getInstance().getDeviceID();

        getHttpService().bindDeviceIDToPushID(deviceID, pushID);
        getConfig().savePushID(pushID);
        Log.e(TAG, "notify pushid after bind:"+pushID);
        DeviceInfoManager.getInstance().notifyPushID(pushID);
    }
}
