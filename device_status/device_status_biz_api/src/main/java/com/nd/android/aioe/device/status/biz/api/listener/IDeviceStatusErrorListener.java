package com.nd.android.aioe.device.status.biz.api.listener;

public interface IDeviceStatusErrorListener {

    int ERROR_CODE_CONFIRM_DEVICE_ID_ERROR = 1001;

    int ERROR_CODE_GET_DEVICE_STATUS_ERROR = 1002;

    void onError(int pErrorCode);
}
