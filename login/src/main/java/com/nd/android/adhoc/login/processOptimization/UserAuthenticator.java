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
import com.nd.android.adhoc.basic.ui.activity.ActivityStackManager;
import com.nd.android.adhoc.basic.util.net.AdhocNetworkUtil;
import com.nd.android.adhoc.login.enumConst.ActivateUserType;
import com.nd.android.adhoc.login.processOptimization.login.IUserLogin;
import com.nd.android.adhoc.login.processOptimization.login.IUserLoginResult;
import com.nd.android.adhoc.login.processOptimization.login.LoginUserOrPwdEmptyException;
import com.nd.android.adhoc.login.processOptimization.login.UserLoginThroughServer;
import com.nd.android.adhoc.loginapi.exception.DeviceIDNotSetException;
import com.nd.android.adhoc.loginapi.exception.NetworkUnavailableException;
import com.nd.android.adhoc.router_api.facade.Postcard;
import com.nd.android.adhoc.router_api.facade.callback.NavCallback;

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

        ActivityStackManager.INSTANCE.closeAllActivitys();
        enterLogoutUI();
    }

    private void enterLogoutUI() {
        Context context = AdhocBasicConfig.getInstance().getAppContext();
        AdhocFrameFactory.getInstance().getAdhocRouter().build(AdhocRouteConstant.PATH_AFTER_LOGOUT)
                .navigation(context, new NavCallback() {
                    @Override
                    public void onInterrupt(@NonNull Postcard postcard) {
                        super.onInterrupt(postcard);
                    }

                    @Override
                    public void onLost(@NonNull Postcard postcard) {
                        super.onLost(postcard);
                    }

                    @Override
                    public void onArrival(@NonNull Postcard postcard) {
                    }
                });
    }

//    private Observable<DeviceStatus> onActivateDeviceSuccess(String pUsername,
//                                                             String pNickName,
//                                                             IQueryActivateResult pResult){
//        DeviceStatus status = pResult.getDeviceStatus();
//        saveLoginInfo(pUsername, pNickName);
//        notifyLogin(pUsername, pNickName);
//        mDeviceStatusListener.onDeviceStatusChanged(status);
//
//        return Observable.just(status);
//    }
//
//    private Observable<DeviceStatus> onActiveDeviceFailed(IQueryActivateResult pResult){
//        ActivateUserError error = pResult.getActivateError();
//        Exception activateException = LoginExceptionUtils.convertErrorToException(error);
//        return Observable.error(activateException);
//    }

//    private Observable<DeviceStatus> queryActivateResultUntilTimesReach(int pTimes, String pDeviceID,
//                                                                        IUserLoginResult pResult) throws Exception {
//        String username = pResult.getUsername();
//        String nickname = pResult.getNickname();
//
//        for (int i = 0; i < pTimes; i++) {
//            Thread.sleep((i * 3 + 1) * 1000);
//
//            IQueryActivateResult queryResult = getHttpService().queryActivateResult(pDeviceID);
//            if (!queryResult.isSuccess()) {
//                if (queryResult.getActivateError() == ActivateUserError.Processing) {
//                    //TODO 加上日志上报，不应该经常出现这个Processing
//                    continue;
//                }
//
//                //TODO 报日志
//                return onActiveDeviceFailed(queryResult);
//            } else {
//                return onActivateDeviceSuccess(username, nickname, queryResult);
//            }
//        }
//
//        return Observable.error(new QueryActivateUserTimeoutException());
//    }

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
        final String deviceID =  DeviceInfoManager.getInstance().getDeviceID();
        if(TextUtils.isEmpty(deviceID)){
            return Observable.error(new DeviceIDNotSetException());
        }

        Context context = AdhocBasicConfig.getInstance().getAppContext();
        if (!AdhocNetworkUtil.isNetWrokAvaiable(context)) {
            return Observable.error(new NetworkUnavailableException());
        }

        if (TextUtils.isEmpty(pUserName) || TextUtils.isEmpty(pPassword)) {
            return Observable.error(new LoginUserOrPwdEmptyException());
        }

        return getLogin().login(pUserName, pPassword)
                .flatMap(new Func1<IUserLoginResult, Observable<DeviceStatus>>() {
                    @Override
                    public Observable<DeviceStatus> call(IUserLoginResult pResult) {
                        return activeUser(ActivateUserType.Uc, pResult.getLoginToken());
                    }
                });

    }


}
