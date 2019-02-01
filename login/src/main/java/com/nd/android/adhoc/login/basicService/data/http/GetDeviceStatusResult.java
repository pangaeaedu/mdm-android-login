package com.nd.android.adhoc.login.basicService.data.http;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;

import java.util.Map;


/*
{
   "errcode":0   //0=成功
    "status":1  //1=入库，2=在用，3=锁定，4=丢失，5=故障，6=淘汰
   "deviceinfo":{"nickname":"xxxxxx"}
    "requestid":"xcxxxxxxxxxxxxxxxxxxxxxxxxxxx"   //唯一标识，等于sessionid
}
 */
public class GetDeviceStatusResult {
    public int errcode = 0;
    public int status = 1;
    public Map<String, Object> deviceinfo = null;
    public String requestid = "";

    public boolean isSuccess(){
        return errcode == 0;
    }

    public DeviceStatus getStatus(){
        return DeviceStatus.fromValue(status);
    }
}
