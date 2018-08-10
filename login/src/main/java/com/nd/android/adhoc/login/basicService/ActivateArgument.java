package com.nd.android.adhoc.login.basicService;

import com.nd.android.adhoc.login.basicService.http.IActivateArgument;
import com.nd.android.adhoc.login.enumConst.DeviceType;
import com.nd.smartcan.accountclient.CurrentUser;

public class ActivateArgument implements IActivateArgument {

    private String mUserToken = "";
    private String mDeviceToken = "";
    private DeviceType mType = DeviceType.Android;

    private CurrentUser mUser = null;

    public ActivateArgument(String pUserToken, String pDeviceToken, CurrentUser pUser){
        mUserToken = pUserToken;
        mDeviceToken = pDeviceToken;
        mUser = pUser;
    }

    @Override
    public String getPostData() {
        return null;
    }
}
