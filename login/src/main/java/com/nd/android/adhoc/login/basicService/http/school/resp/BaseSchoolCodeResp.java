package com.nd.android.adhoc.login.basicService.http.school.resp;


public class BaseSchoolCodeResp {
    private int errcode = 0;
    private String msg = "";

    public BaseSchoolCodeResp() {

    }

    public String getMsg() {
        return msg;
    }

    public int getErrcode() {
        return errcode;
    }
}
