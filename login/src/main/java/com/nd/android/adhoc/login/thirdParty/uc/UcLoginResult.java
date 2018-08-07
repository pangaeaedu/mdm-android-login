package com.nd.android.adhoc.login.thirdParty.uc;

import com.nd.android.adhoc.login.thirdParty.IThirdPartyLoginResult;
import com.nd.smartcan.accountclient.CurrentUser;

public class UcLoginResult implements IThirdPartyLoginResult {
    private CurrentUser mCurrentUser = null;

    public UcLoginResult(CurrentUser pUser){
        mCurrentUser = pUser;
    }

    @Override
    public Object getData() {
        return mCurrentUser;
    }
}
