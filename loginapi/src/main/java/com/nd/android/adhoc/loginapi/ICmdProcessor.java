package com.nd.android.adhoc.loginapi;

import com.nd.android.adhoc.router_api.facade.template.IProvider;

public interface ICmdProcessor extends IProvider {
    void onReceived(String pCmd) throws Exception;
}
