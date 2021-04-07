package com.nd.android.aioe.device.activate.dao.api;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.common.exception.AdhocException;


public interface IDeviceUserDao {

    <T> T getUserInfo(@NonNull Class<T> pClass, @NonNull String pDeviceID, int pDeviceType) throws AdhocException;

    <T> T setAssetCode(@NonNull Class<T> pClass, @NonNull String pDeviceID, int pDeviceType, @NonNull String pAssetCode) throws AdhocException;

}
