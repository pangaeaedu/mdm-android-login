package com.nd.android.aioe.device.status.dao.api;

import androidx.annotation.NonNull;

import java.util.Map;

public interface IDeviceIdDao {

    String ROUTE_PATH = "/cmp_device_status_dao/device_id";

    <T> T confirmDeviceID(@NonNull Class<T> pClass, @NonNull Map<String, Object> pHardwareMap, String pDeviceId, int deviceType) throws Exception;

    <T> T bindDeviceIDWithPushID(@NonNull Class<T> pClass, @NonNull String pDeviceID, int pDeviceType, @NonNull String pPushID) throws Exception;

    boolean submitHardwareInfo(@NonNull String pDeviceId, String pWifiMac, String pLanMac) throws Exception;

}
