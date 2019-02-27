package com.nd.android.adhoc.loginapi.exception;

public class ActivateUserServerException extends BaseInitException {
    public ActivateUserServerException(String pMsg) {
        this(0,pMsg);
    }

    public ActivateUserServerException(int pCode, String pMsg) {
        super(pCode, "ActivateUserServerException:"+pMsg);
    }
}
