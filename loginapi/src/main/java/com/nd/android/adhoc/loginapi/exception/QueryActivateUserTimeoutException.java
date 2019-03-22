package com.nd.android.adhoc.loginapi.exception;

import com.nd.android.adhoc.loginapi.R;

public class QueryActivateUserTimeoutException extends BaseInitException {
    public QueryActivateUserTimeoutException(){
        super("QueryActivateUserTimeoutException");
    }

    @Override
    public String getMessage() {
        return getContext().getString(R.string.exception_device_id_not_set);
    }
}
