package com.nd.android.aioe.device.activate.dao.api.bean;

public class DeviceActivateResult {
    private int errcode = 0;
    private String result = "";
    private String requestid = "";

    public int getErrcode() {
        return errcode;
    }

    public String getResult() {
        return result;
    }

    public String getRequestid() {
        return requestid;
    }
}
