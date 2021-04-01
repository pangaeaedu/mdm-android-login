package com.nd.android.aioe.device.activate.dao.api.bean;


//        "requestid":"xxxxxxxxx"
//        "errcode":0
//        "msgcode": 10015 //当errcode=-1时，10015表示没找到
//        "nick_name":"老王"
//        "user_id":"xxxxxx"
//        "device_code": "11" //获取设备编码，需要移动端补齐位数
//        "groupname":"xxxxx" //组名
//        "groupcode":"0090" //组code
//        "school_name":"xxxx学校" //学校名称，如果有配置的话有

public class GetUserInfoResult {
    public String requestid = "";
    public String nick_name = "";
    public String user_id = "";

    // 设备编码，根据OMO 需求新增的一个字段 -- by hyk 20200410
    public String device_code = "";

    // 根据 OMO 增加对 groupcode 字段的解析，用于后期查询父节点级当前节点的名称等信息 -- by hyk 20200511
    public String groupcode = "";
    public String groupname = "";
    public String school_name = "";


    public int errcode = 0;
    public int msgcode = 0;

    public String getNickName(){
        return nick_name;
    }

    public String getUser_id(){
        return user_id;
    }

    public int getErrcode() {
        return errcode;
    }

    public int getMsgcode(){
        return msgcode;
    }

    public String getDevice_code() {
        return device_code;
    }

    public String getGroupcode() {
        return groupcode;
    }

    public String getGroupname() {
        return groupname;
    }

    public String getSchool_name() {
        return school_name;
    }
}
