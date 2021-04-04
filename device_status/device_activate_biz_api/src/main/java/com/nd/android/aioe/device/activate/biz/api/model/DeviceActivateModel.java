package com.nd.android.aioe.device.activate.biz.api.model;

import com.nd.android.aioe.device.activate.dao.api.bean.DeviceActivateResult;

public class DeviceActivateModel extends DeviceActivateResult {

    public boolean isSuccess(){
        return getErrcode() == 0;
    }

    public int getDelayTime(){
        try {
            return Integer.parseInt(getResult());
        } catch (Exception ignored) {
        }

        return 7200;
    }
}
