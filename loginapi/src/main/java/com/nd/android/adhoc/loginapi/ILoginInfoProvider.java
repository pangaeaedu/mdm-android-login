package com.nd.android.adhoc.loginapi;

import com.nd.android.adhoc.router_api.facade.template.IProvider;

import org.json.JSONObject;

public interface ILoginInfoProvider extends IProvider {
    String PATH = "/assistant/infoprovider";

    JSONObject getDeviceInfo();
}
