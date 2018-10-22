package com.nd.android.adhoc.login.basicService.data.http;

public class GetOldTokenResult {

    private int status = 0;

    private String old_device_token = "";


    public int getStatus() {
        return status;
    }

    public void setStatus(int pStatus) {
        status = pStatus;
    }

    public String getOld_device_token() {
        return old_device_token;
    }

    public void setOld_device_token(String pOld_device_token) {
        old_device_token = pOld_device_token;
    }
}
