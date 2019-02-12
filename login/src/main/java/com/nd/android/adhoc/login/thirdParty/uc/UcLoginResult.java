package com.nd.android.adhoc.login.thirdParty.uc;

import com.nd.android.adhoc.login.basicService.data.push.UserActivateResult;
import com.nd.android.adhoc.loginapi.ILoginResult;
//import com.nd.smartcan.accountclient.CurrentUser;

public class UcLoginResult implements ILoginResult {

//    private CurrentUser mUser;
    private UserActivateResult mCmdData;

    public UcLoginResult( UserActivateResult pArgument){
//        mUser = pUser;
        mCmdData = pArgument;
    }

    @Override
    public Object getData() {
        return mCmdData;
    }

//    public CurrentUser getUser(){
//        return mUser;
//    }

    public UserActivateResult getCmdData(){
        return mCmdData;
    }
}
