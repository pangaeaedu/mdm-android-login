package com.nd.android.adhoc.loginapi;


import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;
import com.nd.android.adhoc.router_api.facade.template.IProvider;

import rx.Observable;

public interface IInitApi extends IProvider {
    @Deprecated
    Observable<Boolean> initEnv();

    Observable<DeviceStatus> initDevice();

    Observable<DeviceStatus> queryDeviceStatus();

    void uninit();

}
