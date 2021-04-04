package com.nd.android.adhoc.login.processOptimization;

import com.nd.android.aioe.device.status.biz.api.constant.DeviceStatus;

public interface IDeviceStatusListener {
    void onDeviceStatusChanged(DeviceStatus pStatus);
}
