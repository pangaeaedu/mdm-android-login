package com.nd.android.adhoc.login.exception;

import com.nd.android.adhoc.loginapi.exception.BaseInitException;

public class UserBindedException extends BaseInitException {
    public UserBindedException(String pMsg) {
        super(pMsg);
    }

    public UserBindedException(int pCode, String pMsg) {
        super(pCode, pMsg);
    }
}
