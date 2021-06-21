package com.nd.android.aioe.device.activate.biz.operator;

import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.aioe.device.activate.biz.api.ActivateConfig;
import com.nd.android.aioe.device.activate.biz.api.model.DeviceActivateModel;
import com.nd.android.aioe.device.activate.biz.cache.DeviceActivateCache;
import com.nd.android.aioe.device.activate.dao.api.IDeviceActivateDao;
import com.nd.android.aioe.device.activate.dao.api.constant.ActivateChannel;
import com.nd.android.aioe.device.activate.dao.impl.DeviceActivateDaoHelper;
import com.nd.android.aioe.device.info.cache.DeviceIdCache;
import com.nd.android.aioe.device.info.util.DeviceInfoHelper;
import com.nd.android.aioe.device.status.dao.impl.constant.DeviceType;
import com.nd.android.mdm.biz.env.MdmEvnFactory;

class _GroupActivator {

    public static DeviceActivateModel activate(@NonNull String pSchoolCode) throws Exception {

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
                pSchoolCode,
                deviceSerialNumber,
                ActivateChannel.AutoLogin,
                "",
                ActivateConfig.getInstance().getActivateRealType(),
                orgId);
    }


    private static IDeviceActivateDao getDeviceActivateDao() {
        return DeviceActivateDaoHelper.getDeviceActivateDao(MdmEvnFactory.getInstance().getCurEnvironment().getUrl());
    }
}
