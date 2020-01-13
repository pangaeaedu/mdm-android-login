package com.nd.android.adhoc.login.processOptimization.login;

public class UcLoginResultImp implements IUserLoginResult {
    private String mUserName = "";
    private String mNickName = "";
    private String mLoginToken = "";

    public UcLoginResultImp(String pUserName, String pNickname, String pLoginToken){
        mUserName = pUserName;
        mNickName = pNickname;
        mLoginToken = pLoginToken;
    }

    @Override
    public String getUsername() {
        return mUserName;
    }

    @Override
    public String getNickname() {
        return mNickName;
    }

    @Override
    public String getLoginToken() {
        return mLoginToken;
    }
}
