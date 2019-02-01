package com.nd.android.adhoc.login.processOptimization;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceInfoManager;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;
import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.frame.api.user.IAdhocLoginStatusNotifier;
import com.nd.android.adhoc.basic.frame.constant.AdhocRouteConstant;
import com.nd.android.adhoc.basic.frame.factory.AdhocFrameFactory;
import com.nd.android.adhoc.basic.util.net.AdhocNetworkUtil;
import com.nd.android.adhoc.login.basicService.http.IQueryActivateResult;
import com.nd.android.adhoc.login.exception.UserNullException;
import com.nd.android.adhoc.loginapi.exception.LoginNetworkUnavailableException;
import com.nd.android.adhoc.login.processOptimization.login.LoginUserOrPwdEmptyException;
import com.nd.android.adhoc.loginapi.exception.QueryActivateUserTimeoutException;
import com.nd.android.adhoc.login.processOptimization.login.IUserLogin;
import com.nd.android.adhoc.login.processOptimization.login.IUserLoginResult;
import com.nd.android.adhoc.login.processOptimization.login.UserLoginThroughServer;
import com.nd.android.adhoc.login.processOptimization.utils.LoginExceptionUtils;

import rx.Observable;
import rx.functions.Func1;

public class UserAuthenticator extends BaseAuthenticator implements IUserAuthenticator {

    public UserAuthenticator(IDeviceStatusListener pProcessor) {
        super(pProcessor);
    }

    public void logout() {
        getConfig().clearData();

        IAdhocLoginStatusNotifier api = (IAdhocLoginStatusNotifier) AdhocFrameFactory.getInstance().getAdhocRouter()
                .build(AdhocRouteConstant.PATH_LOGIN_STATUS_NOTIFIER).navigation();
        if (api == null) {
            return;
        }

        api.onLogout();

        mDeviceStatusListener.onDeviceStatusChanged(DeviceStatus.Enrolled);
    }

    private Observable<DeviceStatus> onActivateDeviceSuccess(String pUsername,
                                                             String pNickName,
                                                             IQueryActivateResult pResult){
        DeviceStatus status = pResult.getDeviceStatus();
        saveLoginInfo(pUsername, pNickName);
        notifyLogin(pUsername, pNickName);
        mDeviceStatusListener.onDeviceStatusChanged(status);

        return Observable.just(status);
    }

    private Observable<DeviceStatus> onActiveDeviceFailed(IQueryActivateResult pResult){
        ActivateUserError error = pResult.getActivateError();
        Exception activateException = LoginExceptionUtils.convertErrorToException(error);
        return Observable.error(activateException);
    }

    private Observable<DeviceStatus> queryActivateResultUntilTimesReach(int pTimes, String pDeviceID,
                                                                        IUserLoginResult pResult) throws Exception {
        String username = pResult.getUsername();
        String nickname = pResult.getNickname();

        for (int i = 0; i < pTimes; i++) {
            Thread.sleep(i * 3 + 3);

            IQueryActivateResult queryResult = getHttpService().queryActivateResult(pDeviceID);
            if(!queryResult.isSuccess()){
                if (queryResult.getActivateError() == ActivateUserError.Processing) {
                    continue;
                }
                return onActiveDeviceFailed(queryResult);
            } else {
                return onActivateDeviceSuccess(username, nickname, queryResult);
            }
        }

        return Observable.error(new QueryActivateUserTimeoutException());
    }

    private void saveLoginInfo(String pUserName, String pNickName) {
        getConfig().saveAccountNum(pUserName);
        getConfig().saveNickname(pNickName);
    }

    @NonNull
    private IUserLogin getLogin() {
        return new UserLoginThroughServer();
    }

    public Observable<DeviceStatus> login(@NonNull final String pUserName,
                                          @NonNull final String pPassword) {
        Context context = AdhocBasicConfig.getInstance().getAppContext();
        if (!AdhocNetworkUtil.isNetWrokAvaiable(context)) {
            return Observable.error(new LoginNetworkUnavailableException());
        }

        if (TextUtils.isEmpty(pUserName) || TextUtils.isEmpty(pPassword)) {
            return Observable.error(new LoginUserOrPwdEmptyException());
        }

        return getLogin().login(pUserName, pPassword)
                .flatMap(new Func1<IUserLoginResult, Observable<DeviceStatus>>() {
                    @Override
                    public Observable<DeviceStatus> call(IUserLoginResult pResult) {
                        try {
                            if (pResult == null
                                    || TextUtils.isEmpty(pResult.getLoginToken())) {
                                return Observable.error(new UserNullException());
                            }

                            String deviceID =  DeviceInfoManager.getInstance().getDeviceID();
                            getHttpService().activateUser(pResult.getLoginToken(), deviceID);

                            return queryActivateResultUntilTimesReach(3, deviceID, pResult);
                        } catch (Exception e) {
                            return Observable.error(e);
                        }
                    }
                });

    }


}
