package com.nd.android.adhoc.loginapi;


import com.nd.android.adhoc.router_api.facade.template.IProvider;

import rx.Observable;

public interface IInitApi extends IProvider {
    String PATH = "/login/init";
    Observable<Boolean> initEnv();
}
