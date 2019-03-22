package com.nd.android.adhoc.loginapi.exception;

import com.nd.android.adhoc.loginapi.R;

public class QueryDeviceStatusServerException extends BaseInitException {
    public QueryDeviceStatusServerException(String pMsg) {
        super(pMsg);
    }

    public QueryDeviceStatusServerException(int pCode, String pMsg) {
        super(pCode, pMsg);
    }

    @Override
    public String getMessage() {
        String msg =  super.getMessage();

        return getContext().getString(R.string.exception_query_status, msg);
    }
}
