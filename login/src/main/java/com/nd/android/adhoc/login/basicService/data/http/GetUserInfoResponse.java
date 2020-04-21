package com.nd.android.adhoc.login.basicService.data.http;


//        "requestid":"xxxxxxxxx"
//        "errcode":0
//        "msgcode": 10015 //当errcode=-1时，10015表示没找到
//        "nick_name":"老王"
//        "user_id":"xxxxxx"
//        "device_code": "11" //获取设备编码，需要移动端补齐位数

public class GetUserInfoResponse {
    public String requestid = "";
    public String nick_name = "";
    public String user_id = "";

    // 设备编码，根据OMO 需求新增的一个字段 -- by hyk 20200410
    public String device_code = "";

    public int errcode = 0;
    public int msgcode = 0;

    public void setNick_name(String strNickName){
        nick_name = strNickName;
    }

    public String getNickName(){
        return nick_name;
    }

    public String getUser_id(){
        return user_id;
    }

    public boolean isSuccess(){
        return errcode == 0;
    }

    public int getMsgcode(){
        return msgcode;
    }

    public String getDevice_code() {
        return device_code;
    }
}
