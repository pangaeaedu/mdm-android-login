package com.nd.android.aioe.device.activate.biz.operator;

import android.content.Context;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.router_api.facade.annotation.Route;
import com.nd.android.aioe.device.activate.biz.api.exception.AdhocActivateErrorCode;
import com.nd.android.aioe.device.activate.biz.api.exception.AdhocActivateException;
import com.nd.android.aioe.device.activate.biz.api.model.CheckActivateModel;
import com.nd.android.aioe.device.activate.biz.api.model.DeviceActivateModel;
import com.nd.android.aioe.device.activate.biz.api.provider.IDeviceActivateProvider;
import com.nd.android.aioe.device.info.config.DeviceInfoSpConfig;
import com.nd.android.aioe.device.status.biz.api.constant.DeviceStatus;

import androidx.annotation.NonNull;

@Route(path = IDeviceActivateProvider.ROUTE_PATH)
public class DeviceActivateProviderImpl implements IDeviceActivateProvider {

    @Override
    public DeviceStatus activateByUser(@NonNull String pUsername, @NonNull String pPassword, String pValidationCode) throws Exception {
        DeviceActivateModel activateModel = _UserActivator.activateByUc(pUsername, pPassword, pValidationCode);
        if (!activateModel.isSuccess()) {
            if (TextUtils.isEmpty(activateModel.getMessage())) {
                throw new AdhocActivateException("activate user error", AdhocActivateErrorCode.ERROR_CHECK_RESULT_FAILED_DEFAULT);
            } else {

                String msgCode = null;

                if (activateModel.getMessage().contains(AdhocActivateErrorCode.UC_AUTH_INVALID_TIMESTAMP)) {
                    msgCode = AdhocActivateErrorCode.UC_AUTH_INVALID_TIMESTAMP;
                }

                String msg = AdhocBasicConfig.getInstance().getAppContext()
                        .getString(AdhocActivateErrorCode.transformCheckResultMsg(msgCode));
                int code = AdhocActivateErrorCode.transformCheckResultCode(msgCode);

                throw new AdhocActivateException(msg, code);
            }
        }

        CheckActivateModel checkActivateModel = _ActivateResultChecker.checkActivateResult(1, DeviceInfoSpConfig.getDeviceID(), activateModel.getRequestid());
        if (checkActivateModel == null) {
            return null;
        }
        _ActivateResultOperator.operateActivateResult(checkActivateModel);
        return checkActivateModel.getDeviceStatus();
    }

    @Override
    public DeviceStatus activateByGroup(@NonNull String pSchoolCode) throws Exception {
        DeviceActivateModel activateModel = _GroupActivator.activate(pSchoolCode);
        CheckActivateModel checkActivateModel = _ActivateResultChecker.checkActivateResult(1, DeviceInfoSpConfig.getDeviceID(), activateModel.getRequestid());
        if (checkActivateModel == null) {
            return null;
        }
        _ActivateResultOperator.operateActivateResult(checkActivateModel);
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
