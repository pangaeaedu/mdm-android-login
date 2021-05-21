package com.nd.android.aioe.device.status.dao.api;

import androidx.annotation.NonNull;

public interface IDeviceStatusDao {

    String ROUTE_PATH = "/cmp_device_status_dao/device_status";

    <T> T getDeviceStatus(@NonNull Class<T> pClass, @NonNull String pDeviceID, @NonNull String pSerialNum) throws Exception;

    <T> T getDeviceStatus(@NonNull Class<T> pClass, @NonNull String pDeviceID, @NonNull String pSerialNum, int pNeedGroup) throws Exception;

    <T> T getDeviceStatus(@NonNull Class<T> pClass, @NonNull String pDeviceID, @NonNull String pSerialNum, int pNeedGroup, int pDeviceType) throws Exception;

}
