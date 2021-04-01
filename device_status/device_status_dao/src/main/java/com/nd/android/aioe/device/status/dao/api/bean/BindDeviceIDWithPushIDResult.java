package com.nd.android.aioe.device.status.dao.api.bean;

public class BindDeviceIDWithPushIDResult {

    private int errcode = 0;
    private String requestid = "";

    public int getErrcode() {
        return errcode;
    }

    public String getRequestid() {
        return requestid;
    }

    //    public boolean isDeviceTokenNotFound() {
//        return errcode == 300000;
//    }
}
