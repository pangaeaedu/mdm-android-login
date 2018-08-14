package com.nd.android.adhoc.login.basicService.data.http;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nd.android.adhoc.login.basicService.http.IBindResult;


public class BindResult implements IBindResult {

    @JsonProperty("login_auto")
    private int mAutoLogin = 0;

    @JsonProperty("nickname")
    private String mNickName = "";

    public BindResult(int pAutoLogin, String pNickName){
        mAutoLogin = pAutoLogin;
        mNickName = pNickName;
    }

    @Override
    public boolean isAutoLogin() {
        return mAutoLogin == 1;
    }

    @Override
    public String getNickName() {
        return mNickName;
    }

    @Override
    public boolean isSuccess() {
        return mAutoLogin != -1;
    }
}
