package com.nd.android.adhoc.login.exception;

import com.nd.android.adhoc.loginapi.exception.BaseInitException;

public class LoginUserServerException extends BaseInitException {
    public LoginUserServerException(String pMsg) {
        super("LoginUserServerException:"+pMsg);
    }

    public LoginUserServerException(int pCode, String pMsg) {
        super(pCode, "LoginUserServerException:"+pMsg);
    }
}
