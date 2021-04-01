package com.nd.android.aioe.device.activate.dao.api.bean;

public class ActivateGroupResult {
    public String result = "";
    public String username = "";
    public String nickname = "";
    public String loginToken = "";

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
//
//    public boolean isSuccess(){
//        if(TextUtils.isEmpty(result)){
//            return false;
//        }
//
//        if(result.equalsIgnoreCase("success")){
//            return true;
//        }
//
//        return false;
//    }
}
