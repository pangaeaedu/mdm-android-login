package com.nd.android.adhoc.login.basicService.http;

import com.nd.android.adhoc.login.basicService.data.http.ActivateHttpResult;

public interface IHttpService {

    void requestPolicy(String pDeviceToken) throws Exception;

    IBindResult bindDevice(String pDeviceToken, String pPushID, String pSerialNum) throws Exception;

    ActivateHttpResult activateUser(String pUserToken, String pDeviceToken) throws
            Exception;
}
