package com.nd.android.aioe.device.activate.dao.impl;

import android.support.annotation.NonNull;

import com.nd.android.aioe.device.activate.dao.api.IDeviceActivateDao;
import com.nd.android.aioe.device.activate.dao.api.IDeviceUserDao;

public class DeviceActivateDaoHelper {

    public static IDeviceActivateDao getDeviceActivateDao(@NonNull String pBaseUrl) {
        return new DeviceActivateDaoImpl(pBaseUrl);
    }

    public static IDeviceUserDao getDeviceUserDao(@NonNull String pBaseUrl) {
        return new DeviceUserDaoImpl(pBaseUrl);
    }

}
