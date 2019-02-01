package com.nd.android.adhoc.login;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceInfoManager;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;
import com.nd.android.adhoc.login.processOptimization.AssistantAuthenticSystem;
import com.nd.android.adhoc.login.processOptimization.IDeviceInitiator;
import com.nd.android.adhoc.login.processOptimization.IUserAuthenticator;
import com.nd.android.adhoc.login.ui.LoginActivity;
import com.nd.android.adhoc.loginapi.ILoginApi;
import com.nd.android.adhoc.router_api.facade.annotation.Route;

import rx.Observable;
import rx.functions.Func1;

@Route(path = ILoginApi.PATH)
public class LoginApiImpl implements ILoginApi {
    @Override
    public void enterLoginUI(@NonNull Context pContext) {
        Intent intent = new Intent(pContext, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pContext.startActivity(intent);
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
        if (TextUtils.isEmpty( DeviceInfoManager.getInstance().getDeviceID())) {
            IDeviceInitiator initiator = AssistantAuthenticSystem.getInstance()
                    .getDeviceInitiator();

            return initiator.init()
                    .flatMap(new Func1<DeviceStatus, Observable<DeviceStatus>>() {
                        @Override
                        public Observable<DeviceStatus> call(DeviceStatus pStatus) {
                            IUserAuthenticator authenticator = AssistantAuthenticSystem.getInstance()
                                    .getUserAuthenticator();
                            return authenticator.login(pUserName, pPassword);
                        }
                    });
        }

        IUserAuthenticator authenticator = AssistantAuthenticSystem.getInstance()
                .getUserAuthenticator();
        return authenticator.login(pUserName, pPassword);
    }

    @Override
    public void init(@NonNull Context pContext) {

    }
}
