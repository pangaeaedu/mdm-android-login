package com.nd.android.adhoc.login.processOptimization;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;

public interface IDeviceStatusListener {
    void onDeviceStatusChanged(DeviceStatus pStatus);
}
