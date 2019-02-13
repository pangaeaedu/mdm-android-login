package com.nd.android.adhoc.loginapi.exception;

public class ConfirmIDServerException extends BaseInitException {
    public ConfirmIDServerException(String pMsg) {
        super(pMsg);
    }

    public ConfirmIDServerException(int pCode, String pMsg) {
        super(pCode, pMsg);
    }
}
