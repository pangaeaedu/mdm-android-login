package com.nd.android.adhoc.loginapi.exception;

import com.nd.android.adhoc.loginapi.R;

public class ConfirmIDServerException extends BaseInitException {
    public ConfirmIDServerException(String pMsg) {
        this(0,pMsg);
    }

    public ConfirmIDServerException(int pCode, String pMsg) {
        super(pCode, pMsg);
    }

    @Override
    public String getMessage() {
        String msg = super.getMessage();

        return getContext().getString(R.string.exception_confirm_device_id, msg);
    }
}
