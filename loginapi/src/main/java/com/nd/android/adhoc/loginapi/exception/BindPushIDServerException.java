package com.nd.android.adhoc.loginapi.exception;

import com.nd.android.adhoc.loginapi.R;

public class BindPushIDServerException extends BaseInitException {

    public BindPushIDServerException(String pMsg){
        super(pMsg);
    }

    @Override
    public String getMessage() {
        String msg = super.getMessage();

        return getContext().getString(R.string.exception_bind_push_id, msg);
    }
}
