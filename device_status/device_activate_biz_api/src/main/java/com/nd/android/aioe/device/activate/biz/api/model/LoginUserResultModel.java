package com.nd.android.aioe.device.activate.biz.api.model;

import android.text.TextUtils;

public class LoginUserResultModel implements IActivateResult {

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


    public boolean isSuccess() {
        if (TextUtils.isEmpty(getResult())) {
            return false;
        }

        return "success".equalsIgnoreCase(getResult());
    }
}
