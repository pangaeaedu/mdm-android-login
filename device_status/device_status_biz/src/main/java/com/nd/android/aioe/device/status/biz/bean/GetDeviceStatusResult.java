package com.nd.android.aioe.device.status.biz.bean;

import com.nd.android.aioe.device.status.biz.api.DeviceStatus;

public class GetDeviceStatusResult {

    public int errcode = 0;
    public int status = 1;
    private int login_auto = 0;
    private int need_group = 0;       // 是否需要组织节点
    private String nick_name = "";
    private String jobnum  = "";
    private String groupcode = "";    // 组织节点的根节点
    public String requestid = "";

    private String nodecode = "";
    private String nodename = "";
    private boolean delete_status = false;

    private String selSchoolGroupCode = "";

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

    public boolean isNeedGroup(){
        return need_group == 1;
    }

    public String getSelSchoolGroupCode(){
        return selSchoolGroupCode;
    }

    public void setSelSchoolGroupCode(String pSchoolGroupCode){
        selSchoolGroupCode = pSchoolGroupCode;
    }

    public String getRootCode(){
        return groupcode;
    }

    public String getNodecode() {
        return nodecode;
    }

    public String getNodename() {
        return nodename;
    }

    public String toString() {
        return "errcode:" + errcode
                + " status:" + status
                + " login_auto:" + login_auto
                + " nick_name:" + nick_name
                + " jobnum:" + jobnum;
    }

    public boolean getDeleteStatus() {
        return delete_status;
    }
}
