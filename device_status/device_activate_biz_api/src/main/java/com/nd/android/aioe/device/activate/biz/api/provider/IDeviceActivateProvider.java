package com.nd.android.aioe.device.activate.biz.api.provider;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.router_api.facade.template.IProvider;

import com.nd.android.aioe.device.activate.biz.api.model.DeviceActivateModel;
import com.nd.android.aioe.device.status.biz.api.constant.DeviceStatus;

public interface IDeviceActivateProvider extends IProvider {

    String ROUTE_PATH = "/cmp_device_activate_biz/activate_provider";

    DeviceActivateModel activateByUser(@NonNull String pUsername, @NonNull String pPassword, String pValidationCode) throws Exception;

    DeviceActivateModel activateByGroup(@NonNull String pSchoolCode) throws Exception;

    DeviceStatus autoActivateByGroup(@NonNull String pRootCode, @NonNull String pSchoolCode) throws Exception;
}
