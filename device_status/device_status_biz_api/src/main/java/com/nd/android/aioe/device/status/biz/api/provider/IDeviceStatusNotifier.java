package com.nd.android.aioe.device.status.biz.api.provider;

import androidx.annotation.NonNull;

import com.nd.android.adhoc.router_api.facade.template.IProvider;
import com.nd.android.aioe.device.status.biz.api.constant.DeviceStatus;

public interface IDeviceStatusNotifier extends IProvider {

    String ROUTE_PATH = "/cmp_device_status_biz/status_notifier";

    void notifyDeviceStatus(@NonNull DeviceStatus pNewStatus);
}
