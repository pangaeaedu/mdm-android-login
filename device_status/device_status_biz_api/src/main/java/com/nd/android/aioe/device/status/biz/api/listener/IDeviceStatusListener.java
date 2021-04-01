package com.nd.android.aioe.device.status.biz.api.listener;

import android.support.annotation.NonNull;

import com.nd.android.aioe.device.status.biz.api.constant.DeviceStatus;

public interface IDeviceStatusListener {

    void onStatusChange(@NonNull DeviceStatus pOldStatus, @NonNull DeviceStatus pNewStatus);
}
