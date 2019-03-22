package com.nd.android.adhoc.login.basicService.data.http;

import android.text.TextUtils;

/*
 "errcode":0   //0=成功
    "device_token":"xxxxxxxx"  //应该使用的devicetoken
    "requestid":"xcxxxxxxxxxxxxxxxxxxxxxxxxxxx"   //唯一标识，等于sessionid
 */
public class ConfirmDeviceIDResponse {
    public int errcode = 0;
    public String device_token = "";
    public String requestid = "";

    public boolean isSuccess(){
        if(errcode == 0 && !TextUtils.isEmpty(device_token)){
            return true;
        }

        return false;
    }

    public String getDeviceID(){
        return device_token;
    }
}
