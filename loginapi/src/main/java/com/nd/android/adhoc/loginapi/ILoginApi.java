package com.nd.android.adhoc.loginapi;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nd.android.adhoc.router_api.facade.template.IProvider;

public interface ILoginApi extends IProvider {
    String PATH = "/loginapi/login";


//    Observable<ILoginResult> login(@NonNull final String pUserName,
//                                   @NonNull final String pPassword);
    void enterLoginUI(@NonNull Context pContext);
    void logout();


}
