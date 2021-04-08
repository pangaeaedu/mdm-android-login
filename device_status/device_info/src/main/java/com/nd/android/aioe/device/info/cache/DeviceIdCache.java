package com.nd.android.aioe.device.info.cache;

import android.content.Context;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.aioe.device.info.config.DeviceInfoSpConfig;
import com.nd.android.aioe.device.info.util.DeviceIDFileUtils;
import com.nd.android.aioe.device.info.util.DeviceIDSPUtils;

public class DeviceIdCache {

    private static String sDeviceId = "";

    public static void setDeviceId(String pDeviceId) {
        sDeviceId = pDeviceId;
    }

    public static String getDeviceId() {
        if (TextUtils.isEmpty(sDeviceId)) {
            sDeviceId = DeviceInfoSpConfig.getDeviceID();
        }

        return sDeviceId;
    }


    public static void clearDeviceId() {
        sDeviceId = "";
        Context context = AdhocBasicConfig.getInstance().getAppContext();
        DeviceIDSPUtils.saveDeviceIDToSp("");
        DeviceIDFileUtils.saveDeviceIDToSdFile(context, "");
        DeviceIDFileUtils.saveDeviceIDToCacheFile(context, "");
    }


}
