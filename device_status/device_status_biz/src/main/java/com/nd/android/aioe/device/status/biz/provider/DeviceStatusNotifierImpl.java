package com.nd.android.aioe.device.status.biz.provider;

import android.content.Context;
import androidx.annotation.NonNull;

import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.router_api.facade.annotation.Route;
import com.nd.android.aioe.device.status.biz.api.constant.DeviceStatus;
import com.nd.android.aioe.device.status.biz.api.provider.IDeviceStatusNotifier;

@Route(path = IDeviceStatusNotifier.ROUTE_PATH)
public class DeviceStatusNotifierImpl implements IDeviceStatusNotifier {

    private static final String TAG = "DeviceStatusNotifierImpl";

    @Override
    public void notifyDeviceStatus(@NonNull DeviceStatus pNewStatus) {
        Logger.i(TAG,"notifyDeviceStatus: " + pNewStatus);
        if (pNewStatus == DeviceStatus.Init) {
            pNewStatus = DeviceStatus.Enrolled;
            pNewStatus.setIsDeleted(true);
        }
        DeviceStatusChangeManager.notifyDeviceStatus(pNewStatus);
    }

    @Override
    public void init(@NonNull Context pContext) {

    }
}
