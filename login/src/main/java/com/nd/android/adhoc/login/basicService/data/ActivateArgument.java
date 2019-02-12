package com.nd.android.adhoc.login.basicService.data;

import com.nd.android.adhoc.login.basicService.http.IActivateArgument;
import com.nd.android.adhoc.login.enumConst.DeviceType;
//import com.nd.smartcan.accountclient.CurrentUser;

public class ActivateArgument implements IActivateArgument {

    private String mUserToken = "";
    private String mDeviceToken = "";
    private DeviceType mType = DeviceType.Android;

//    private CurrentUser mUser = null;

    public ActivateArgument(String pUserToken, String pDeviceToken){
        mUserToken = pUserToken;
        mDeviceToken = pDeviceToken;
//        mUser = pUser;
    }

    @Override
    public String getPostData() {
        return null;
    }
}
