package com.nd.android.adhoc.login.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceInfoManager;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;
import com.nd.android.adhoc.basic.frame.factory.AdhocFrameFactory;
import com.nd.android.adhoc.basic.log.CrashAnalytics;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.login.LoginRoutePathConstants;
import com.nd.android.adhoc.login.basicService.data.http.GetUserInfoResponse;
import com.nd.android.adhoc.login.exception.GetUserInfoServerException;
import com.nd.android.adhoc.login.processOptimization.AssistantAuthenticSystem;
import com.nd.android.adhoc.login.processOptimization.BaseAbilityProvider;
import com.nd.android.adhoc.login.processOptimization.IDeviceInitiator;
import com.nd.android.adhoc.login.processOptimization.IUserAuthenticator;
import com.nd.android.adhoc.loginapi.ILoginApi;
import com.nd.android.adhoc.loginapi.LoginApiRoutePathConstants;
import com.nd.android.adhoc.loginapi.exception.AutoLoginMeetUserLoginException;
import com.nd.android.adhoc.router_api.facade.Postcard;
import com.nd.android.adhoc.router_api.facade.annotation.Route;
import com.nd.android.adhoc.router_api.facade.callback.NavCallback;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

@Route(path = LoginRoutePathConstants.PATH_LOGIN_LOGIN)
public class LoginApiImpl extends BaseAbilityProvider implements ILoginApi {
    private static final String TAG = "LoginApiImpl";
    @Override
    public void enterLoginUI(@NonNull Context pContext) {
        AdhocFrameFactory.getInstance().getAdhocRouter()
                .build(LoginApiRoutePathConstants.PATH_LOGINAPI_LOGINUI)
                .navigation(pContext, new NavCallback() {
                    @Override
                    public void onInterrupt(@NonNull Postcard postcard) {
                        super.onInterrupt(postcard);
                        Logger.w(TAG, "onInterrupt");
                    }

                    @Override
                    public void onLost(@NonNull Postcard postcard) {
                        super.onLost(postcard);
                        Logger.e(TAG, "onLost");
                    }

                    @Override
                    public void onArrival(@NonNull Postcard postcard) {
                    }
                });
    }

    @Override
    public void logout() {
        IUserAuthenticator authenticator = AssistantAuthenticSystem.getInstance()
                .getUserAuthenticator();
        if(authenticator == null){
            return;
        }

        authenticator.logout();
    }

    @Override
    public Observable<DeviceStatus> login(@NonNull final String pUserName, @NonNull final String pPassword) {
        //如果device id没有设置上去，说明初始化没有完成，则要先走一次初始化的动作
        if (TextUtils.isEmpty(DeviceInfoManager.getInstance().getDeviceID())) {
            IDeviceInitiator initiator = AssistantAuthenticSystem.getInstance()
                    .getDeviceInitiator();

            return initiator.init()
                    .flatMap(new Func1<DeviceStatus, Observable<DeviceStatus>>() {
                        @Override
                        public Observable<DeviceStatus> call(DeviceStatus pStatus) {
                            if(DeviceStatus.isStatusUnLogin(pStatus)) {
                                IUserAuthenticator authenticator = AssistantAuthenticSystem.getInstance()
                                        .getUserAuthenticator();
                                return authenticator.login(pUserName, pPassword);
                            }

                            return Observable.error(new AutoLoginMeetUserLoginException("unknown"));
                        }
                    });
        }


        IUserAuthenticator authenticator = AssistantAuthenticSystem.getInstance()
                .getUserAuthenticator();
        return authenticator.login(pUserName, pPassword);
    }

    @Override
    public Observable<String> getNickName() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> pSubscriber) {
                String nickname = getConfig().getNickname();
                if (!TextUtils.isEmpty(nickname)) {
                    pSubscriber.onNext(nickname);
                    pSubscriber.onCompleted();
                    return;
                }

                try {
                    String deviceID = DeviceInfoManager.getInstance().getDeviceID();
                    GetUserInfoResponse response = getHttpService().getUserInfo(deviceID);

                    if (response.isSuccess()) {
                        getConfig().saveNickname(response.getNickName());
                        pSubscriber.onNext(response.getNickName());
                        pSubscriber.onCompleted();
                        return;
                    }

                    GetUserInfoServerException exception = new GetUserInfoServerException("" +
                            response.getMsgcode());
                    CrashAnalytics.INSTANCE.reportException(exception);
                    pSubscriber.onError(exception);
                } catch (Exception pE) {
                    CrashAnalytics.INSTANCE.reportException(pE);
                    pSubscriber.onError(pE);
                }

            }
        });
    }

    @Override
    public void init(@NonNull Context pContext) {

    }
}
