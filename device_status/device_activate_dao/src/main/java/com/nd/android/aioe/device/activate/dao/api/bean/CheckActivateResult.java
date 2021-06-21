package com.nd.android.aioe.device.activate.dao.api.bean;

import androidx.annotation.NonNull;

/**
  "errcode":0   ////激活成功，-1=激活失败
     "msgcode" :"002" //失败原因,010=激活进行中，0030=自动激活失效
     "status":1 //成功会带上状态
      "nickname":"xxxxx" //成功会带上用户名
     "requestid":"xcxxxxxxxxxxxxxxxxxxxxxxxxxxx"   //唯一标识，等于sessionid
 */

public class CheckActivateResult {
    private int errcode = 0;
    private String msgcode = "";
    private int status = 1;
    private String nick_name = "";
    private String requestid = "";
    private String userid = "";

    public int getErrcode() {
        return errcode;
    }

    /**
     * 010=激活进行中
     * 020=mdm异常
     * 030=自动激活失效（如果失效表示不再自动登录，输入用户名和密码登录）
     * 040=超过设备运行被激活的次数（目前一台设备只运行同时被激活一次，所以表示该设备已经被用）
     * 050=超过用户允许激活的设备数（目前一个用户只运行同时被注册一次，所以表示该用户已经被用）
     * 060=组不存在
     */
    public String getMsgcode() {
        return msgcode;
    }

    public int getStatus() {
        return status;
    }

    public String getNick_name() {
        return nick_name;
    }

    public String getRequestid() {
        return requestid;
    }

    public String getUserid() {
        return userid;
    }


    @Override
    public String toString() {
        return "GetActivateUserResult{" +
                "errcode=" + errcode +
                ", msgcode='" + msgcode + '\'' +
                ", status=" + status +
                ", nick_name='" + nick_name + '\'' +
                ", requestid='" + requestid + '\'' +
                ", userid='" + userid + '\'' +
                '}';
    }

}
