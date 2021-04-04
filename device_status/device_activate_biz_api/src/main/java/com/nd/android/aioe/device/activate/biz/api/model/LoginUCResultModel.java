package com.nd.android.aioe.device.activate.biz.api.model;

import android.text.TextUtils;

public class LoginUCResultModel implements IActivateResult {

    private String result = "";
    private String username = "";
    private String nickname = "";
    private String loginToken = "";

    public LoginUCResultModel(String username, String nickname, String loginToken) {
        this.username = username;
        this.nickname = nickname;
        this.loginToken = loginToken;
    }

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


    public boolean isSuccess() {
        if (TextUtils.isEmpty(getResult())) {
            return false;
        }

        return "success".equalsIgnoreCase(getResult());
    }
}
