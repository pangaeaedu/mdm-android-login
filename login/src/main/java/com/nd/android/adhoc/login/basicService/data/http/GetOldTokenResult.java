package com.nd.android.adhoc.login.basicService.data.http;

public class GetOldTokenResult {

    private int status = 0;

    private String old_device_token = "";

    private String push_id = "";

    private String nick_name = "";

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

    public String getPush_id() {
        return push_id;
    }

    public void setPush_id(String pPush_id) {
        push_id = pPush_id;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String pNick_name) {
        nick_name = pNick_name;
    }
}
