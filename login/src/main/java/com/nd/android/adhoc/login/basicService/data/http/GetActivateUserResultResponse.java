package com.nd.android.adhoc.login.basicService.data.http;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;

/**
  "errcode":0   ////激活成功，-1=激活失败
     "msgcode" :"002" //失败原因,0010=激活进行中，0030=自动激活失效
     "status":1 //成功会带上状态
      "nickname":"xxxxx" //成功会带上用户名
     "requestid":"xcxxxxxxxxxxxxxxxxxxxxxxxxxxx"   //唯一标识，等于sessionid
 */

public class GetActivateUserResultResponse {
    private int errcode = 0;
    private String msgcode = "";
    private int status = 1;
    private String nickname = "";
    private String requestid = "";

    public boolean isSuccess(){
        if(errcode != 0){
            return false;
        }

        return true;
    }

    public String getMsgcode(){
        return msgcode;
    }

    public String getUsername(){
        return "";
    }

    public String getNickname(){
        return nickname;
    }
    public DeviceStatus getStatus(){
        return DeviceStatus.fromValue(status);
    }

    public boolean isActivateStillProcessing(){
        if(msgcode.equalsIgnoreCase("0010")
                || msgcode.equalsIgnoreCase("0020")){
            return true;
        }

        return false;
    }

}
