package com.nd.android.adhoc.login;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.frame.api.user.IAdhocLoginStatusNotifier;
import com.nd.android.adhoc.basic.frame.constant.AdhocRouteConstant;
import com.nd.android.adhoc.basic.frame.factory.AdhocFrameFactory;
import com.nd.android.adhoc.login.ui.LoginActivity;
import com.nd.android.adhoc.loginapi.ILoginApi;
import com.nd.android.adhoc.router_api.facade.annotation.Route;

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
        LoginManager.getInstance().logout();

        IAdhocLoginStatusNotifier api = (IAdhocLoginStatusNotifier) AdhocFrameFactory.getInstance().getAdhocRouter()
                .build(AdhocRouteConstant.PATH_LOGIN_STATUS_NOTIFIER).navigation();
        if(api == null){
            return;
        }

        api.onLogout();
    }

    @Override
    public void init(@NonNull Context pContext) {

    }
}
