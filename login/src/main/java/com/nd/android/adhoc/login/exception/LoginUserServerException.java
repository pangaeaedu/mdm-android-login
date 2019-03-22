package com.nd.android.adhoc.login.exception;

import com.nd.android.adhoc.login.R;
import com.nd.android.adhoc.loginapi.exception.BaseInitException;

public class LoginUserServerException extends BaseInitException {
    public LoginUserServerException(String pMsg) {
        this(0, pMsg);
    }

    public LoginUserServerException(int pCode, String pMsg) {
        super(pCode, pMsg);
    }


    @Override
    public String getMessage() {
        String msg = super.getMessage();

        return getContext().getString(R.string.exception_login_user, msg);
    }
}
