package com.nd.android.aioe.device.activate.biz.api.provider;

import com.nd.android.adhoc.router_api.facade.template.IProvider;

public interface IDeviceActivateRouteProvider extends IProvider {

    String ROUTE_PATH = "/cmp_device_activate_biz/activate_route_provider";

    void navigationActivateRoute();

    void navigationDeActivateRoute();
}
