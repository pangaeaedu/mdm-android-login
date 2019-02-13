package com.nd.android.adhoc.login.exception;

import com.nd.android.adhoc.loginapi.exception.BaseInitException;

public class SimOrOtherException extends BaseInitException {
    public SimOrOtherException(String pMsg) {
        super(pMsg);
    }

    public SimOrOtherException(int pCode, String pMsg) {
        super(pCode, pMsg);
    }
}
