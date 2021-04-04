package com.nd.android.aioe.device.activate.dao.api.bean;

public class LoginUcUserResult implements ILoginUserResult {

    private String username = "";
    private String nickname = "";
    private String loginToken = "";

    public LoginUcUserResult(String username, String nickname, String loginToken) {
        this.username = username;
        this.nickname = nickname;
        this.loginToken = loginToken;
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
