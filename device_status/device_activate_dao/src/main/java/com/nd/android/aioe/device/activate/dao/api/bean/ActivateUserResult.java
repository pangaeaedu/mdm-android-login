package com.nd.android.aioe.device.activate.dao.api.bean;

public class ActivateUserResult {
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
