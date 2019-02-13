package com.nd.android.adhoc.loginapi.exception;

public class QueryDeviceStatusServerException extends BaseInitException {
    public QueryDeviceStatusServerException(String pMsg) {
        super(pMsg);
    }

    public QueryDeviceStatusServerException(int pCode, String pMsg) {
        super(pCode, pMsg);
    }
}
