package com.nd.android.adhoc.login.basicService.http;

public interface IHttpService {
    void clear();
    void requestPolicy(String pPolicyVersion) throws Exception;

    IBindResult bindDevice(String pDeviceToken, String pPushID, String pSerialNum) throws Exception;
    IActivateResult activateUser(IActivateArgument pArgument) throws Exception;
}
