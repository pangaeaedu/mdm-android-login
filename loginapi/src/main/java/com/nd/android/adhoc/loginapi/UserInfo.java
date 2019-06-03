package com.nd.android.adhoc.loginapi;

public class UserInfo {
    private String mUserName = "";
    private String mUserID = "";
    private String mOrgCode = "";

    public UserInfo(String pUserName, String pUserID, String pOrgCode){
        mUserName = pUserName;
        mUserID = pUserID;
        mOrgCode = pOrgCode;
    }

    public String getUserName(){
        return mUserName;
    }

    public String getUserID(){
        return mUserID;
    }

    public String getOrgCode(){
        return mOrgCode;
    }
}
