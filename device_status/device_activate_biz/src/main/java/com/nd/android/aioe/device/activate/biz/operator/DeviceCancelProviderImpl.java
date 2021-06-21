package com.nd.android.aioe.device.activate.biz.operator;

import android.content.Context;
import androidx.annotation.NonNull;

import com.nd.android.adhoc.router_api.facade.annotation.Route;
import com.nd.android.aioe.device.activate.biz.api.provider.IDeviceCancelProvider;

@Route(path = IDeviceCancelProvider.ROUTE_PATH)
public class DeviceCancelProviderImpl implements IDeviceCancelProvider{

    private static final String TAG = "DeviceActivate";

    @Override
    public void onDeviceCancel() {
        _DeviceCancelOperator.cancelDevice();
    }

    @Override
    public void init(@NonNull Context pContext) {

    }
}
