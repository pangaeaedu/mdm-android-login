package com.nd.android.adhoc.login.basicService.data.http;

import com.nd.android.adhoc.login.basicService.http.IBindResult;


public class BindResult implements IBindResult {

    private int login_auto = 0;

    private String nick_name = "";

    public BindResult(int pAutoLogin, String pNickName){
        login_auto = pAutoLogin;
        nick_name = pNickName;
    }

    @Override
    public boolean isAutoLogin() {
        return login_auto == 1;
    }

    @Override
    public String getNickName() {
        return nick_name;
    }

    @Override
    public boolean isSuccess() {
        return login_auto != -1;
    }
}
