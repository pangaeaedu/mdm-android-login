package com.nd.android.aioe.device.status.dao.impl;

import android.support.annotation.NonNull;

import com.nd.android.aioe.device.status.dao.api.IDeviceIdDao;
import com.nd.android.aioe.device.status.dao.api.IDeviceStatusDao;

public final class DeviceStatusDaoHelper {

    public static IDeviceIdDao getDeviceIdDao(@NonNull String pBaseUrl) {
        return new DeviceIdDaoImpl(pBaseUrl);
    }

    public static IDeviceStatusDao getDeviceStatusDao(@NonNull String pBaseUrl) {
        return new DeviceStatusDao(pBaseUrl);
    }
}
