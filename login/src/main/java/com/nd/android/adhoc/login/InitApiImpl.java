package com.nd.android.adhoc.login;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nd.android.adhoc.loginapi.IInitApi;
import com.nd.android.adhoc.router_api.facade.annotation.Route;

import rx.Observable;

@Route(path = IInitApi.PATH)
public class InitApiImpl implements IInitApi {
    @Override
    public Observable<Boolean> initEnv() {
        return LoginManager.getInstance().init();
    }

    @Override
    public void init(@NonNull Context pContext) {

    }
}
