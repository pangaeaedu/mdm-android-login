package com.nd.android.adhoc.login.basicService;

public interface IHttpService {
    void clear();
    void requestPolicy(String pPolicyVersion) throws Exception;
    void bindToken() throws Exception;

}
