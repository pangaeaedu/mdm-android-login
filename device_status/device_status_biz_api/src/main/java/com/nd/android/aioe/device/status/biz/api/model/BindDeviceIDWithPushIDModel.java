package com.nd.android.aioe.device.status.biz.api.model;

import com.nd.android.aioe.device.status.dao.api.bean.BindDeviceIDWithPushIDResult;

public class BindDeviceIDWithPushIDModel extends BindDeviceIDWithPushIDResult {


    public boolean isSuccess() {
        return getErrcode() == 0;
    }

    public boolean isDeviceTokenNotFound() {
        return getErrcode() == 300000;
    }
}
