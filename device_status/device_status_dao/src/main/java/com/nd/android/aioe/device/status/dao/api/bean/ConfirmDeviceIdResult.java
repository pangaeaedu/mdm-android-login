package com.nd.android.aioe.device.status.dao.api.bean;


/**
 * {
 *    "errcode":0   //0=成功
 *     "device_token":"xxxxxxxx"  //应该使用的devicetoken
 *     "requestid":"xcxxxxxxxxxxxxxxxxxxxxxxxxxxx"   //唯一标识，等于sessionid
 * }
 */
public class ConfirmDeviceIdResult {

    private int errcode = 0;
    private String device_token = "";
    private String requestid = "";

//    public boolean isSuccess() {
//        return errcode == 0 && !TextUtils.isEmpty(device_token);
//    }


    public int getErrcode() {
        return errcode;
    }

    public String getDeviceID() {
        return device_token;
    }

    public String getRequestid() {
        return requestid;
    }
}
