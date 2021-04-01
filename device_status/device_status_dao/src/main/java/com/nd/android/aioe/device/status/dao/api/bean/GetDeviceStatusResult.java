package com.nd.android.aioe.device.status.dao.api.bean;

import android.support.annotation.NonNull;

/**
 * {
 *  "errcode":0 //0=成功
 *  "status":1 //0=未知，1=入库，3=丢失,4=在用，5=故障，6=锁定，7=淘汰
 *  "login_auto":1 //1=自动登录，0=非自动登录(uc)
 *  "nick_name":"dddd" // 在用的情况下会返回NickName
 *  "jobnum":"120124" //工号
 *  "requestid":"xcxxxxxxxxxxxxxxxxxxxxxxxxxxx" //唯一标识，等于sessionid
 *  "need_group" : 1
 *  "groupcode":"0020"  //根节点的groupcode
 *  "nodecode":"009089" //所在的节点
 *  "nodename":"default" //所在的节点名称
 *  "delete_status":true     //true =设备被注销或删除（此时受控端如果是自动激活模式则不进入自动激活等待用户操作才进入），false 或这个值不存在=设备没有注销或删除（此时受控端如果是自动激活模式则开始自动激活）
 * }
 */
public class GetDeviceStatusResult {

    private int errcode = 0;
    private int status = 1;
    private int login_auto = 0;
    private int need_group = 0;       // 是否需要组织节点
    private String nick_name = "";
    private String jobnum  = "";
    private String groupcode = "";    // 组织节点的根节点
    private String requestid = "";

    private String nodecode = "";
    private String nodename = "";
    private boolean delete_status = false;

    private String selSchoolGroupCode = "";

    public int getErrcode() {
        return errcode;
    }

    public int getStatus() {
        return status;
    }

    public int getLogin_auto() {
        return login_auto;
    }

    public int getNeed_group() {
        return need_group;
    }

    public String getNick_name() {
        return nick_name;
    }

    public String getJobnum() {
        return jobnum;
    }

    public String getGroupcode() {
        return groupcode;
    }

    public String getRequestid() {
        return requestid;
    }

    public String getNodecode() {
        return nodecode;
    }

    public String getNodename() {
        return nodename;
    }

    public boolean isDelete_status() {
        return delete_status;
    }

    public String getSelSchoolGroupCode() {
        return selSchoolGroupCode;
    }

    @NonNull
    @Override
    public String toString() {
        return "GetDeviceStatusResult{" +
                "errcode=" + errcode +
                ", status=" + status +
                ", login_auto=" + login_auto +
                ", need_group=" + need_group +
                ", nick_name='" + nick_name + '\'' +
                ", jobnum='" + jobnum + '\'' +
                ", groupcode='" + groupcode + '\'' +
                ", requestid='" + requestid + '\'' +
                ", nodecode='" + nodecode + '\'' +
                ", nodename='" + nodename + '\'' +
                ", delete_status=" + delete_status +
                ", selSchoolGroupCode='" + selSchoolGroupCode + '\'' +
                '}';
    }
}
