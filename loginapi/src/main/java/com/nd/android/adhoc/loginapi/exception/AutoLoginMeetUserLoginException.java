package com.nd.android.adhoc.loginapi.exception;

public class AutoLoginMeetUserLoginException extends BaseInitException {
    public AutoLoginMeetUserLoginException(String pMsg) {
        this(0,pMsg);
    }

    public AutoLoginMeetUserLoginException(int pCode, String pMsg) {
        super(pCode, "AutoLoginMeetUserLoginException:"+pMsg);
    }
}
