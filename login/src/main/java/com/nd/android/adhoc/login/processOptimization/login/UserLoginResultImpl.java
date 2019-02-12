package com.nd.android.adhoc.login.processOptimization.login;

import com.nd.android.adhoc.login.basicService.data.http.LoginUserResult;

public class UserLoginResultImpl implements IUserLoginResult {

    private LoginUserResult mData = null;
    public UserLoginResultImpl(LoginUserResult pData){
        mData = pData;
    }

    @Override
    public String getUsername() {
        return mData.username;
    }

    @Override
    public String getNickname() {
        return mData.nickname;
    }

    @Override
    public String getLoginToken() {
        return mData.loginToken;
    }
}
