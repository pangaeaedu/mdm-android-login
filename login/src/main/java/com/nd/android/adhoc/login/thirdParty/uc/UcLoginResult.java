package com.nd.android.adhoc.login.thirdParty.uc;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.login.basicService.data.push.UserActivateResult;
import com.nd.android.adhoc.login.thirdParty.IThirdPartyLoginResult;
import com.nd.smartcan.accountclient.CurrentUser;

public class UcLoginResult implements IThirdPartyLoginResult {

    private CurrentUser mUser;
    private UserActivateResult mCmdData;

    public UcLoginResult(@NonNull CurrentUser pUser,  UserActivateResult pArgument){
        mUser = pUser;
        mCmdData = pArgument;
    }

    @Override
    public Object getData() {
        return mCmdData;
    }

    public CurrentUser getUser(){
        return mUser;
    }

    public UserActivateResult getCmdData(){
        return mCmdData;
    }
}
