package com.nd.android.adhoc.loginapi.exception;

import com.nd.android.adhoc.loginapi.R;

public class AutoLoginMeetUserLoginException extends BaseInitException {
    public AutoLoginMeetUserLoginException(String pMsg) {
        this(0,pMsg);
    }

    public AutoLoginMeetUserLoginException(int pCode, String pMsg) {
        super(pCode, pMsg);
    }

    @Override
    public String getMessage() {
        String msg = super.getMessage();

        return getContext().getString(R.string.exception_login_user_auto, msg);
    }
}
