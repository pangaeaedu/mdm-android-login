package com.nd.android.adhoc.login.basicService.data.http;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;

/**
  "errcode":0   ////激活成功，-1=激活失败
     "msgcode" :"002" //失败原因,010=激活进行中，0030=自动激活失效
     "status":1 //成功会带上状态
      "nickname":"xxxxx" //成功会带上用户名
     "requestid":"xcxxxxxxxxxxxxxxxxxxxxxxxxxxx"   //唯一标识，等于sessionid
 */

public class GetActivateUserResultResponse {
    private int errcode = 0;
    private String msgcode = "";
    private int status = 1;
    private String nick_name = "";
    private String requestid = "";
    private String userid = "";

    public boolean isSuccess(){
        if(errcode != 0){
            return false;
        }

        return true;
    }

    /**
     * 010=激活进行中
     * 020=mdm异常
     * 030=自动激活失效（如果失效表示不再自动登录，输入用户名和密码登录）
     * 040=超过设备运行被激活的次数（目前一台设备只运行同时被激活一次，所以表示该设备已经被用）
     * 050=超过用户允许激活的设备数（目前一个用户只运行同时被注册一次，所以表示该用户已经被用）
     * 060=组不存在
     */
    public String getMsgcode(){
        return msgcode;
    }

    public String getUsername(){
        return "";
    }

    public String getNickname(){
        return nick_name;
    }

    public String getUserid(){
        return userid;
    }
    public DeviceStatus getStatus(){
        return DeviceStatus.fromValue(status);
    }

    public boolean isActivateStillProcessing(){
        if(msgcode.equalsIgnoreCase("010")
                || msgcode.equalsIgnoreCase("020")){
            return true;
        }

        return false;
    }

    /**
     * 是否是 组（学校）不存在
     */
    public boolean isGroupNotFound() {
        return msgcode.equalsIgnoreCase("060");
    }

    public String toString() {
        return "errcode:" + errcode
                + " msgcode:" + msgcode
                + " status:" + status
                + " nickname:" + nick_name;
    }

}
