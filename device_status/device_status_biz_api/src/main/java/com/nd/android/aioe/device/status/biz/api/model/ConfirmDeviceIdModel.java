package com.nd.android.aioe.device.status.biz.api.model;


import android.text.TextUtils;

import com.nd.android.aioe.device.status.dao.api.bean.ConfirmDeviceIdResult;


public class ConfirmDeviceIdModel extends ConfirmDeviceIdResult {

    public boolean isSuccess() {
        return getErrcode() == 0 && !TextUtils.isEmpty(getDeviceID());
    }
}
