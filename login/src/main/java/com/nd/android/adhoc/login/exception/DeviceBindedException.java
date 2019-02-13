package com.nd.android.adhoc.login.exception;

import com.nd.android.adhoc.loginapi.exception.BaseInitException;

public class DeviceBindedException extends BaseInitException {
    public DeviceBindedException(String pMsg) {
        super(pMsg);
    }

    public DeviceBindedException(int pCode, String pMsg) {
        super(pCode, pMsg);
    }
}
