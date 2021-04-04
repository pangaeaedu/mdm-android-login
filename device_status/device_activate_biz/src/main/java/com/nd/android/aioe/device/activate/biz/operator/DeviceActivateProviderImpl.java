package com.nd.android.aioe.device.activate.biz.operator;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nd.android.adhoc.router_api.facade.annotation.Route;
import com.nd.android.aioe.device.activate.biz.api.model.DeviceActivateModel;
import com.nd.android.aioe.device.activate.biz.api.provider.IDeviceActivateProvider;
import com.nd.android.aioe.device.status.biz.api.constant.DeviceStatus;

@Route(path = IDeviceActivateProvider.ROUTE_PATH)
public class DeviceActivateProviderImpl implements IDeviceActivateProvider {

    @Override
    public DeviceActivateModel activateByUser(@NonNull String pUsername, @NonNull String pPassword, String pValidationCode) throws Exception {
        return UserActivator.activateByUc(pUsername, pPassword, pValidationCode);
    }

    @Override
    public DeviceActivateModel activateByGroup(@NonNull String pSchoolCode) throws Exception {
        return GroupActivator.activate(pSchoolCode);
    }

    @Override
    public DeviceStatus autoActivateByGroup(@NonNull String pRootCode, @NonNull String pSchoolCode) throws Exception {
        return AutoActivateByGroup.autoActivateByGroupCode(pRootCode, pSchoolCode);
    }

    @Override
    public void init(@NonNull Context pContext) {

    }
}
