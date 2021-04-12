package com.nd.android.aioe.device.status.biz.provider;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.router_api.facade.annotation.Route;
import com.nd.android.aioe.device.info.config.DeviceInfoSpConfig;
import com.nd.android.aioe.device.info.util.DeviceInfoHelper;
import com.nd.android.aioe.device.status.biz.api.constant.DeviceStatus;
import com.nd.android.aioe.device.status.biz.api.model.GetDeviceStatusModel;
import com.nd.android.aioe.device.status.biz.api.provider.IDeviceStatusProvider;

@Route(path = IDeviceStatusProvider.ROUTE_PATH)
public class DeviceStatusProviderImpl implements IDeviceStatusProvider {

    private static final String TAG = "DeviceStatusProviderImpl";

    @Override
    public GetDeviceStatusModel getDeviceStatusFromServer() throws AdhocException {

        String deviceId = DeviceIdGetter.getDeviceId();

        String serialNum = DeviceInfoHelper.getSerialNumberThroughControl();

        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(serialNum)) {
            throw new AdhocException("checkDeviceStatusFromServer failed, deviceid and serial number are empty");
        }

        try {
            GetDeviceStatusModel model = DeviceStatusGetter.queryDeviceStatusFromServer(deviceId);

            if (model != null) {
                DeviceStatus deviceStatus = model.getDevicesStatus();

                if (!deviceStatus.isUnActivated()) {
                    // 记录当前的节点 code 和 名称
                    DeviceInfoSpConfig.saveNodeCode(model.getNodecode());
                    DeviceInfoSpConfig.saveNodeName(model.getNodename());
                    DeviceInfoSpConfig.saveGroupCode(model.getNodecode());
                }

//                DeviceStatusChangeManager.notifyDeviceStatus(deviceStatus);
            }

        } catch (Exception e) {
            Logger.e(TAG, "getDeviceStatusFromServer error: " + e);
        }

        return null;
    }

    @Override
    public void updateDeviceStatus() throws AdhocException {
        DeviceStatusUpdater.updateDeviceStatus();
    }

    @Override
    public void init(@NonNull Context pContext) {

    }
}
