package com.nd.android.adhoc.login.basicService.data.http;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;


/*
{
    "errcode":0   //0=成功
    "status":1  //0=未知，1=入库，3=丢失,4=在用，5=故障，6=锁定，7=淘汰
    "login_auto":1 //1=自动登录，0=非自动登录
    "requestid":"xcxxxxxxxxxxxxxxxxxxxxxxxxxxx"   //唯一标识，等于sessionid
}
 */
public class QueryDeviceStatusResponse {
    public int errcode = 0;
    public int status = 1;
    private int login_auto = 0;
    private String nick_name = "";
    private String jobnum  = "";
    public String requestid = "";

    public boolean isSuccess(){
        return errcode == 0;
    }

    public DeviceStatus getStatus(){
        return DeviceStatus.fromValue(status);
    }

    public String getNickname(){
        return nick_name;
    }

    //由于郭文那边不会返加username,先用nickname代替
    public String getUsername(){
        return nick_name;
    }

    public String getJobnum(){
        return jobnum;
    }
    public boolean isAutoLogin(){
        return login_auto == 1;
    }

    public String toString() {
        return "errcode:" + errcode
                + " status:" + status
                + " login_auto:" + login_auto
                + " nick_name:" + nick_name
                + " jobnum:" + jobnum;
    }
}
