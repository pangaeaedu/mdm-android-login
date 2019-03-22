package com.nd.android.adhoc.login.processOptimization.login;

import com.nd.android.adhoc.login.basicService.data.http.LoginUserResponse;

public class UserLoginResultImpl implements IUserLoginResult {

    private LoginUserResponse mData = null;
    public UserLoginResultImpl(LoginUserResponse pData){
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
