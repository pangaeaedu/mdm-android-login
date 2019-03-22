package com.nd.android.adhoc.login.processOptimization;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;

import rx.Observable;

public interface IDeviceInitiator {

    Observable<DeviceStatus> init();
    Observable<DeviceStatus> queryDeviceStatus();

    void uninit();
}
