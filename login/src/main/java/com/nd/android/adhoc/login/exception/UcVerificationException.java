package com.nd.android.adhoc.login.exception;

import com.nd.android.adhoc.loginapi.exception.BaseInitException;

/**
 * Created by Administrator on 2018/8/17 0017.
 */

public class UcVerificationException extends BaseInitException {
    public UcVerificationException(String pMsg) {
        super(pMsg);
    }

    public UcVerificationException(int pCode, String pMsg) {
        super(pCode, pMsg);
    }
}
