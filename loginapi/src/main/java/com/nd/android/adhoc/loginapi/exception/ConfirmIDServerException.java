package com.nd.android.adhoc.loginapi.exception;

public class ConfirmIDServerException extends BaseInitException {
    public ConfirmIDServerException(String pMsg) {
        this(0,pMsg);
    }

    public ConfirmIDServerException(int pCode, String pMsg) {
        super(pCode, "ConfirmIDServerException:"+pMsg);
    }
}
