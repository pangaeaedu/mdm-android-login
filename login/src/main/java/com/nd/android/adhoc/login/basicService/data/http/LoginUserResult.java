package com.nd.android.adhoc.login.basicService.data.http;

import android.text.TextUtils;

public class LoginUserResult {
    public String result = "";
    public String username = "";
    public String nickname = "";
    public String loginToken = "";

    public boolean isSuccess(){
        if(TextUtils.isEmpty(result)){
            return false;
        }

        if(result.equalsIgnoreCase("success")){
            return true;
        }

        return false;
    }
}
