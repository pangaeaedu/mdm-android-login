package com.nd.android.adhoc.loginapi.exception;

public class ConfirmIDServerException extends BaseInitException {
    public ConfirmIDServerException(String pMsg) {
        super("ConfirmIDServerException:"+pMsg);
    }

    public ConfirmIDServerException(int pCode, String pMsg) {
        super(pCode, "ConfirmIDServerException:"+pMsg);
    }
}
