package com.nd.android.aioe.device.status.dao.impl.constant;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;

public final class DeviceType {


    // android：1，iOS：2，PC：3，AndroidRobot：5002
    private static int sDeviceType = -1;

    private static final String KEY_DEVICE_TYPE = "ADHOC_DEVICE_TYPE";

    public static int getValue() {
        if (sDeviceType == -1) {
            try {
                Context context = AdhocBasicConfig.getInstance().getAppContext();
                ApplicationInfo appInfo = context.getPackageManager()
                        .getApplicationInfo(context.getPackageName(),
                                PackageManager.GET_META_DATA);

                if (!appInfo.metaData.containsKey(KEY_DEVICE_TYPE)) {
                    sDeviceType = 1;
                    return sDeviceType;
                }

                sDeviceType = appInfo.metaData.getInt(KEY_DEVICE_TYPE);

            } catch (Exception e) {
                sDeviceType = 1;
            }
        }

        return sDeviceType;
    }
}
