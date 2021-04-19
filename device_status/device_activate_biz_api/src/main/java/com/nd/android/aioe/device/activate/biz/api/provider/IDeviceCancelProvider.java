package com.nd.android.aioe.device.activate.biz.api.provider;

import com.nd.android.adhoc.router_api.facade.template.IProvider;

public interface IDeviceCancelProvider extends IProvider {

    String ROUTE_PATH = "/cmp_device_activate_biz/device_cancel";

    void onDeviceCancel();

}
