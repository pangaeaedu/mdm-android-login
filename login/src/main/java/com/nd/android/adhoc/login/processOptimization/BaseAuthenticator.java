package com.nd.android.adhoc.login.processOptimization;


import android.text.TextUtils;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceHelper;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceInfoManager;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;
import com.nd.android.adhoc.basic.frame.api.user.IAdhocLoginStatusNotifier;
import com.nd.android.adhoc.basic.frame.constant.AdhocRouteConstant;
import com.nd.android.adhoc.basic.frame.factory.AdhocFrameFactory;
import com.nd.android.adhoc.login.basicService.data.http.ActivateUserResponse;
import com.nd.android.adhoc.login.basicService.data.http.GetActivateUserResultResponse;
import com.nd.android.adhoc.login.enumConst.ActivateUserType;
import com.nd.android.adhoc.login.info.AdhocLoginInfoImpl;
import com.nd.android.adhoc.login.info.AdhocUserInfoImpl;
import com.nd.android.adhoc.loginapi.exception.QueryActivateUserTimeoutException;

import rx.Observable;
import rx.Subscriber;

public abstract class BaseAuthenticator extends BaseAbilityProvider {

    protected IDeviceStatusListener mDeviceStatusListener = null;

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

    protected Observable<DeviceStatus> activeUser(final ActivateUserType pUserType,
                                                  final String pLoginToken) {
        return Observable.create(new Observable.OnSubscribe<DeviceStatus>() {
            @Override
            public void call(Subscriber<? super DeviceStatus> pSubscriber) {
                try {
                    ActivateUserResponse response = null;
                    String deviceID = DeviceInfoManager.getInstance().getDeviceID();
                    String serialNum = DeviceHelper.getSerialNumberThroughControl();
                    if (TextUtils.isEmpty(deviceID) || TextUtils.isEmpty(serialNum)) {
                        pSubscriber.onError(new Exception("deviceID:" + deviceID + " serial num:" + serialNum));
                        return;
                    }

                    getHttpService().activateUser(deviceID, serialNum, pUserType, pLoginToken);
                    queryActivateResultUntilTimesReach(3, deviceID, pSubscriber);
                } catch (Exception e) {
                    pSubscriber.onError(e);
                }
            }
        });
    }

    private void saveLoginInfo(String pUserName, String pNickName) {
        getConfig().saveAccountNum(pUserName);
        getConfig().saveNickname(pNickName);
    }

    protected void queryActivateResultUntilTimesReach(int pTimes, String pDeviceID,
                                                      Subscriber<? super DeviceStatus> pSubscriber) throws Exception {
        for (int i = 0; i < pTimes; i++) {
            Thread.sleep((i * 3 + 1) * 1000);

            GetActivateUserResultResponse queryResult = getHttpService().queryActivateResult(pDeviceID);
            if (!queryResult.isSuccess()) {
                if (queryResult.isActivateStillProcessing()) {
                    //TODO 加上日志上报，不应该经常出现这个Processing
                    continue;
                }

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
}
