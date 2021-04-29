package com.nd.android.aioe.device.activate.biz.api.model;

import android.text.TextUtils;

import com.nd.android.aioe.device.activate.dao.api.bean.DeviceActivateResult;

public class DeviceActivateModel extends DeviceActivateResult {

    public boolean isSuccess() {
        return getErrcode() == 0 && getCode() == 0 && !TextUtils.isEmpty(getRequestid());
    }

    public int getDelayTime(){
        try {
            return Integer.parseInt(getResult());
        } catch (Exception ignored) {
        }

        return 7200;
    }
}
