package com.nd.android.adhoc.loginapi.exception;

import com.nd.android.adhoc.loginapi.R;

public class ActivateUserServerException extends BaseInitException {
    public ActivateUserServerException(String pMsg) {
        this(0,pMsg);
    }

    public ActivateUserServerException(int pCode, String pMsg) {
        super(pCode, pMsg);
    }

    @Override
    public String getMessage() {
        String msg = super.getMessage();

        return getContext().getString(R.string.exception_activate_user_server, msg);
    }
}
