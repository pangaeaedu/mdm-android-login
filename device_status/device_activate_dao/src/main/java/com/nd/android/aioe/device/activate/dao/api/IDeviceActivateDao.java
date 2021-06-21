package com.nd.android.aioe.device.activate.dao.api;

import androidx.annotation.NonNull;

import com.nd.android.aioe.device.activate.dao.api.constant.ActivateChannel;


public interface IDeviceActivateDao {

    String ROUTE_PATH = "/cmp_device_activate_dao/activate";

    <T> T activate(@NonNull Class<T> pClass, @NonNull String pDeviceID, int pDeviceType, @NonNull String pSerialNo, @NonNull String pDeviceSerialNo,
                   @NonNull ActivateChannel pChannel, @NonNull String pLoginToken, String pOrgId) throws Exception;

    <T> T activate(@NonNull Class<T> pClass, @NonNull String pDeviceID, int pDeviceType, @NonNull String pSerialNo, @NonNull String pSchoolGroupCode, @NonNull String pDeviceSerialNo,
                   @NonNull ActivateChannel pChannel, @NonNull String pLoginToken, int pRealType, String pOrgId) throws Exception;

    <T> T getActivateResult(@NonNull Class<T> pClass, @NonNull String pDeviceID,
                            int pDeviceType, @NonNull String pRequestID) throws Exception;
}
