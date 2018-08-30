package com.nd.android.adhoc.login.info;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nd.android.adhoc.basic.frame.api.user.IAdhocLoginInfo;
import com.nd.android.adhoc.basic.frame.api.user.IAdhocUserInfo;
import com.nd.android.adhoc.basic.frame.util.AdhocMapDecorator;

public class AdhocLoginInfoImpl implements IAdhocLoginInfo {

    private IAdhocUserInfo mUserInfo = null;
    private AdhocMapDecorator mExtInfo = null;

    public AdhocLoginInfoImpl(IAdhocUserInfo pUserInfo, AdhocMapDecorator pExtInfo){
        mUserInfo = pUserInfo;
        mExtInfo = pExtInfo;
    }
    @NonNull
    @Override
    public IAdhocUserInfo getUserInfo() {
        return mUserInfo;
    }

    @Nullable
    @Override
    public AdhocMapDecorator getExtInfo() {
        return mExtInfo;
    }
}
