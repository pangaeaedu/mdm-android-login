package com.nd.android.adhoc.login;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceInfoManager;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;
import com.nd.android.adhoc.basic.frame.factory.AdhocFrameFactory;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.login.processOptimization.AssistantAuthenticSystem;
import com.nd.android.adhoc.login.processOptimization.IDeviceInitiator;
import com.nd.android.adhoc.login.processOptimization.IUserAuthenticator;
import com.nd.android.adhoc.loginapi.ILoginApi;
import com.nd.android.adhoc.loginapi.LoginApiRoutePathConstants;
import com.nd.android.adhoc.loginapi.exception.AutoLoginMeetUserLoginException;
import com.nd.android.adhoc.router_api.facade.Postcard;
import com.nd.android.adhoc.router_api.facade.annotation.Route;
import com.nd.android.adhoc.router_api.facade.callback.NavCallback;

import rx.Observable;
import rx.functions.Func1;

@Route(path = LoginRoutePathConstants.PATH_LOGIN_LOGIN)
public class LoginApiImpl implements ILoginApi {
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
                            if(pStatus == DeviceStatus.Init || pStatus == DeviceStatus.Unknown
                                    || pStatus == DeviceStatus.Enrolled) {
                                IUserAuthenticator authenticator = AssistantAuthenticSystem.getInstance()
                                        .getUserAuthenticator();
                                return authenticator.login(pUserName, pPassword);
                            }

                            return Observable.error(new AutoLoginMeetUserLoginException("this " +
                                    "device is auto login, actual username maybe different"));
                        }
                    });
        }


        IUserAuthenticator authenticator = AssistantAuthenticSystem.getInstance()
                .getUserAuthenticator();
        return authenticator.login(pUserName, pPassword);
    }

    @Override
    public Observable<String> getNickName() {
        return null;
    }

    @Override
    public void init(@NonNull Context pContext) {

    }
}
