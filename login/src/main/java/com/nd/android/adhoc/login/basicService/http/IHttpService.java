package com.nd.android.adhoc.login.basicService.http;

import com.nd.android.adhoc.login.basicService.data.http.ActivateHttpResult;

import org.json.JSONObject;

public interface IHttpService {

    void requestPolicy(String pDeviceToken, long pTime, JSONObject pData) throws Exception;

    IBindResult bindDevice(String pDeviceToken, String pPushID, String pSerialNum) throws Exception;

    ActivateHttpResult activateUser(String pUCAccessToken, String pDeviceToken) throws
            Exception;
}
