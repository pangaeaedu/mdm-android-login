package com.nd.android.adhoc.login;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nd.android.adhoc.loginapi.IInitApi;
import com.nd.android.adhoc.router_api.facade.annotation.Route;
import com.nd.smartcan.accountclient.UCEnv;
import com.nd.smartcan.accountclient.UCManager;

import rx.Observable;

@Route(path = IInitApi.PATH)
public class InitApiImpl implements IInitApi {
    @Override
    public Observable<Boolean> initEnv() {
        return LoginManager.getInstance().init();
    }

//    @Override
//    public void onEnvChanged(int pIndex) {
//        setUcEnv(pIndex);
//    }
//
//    private void setUcEnv(int pIndex){
//        switch (pIndex) {
//            case 3:
//                UCManager.getInstance().setEnv(UCEnv.AWS);
//                break;
//            case 0:
//            case 1:
//            case 2:
//            default:
//                UCManager.getInstance().setEnv(UCEnv.PreProduct);
//                break;
//        }
//    }

    @Override
    public void init(@NonNull Context pContext) {

    }
}
