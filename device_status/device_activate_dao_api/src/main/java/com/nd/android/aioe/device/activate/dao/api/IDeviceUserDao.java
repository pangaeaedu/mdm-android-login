package com.nd.android.aioe.device.activate.dao.api;

import android.support.annotation.NonNull;

import com.nd.android.aioe.device.activate.dao.api.constant.ActivateChannel;



public interface IDeviceUserDao {

    String ROUTE_PATH = "/cmp_device_activate_dao/user_info";

    <T> T getUserInfo(@NonNull Class<T> pClass, @NonNull String pDeviceID, int pDeviceType) throws Exception;

}
