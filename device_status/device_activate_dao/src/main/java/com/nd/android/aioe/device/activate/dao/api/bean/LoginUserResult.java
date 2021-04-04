package com.nd.android.aioe.device.activate.dao.api.bean;

public class LoginUserResult implements ILoginUserResult{
    private String result = "";
    private String username = "";
    private String nickname = "";
    private String loginToken = "";

    public String getResult() {
        return result;
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public String getLoginToken() {
        return loginToken;
    }
}
