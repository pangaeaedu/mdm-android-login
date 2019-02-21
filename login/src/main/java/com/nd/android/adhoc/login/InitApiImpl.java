package com.nd.android.adhoc.login;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;
import com.nd.android.adhoc.login.processOptimization.AssistantAuthenticSystem;
import com.nd.android.adhoc.login.processOptimization.IDeviceInitiator;
import com.nd.android.adhoc.loginapi.IInitApi;
import com.nd.android.adhoc.router_api.facade.annotation.Route;

import rx.Observable;

@Route(path = LoginRoutePathConstants.PATH_LOGIN_INIT)
public class InitApiImpl implements IInitApi {

    @Deprecated
    @Override
    public Observable<Boolean> initEnv() {
//        return LoginManager.getInstance().init();
        return Observable.error(new Exception("not allow to revoke"));
    }

    @Override
    public Observable<DeviceStatus> initDevice() {
        IDeviceInitiator initiator = AssistantAuthenticSystem.getInstance().getDeviceInitiator();
        return initiator.init();
    }

    @Override
    public Observable<DeviceStatus> queryDeviceStatus() {
        IDeviceInitiator initiator = AssistantAuthenticSystem.getInstance().getDeviceInitiator();
        return initiator.queryDeviceStatus();
    }

    @Override
    public void uninit() {
        IDeviceInitiator initiator = AssistantAuthenticSystem.getInstance().getDeviceInitiator();
        initiator.uninit();
    }

    @Override
    public void init(@NonNull Context pContext) {

    }
}
