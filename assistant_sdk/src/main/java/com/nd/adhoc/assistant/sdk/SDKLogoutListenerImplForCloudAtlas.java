package com.nd.adhoc.assistant.sdk;

import com.nd.android.adhoc.basic.frame.api.user.IAdhocLogoutListener;
import com.nd.android.adhoc.basic.util.statistic.CloudAtlasUtils;
import com.nd.sdp.android.serviceloader.annotation.Service;

@Service(IAdhocLogoutListener.class)
public class SDKLogoutListenerImplForCloudAtlas implements IAdhocLogoutListener {
    @Override
    public void onLogout() {
        CloudAtlasUtils.profileDeviceDeactivate();
    }
}
