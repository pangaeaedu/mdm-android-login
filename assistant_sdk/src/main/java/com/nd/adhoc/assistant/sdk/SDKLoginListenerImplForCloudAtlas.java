package com.nd.adhoc.assistant.sdk;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.frame.api.user.IAdhocLoginInfo;
import com.nd.android.adhoc.basic.frame.api.user.IAdhocLoginListener;
import com.nd.android.adhoc.basic.util.statistic.CloudAtlasUtils;
import com.nd.android.aioe.device.info.util.DeviceInfoManager;
import com.nd.sdp.android.serviceloader.annotation.Service;

@Service(IAdhocLoginListener.class)
public class SDKLoginListenerImplForCloudAtlas implements IAdhocLoginListener {
    @Override
    public void onLogin(@NonNull IAdhocLoginInfo pLoginInfo) {
        CloudAtlasUtils.profileDeviceActivate(DeviceInfoManager.getInstance().getDeviceID());
    }
}
