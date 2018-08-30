package com.nd.android.adhoc.login.info;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.frame.api.user.IAdhocUserInfo;

public class AdhocUserInfoImpl implements IAdhocUserInfo {

    private String mAccountNum = "";
    private String mNickname = "";

    public AdhocUserInfoImpl(String pAccountNum, String pNickname){
        mAccountNum = pAccountNum;
        mNickname = pNickname;
    }
    @NonNull
    @Override
    public String getUserId() {
        return mAccountNum;
    }

    @NonNull
    @Override
    public String getUserName() {
        return mNickname;
    }
}
