package com.nd.android.aioe.device.status.biz.api;

import android.support.annotation.NonNull;

public interface IDeviceStatusChangeListener {

    void onStatusChange(@NonNull DeviceStatus pNewStatus);
}
