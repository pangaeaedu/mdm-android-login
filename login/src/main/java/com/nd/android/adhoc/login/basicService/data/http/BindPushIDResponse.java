package com.nd.android.adhoc.login.basicService.data.http;

/**
 *  "errcode":1  //0=成功，-1=失败
 "requestid":"08002800A8C5" //设备的唯一标识，必填
 */
public class BindPushIDResponse {
    private int errcode = 0;
    private String requestid = "";

    public boolean isSuccess(){
        return errcode == 0;
    }
}
