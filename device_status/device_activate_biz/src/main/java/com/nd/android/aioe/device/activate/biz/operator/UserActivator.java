package com.nd.android.aioe.device.activate.biz.operator;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.aioe.device.activate.biz.api.model.DeviceActivateModel;
import com.nd.android.aioe.device.activate.biz.cache.DeviceActivateCache;
import com.nd.android.aioe.device.activate.dao.api.IDeviceActivateDao;
import com.nd.android.aioe.device.activate.dao.api.IUserLoginDao;
import com.nd.android.aioe.device.activate.dao.api.bean.ILoginUserResult;
import com.nd.android.aioe.device.activate.dao.api.constant.ActivateChannel;
import com.nd.android.aioe.device.activate.dao.impl.DeviceActivateDaoHelper;
import com.nd.android.aioe.device.info.cache.DeviceIdCache;
import com.nd.android.aioe.device.info.util.DeviceInfoHelper;
import com.nd.android.aioe.device.status.dao.impl.constant.DeviceType;
import com.nd.android.mdm.biz.env.MdmEvnFactory;

class UserActivator {

    private static final String TAG = "UserActivator";


    public static DeviceActivateModel activate(@NonNull String pUsername, @NonNull String pPassword) throws Exception {

        ILoginUserResult result = getUserLoginDao().login(pUsername, pPassword);
        if (result == null) {
            throw new AdhocException("login user failed");
        }

        String loginToken = result.getLoginToken();
        if (TextUtils.isEmpty(loginToken)) {
            throw new AdhocException("login user failed, loginToken is empty");
        }

        String deviceID = DeviceIdCache.getDeviceId();
        String serialNum = DeviceInfoHelper.getSerialNumberThroughControl();
        String deviceSerialNumber = DeviceInfoHelper.getDeviceSerialNumberThroughControl();

        if (TextUtils.isEmpty(deviceID)) {
            throw new AdhocException("device id is empty");
        }

        if (TextUtils.isEmpty(serialNum)) {
            throw new AdhocException("serial number is empty");
        }

        String orgId = DeviceActivateCache.getOrgId();

        return getDeviceActivateDao().activate(
                DeviceActivateModel.class,
                deviceID,
                DeviceType.getValue(),
                serialNum,
                deviceSerialNumber,
                ActivateChannel.Uc,
                loginToken,
                orgId
        );

    }

    public static DeviceActivateModel activateByUc(@NonNull String pUsername, @NonNull String pPassword, String pValidationCode) throws Exception {

        ILoginUserResult result = getUserLoginDao().loginUC(pUsername, pPassword, pValidationCode, MdmEvnFactory.getInstance().getCurEnvironment().getUcOrgCode());

        if (result == null) {
            throw new AdhocException("login user failed, result is nulll");
        }

        String loginToken = result.getLoginToken();

        if (TextUtils.isEmpty(loginToken)) {
            throw new AdhocException("login user failed, loginToken is empty");
        }

        String deviceID = DeviceIdCache.getDeviceId();
        String serialNum = DeviceInfoHelper.getSerialNumberThroughControl();
        String deviceSerialNumber = DeviceInfoHelper.getDeviceSerialNumberThroughControl();

        if (TextUtils.isEmpty(deviceID)) {
            throw new AdhocException("device id is empty");
        }

        if (TextUtils.isEmpty(serialNum)) {
            throw new AdhocException("serial number is empty");
        }

        String orgId = DeviceActivateCache.getOrgId();

        return getDeviceActivateDao().activate(
                DeviceActivateModel.class,
                deviceID,
                DeviceType.getValue(),
                serialNum,
                deviceSerialNumber,
                ActivateChannel.Uc,
                loginToken,
                orgId
        );

    }


    private static IDeviceActivateDao getDeviceActivateDao(){
        return DeviceActivateDaoHelper.getDeviceActivateDao(MdmEvnFactory.getInstance().getCurEnvironment().getUrl());
    }


    private static IUserLoginDao getUserLoginDao() {
        return DeviceActivateDaoHelper.getUserLoginDao(MdmEvnFactory.getInstance().getCurEnvironment().getUrl());
    }
}
