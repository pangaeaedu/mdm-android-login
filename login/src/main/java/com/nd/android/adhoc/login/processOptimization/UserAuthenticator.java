package com.nd.android.adhoc.login.processOptimization;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceInfoManager;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;
import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.frame.api.user.IAdhocLoginStatusNotifier;
import com.nd.android.adhoc.basic.frame.constant.AdhocRouteConstant;
import com.nd.android.adhoc.basic.frame.factory.AdhocFrameFactory;
import com.nd.android.adhoc.basic.ui.activity.ActivityStackManager;
import com.nd.android.adhoc.basic.util.net.AdhocNetworkUtil;
import com.nd.android.adhoc.login.basicService.data.http.QueryDeviceStatusResponse;
import com.nd.android.adhoc.login.enumConst.ActivateUserType;
import com.nd.android.adhoc.login.processOptimization.login.IUserLogin;
import com.nd.android.adhoc.login.processOptimization.login.IUserLoginResult;
import com.nd.android.adhoc.login.processOptimization.login.LoginUserOrPwdEmptyException;
import com.nd.android.adhoc.login.processOptimization.login.UserLoginThoughUC11;
import com.nd.android.adhoc.loginapi.exception.AutoLoginMeetUserLoginException;
import com.nd.android.adhoc.loginapi.exception.DeviceIDNotSetException;
import com.nd.android.adhoc.loginapi.exception.NetworkUnavailableException;
import com.nd.android.adhoc.policy.api.provider.IAdhocPolicyLifeCycleProvider;
import com.nd.android.adhoc.router_api.facade.Postcard;
import com.nd.android.adhoc.router_api.facade.callback.NavCallback;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class UserAuthenticator extends BaseAuthenticator implements IUserAuthenticator {

    public UserAuthenticator(IDeviceStatusListener pProcessor) {
        super(pProcessor);
    }

    public void logout() {
        Log.e("yhq", "logout");
        getConfig().clearData();

        IAdhocLoginStatusNotifier api = (IAdhocLoginStatusNotifier) AdhocFrameFactory.getInstance().getAdhocRouter()
                .build(AdhocRouteConstant.PATH_LOGIN_STATUS_NOTIFIER).navigation();
        if (api == null) {
            return;
        }

        api.onLogout();

        //登出的时候，不要清掉DeviceID。DeviceID只有在切换环境的时候才会被清理
        DeviceInfoManager.getInstance().resetStatusAndPushIDSubject();
        mDeviceStatusListener.onDeviceStatusChanged(DeviceStatus.Init);

        ActivityStackManager.INSTANCE.closeAllActivitys();
        enterLogoutUI();

        clearPolicy();
    }

    private void clearPolicy() {
        Log.d("yhq", "clearPolicy");
        Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> pSubscriber) {
                        try {
                            IAdhocPolicyLifeCycleProvider policyLifeCycleProvider =
                                    (IAdhocPolicyLifeCycleProvider) AdhocFrameFactory.getInstance()
                                            .getAdhocRouter().build(IAdhocPolicyLifeCycleProvider.ROUTE_PATH).navigation();
                            if (policyLifeCycleProvider != null) {
                                Log.d("yhq", "IAdhocPolicyLifeCycleProvider clearPolicy");
                                policyLifeCycleProvider.clearPolicy();
                                pSubscriber.onNext(null);
                                pSubscriber.onCompleted();
                            } else {
                                Log.d("yhq", "IAdhocPolicyLifeCycleProvider not found");
                            }
                        } catch (Exception pE) {
                            pSubscriber.onError(pE);
                        }

                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Void pVoid) {

                    }
                });

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

    @NonNull
    private IUserLogin getLogin() {
//        return new UserLoginThroughServer();
        return new UserLoginThoughUC11();
    }

    public Observable<DeviceStatus> login(@NonNull final String pUserName,
                                          @NonNull final String pPassword) {
        return login(pUserName, pPassword, "");
    }

    public Observable<DeviceStatus> login(@NonNull final String pUserName,
                                          @NonNull final String pPassword,
                                          final String pValidationCode) {
        Log.e("yhq", "login");
        final String deviceID = DeviceInfoManager.getInstance().getDeviceID();
        if (TextUtils.isEmpty(deviceID)) {
            return Observable.error(new DeviceIDNotSetException());
        }

        Context context = AdhocBasicConfig.getInstance().getAppContext();
        if (!AdhocNetworkUtil.isNetWrokAvaiable(context)) {
            return Observable.error(new NetworkUnavailableException());
        }

        DeviceStatus status = DeviceInfoManager.getInstance().getCurrentStatus();
        if (status == DeviceStatus.Init) {
            return queryDeviceStatusThenLogin(deviceID, pUserName, pPassword);
        }

        if (TextUtils.isEmpty(pUserName) || TextUtils.isEmpty(pPassword)) {
            return Observable.error(new LoginUserOrPwdEmptyException());
        }

        Log.e("yhq", "direct login");
        return getLogin().login(pUserName, pPassword, pValidationCode)
                .flatMap(new Func1<IUserLoginResult, Observable<DeviceStatus>>() {
                    @Override
                    public Observable<DeviceStatus> call(IUserLoginResult pResult) {
                        return activeUser(ActivateUserType.Uc, "", pResult.getLoginToken());
                    }
                });
    }

    private Observable<DeviceStatus> queryDeviceStatusThenLogin(String pDeviceID,
                                                                final String pUserName,
                                                                final String pPassword){
        Log.e("yhq", "queryDeviceStatusThenLogin");
        return queryDeviceStatusFromServer(pDeviceID)
                .flatMap(new Func1<QueryDeviceStatusResponse, Observable<DeviceStatus>>() {
                    @Override
                    public Observable<DeviceStatus> call(final QueryDeviceStatusResponse pResponse) {
                        if (pResponse.isAutoLogin() && pResponse.getStatus() == DeviceStatus.Enrolled) {
                            return activeUser(ActivateUserType.AutoLogin, pResponse
                                    .getSelSchoolGroupCode(), "")
                                    .flatMap(new Func1<DeviceStatus, Observable<DeviceStatus>>() {
                                        @Override
                                        public Observable<DeviceStatus> call(DeviceStatus pStatus) {
                                            if (TextUtils.isEmpty(pResponse.getJobnum())) {
                                                return Observable.error(new
                                                        AutoLoginMeetUserLoginException(""));
                                            } else {
                                                return Observable.error(new
                                                        AutoLoginMeetUserLoginException
                                                        ("" + pResponse.getJobnum()));
                                            }
                                        }
                                    });
                        }

                        return getLogin().login(pUserName, pPassword)
                                .flatMap(new Func1<IUserLoginResult, Observable<DeviceStatus>>() {
                                    @Override
                                    public Observable<DeviceStatus> call(IUserLoginResult pResult) {
                                        return activeUser(ActivateUserType.Uc, "", pResult
                                                .getLoginToken());
                                    }
                                });
                    }
                });
    }

}
