package com.nd.android.aioe.device.status.biz.provider;

import android.text.TextUtils;

import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.aioe.device.info.config.DeviceInfoSpConfig;
import com.nd.android.aioe.device.info.util.DeviceInfoHelper;
import com.nd.android.aioe.device.status.biz.api.constant.DeviceStatus;
import com.nd.android.aioe.device.status.biz.api.model.GetDeviceStatusModel;
import com.nd.android.aioe.device.status.dao.api.IDeviceStatusDao;
import com.nd.android.aioe.device.status.dao.impl.DeviceStatusDaoHelper;
import com.nd.android.mdm.biz.env.MdmEvnFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class _DeviceStatusChecker {

    private static final String TAG = "DeviceStatusChecker";

    private static final ExecutorService sCheckStatusSingleExecutor = Executors.newSingleThreadExecutor();

    public static void checkDeviceStatusFromServer(final String pDeviceID) {

        sCheckStatusSingleExecutor.submit(new Runnable() {
            @Override
            public void run() {
                String serialNum = DeviceInfoHelper.getSerialNumberThroughControl();

                if (TextUtils.isEmpty(pDeviceID) || TextUtils.isEmpty(serialNum)) {
                    Logger.i(TAG, " checkDeviceStatusFromServer failed, deviceid and serial number are empty");
                    return;
                }

                try {
                    GetDeviceStatusModel model = getDeviceStatusDao().getDeviceStatus(GetDeviceStatusModel.class, pDeviceID, serialNum, 1);

                    if (model != null) {
                        DeviceStatus deviceStatus = model.getDevicesStatus();

                        if (!deviceStatus.isUnActivated()) {
                            // 记录当前的节点 code 和 名称
                            DeviceInfoSpConfig.saveNodeCode(model.getNodecode());
                            DeviceInfoSpConfig.saveNodeName(model.getNodename());
                            DeviceInfoSpConfig.saveGroupCode(model.getNodecode());
                        }

                        DeviceStatusChangeManager.notifyDeviceStatus(deviceStatus);
                    }

                } catch (Exception e) {
                    Logger.e(TAG, "checkDeviceStatusFromServer error: " + e);
                }
            }
        });
    }

    private static IDeviceStatusDao getDeviceStatusDao() {
        return DeviceStatusDaoHelper.getDeviceStatusDao(MdmEvnFactory.getInstance().getCurEnvironment().getUrl());
    }

}
