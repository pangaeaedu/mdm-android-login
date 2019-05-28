package com.nd.android.adhoc.login.basicService.data.http;

/*
    "requestid":"xxxxxxxxx"
    "nick_name":"老王"
    "errcode":0
    "msgcode": 10015 //当errcode=-1时，10015表示没找到
 */
public class GetUserInfoResponse {
    public String requestid = "";
    public String nick_name = "";
    public String user_id = "";

    public int errcode = 0;
    public int msgcode = 0;

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
}
