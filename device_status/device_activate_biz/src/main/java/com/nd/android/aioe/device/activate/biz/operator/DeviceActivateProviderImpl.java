package com.nd.android.aioe.device.activate.biz.operator;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nd.android.adhoc.router_api.facade.annotation.Route;
import com.nd.android.aioe.device.activate.biz.api.model.CheckActivateModel;
import com.nd.android.aioe.device.activate.biz.api.model.DeviceActivateModel;
import com.nd.android.aioe.device.activate.biz.api.provider.IDeviceActivateProvider;
import com.nd.android.aioe.device.info.config.DeviceInfoSpConfig;
import com.nd.android.aioe.device.status.biz.api.constant.DeviceStatus;

@Route(path = IDeviceActivateProvider.ROUTE_PATH)
public class DeviceActivateProviderImpl implements IDeviceActivateProvider {

    @Override
    public DeviceStatus activateByUser(@NonNull String pUsername, @NonNull String pPassword, String pValidationCode) throws Exception {
        DeviceActivateModel activateModel = _UserActivator.activateByUc(pUsername, pPassword, pValidationCode);
        CheckActivateModel checkActivateModel = _ActivateResultChecker.checkActivateResult(1, DeviceInfoSpConfig.getDeviceID(),activateModel.getRequestid());
        return checkActivateModel.getDeviceStatus();
    }

    @Override
    public DeviceStatus activateByGroup(@NonNull String pSchoolCode) throws Exception {
        DeviceActivateModel activateModel =  _GroupActivator.activate(pSchoolCode);
        CheckActivateModel checkActivateModel = _ActivateResultChecker.checkActivateResult(1, DeviceInfoSpConfig.getDeviceID(),activateModel.getRequestid());
        return checkActivateModel.getDeviceStatus();
    }

    @Override
    public DeviceStatus autoActivateByGroup(@NonNull String pRootCode, @NonNull String pSchoolCode) throws Exception {
        return _GroupAutoActivator.autoActivateByGroupCode(pRootCode, pSchoolCode);
    }

    @Override
    public void init(@NonNull Context pContext) {

    }
}
