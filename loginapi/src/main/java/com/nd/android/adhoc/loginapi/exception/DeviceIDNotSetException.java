package com.nd.android.adhoc.loginapi.exception;

import com.nd.android.adhoc.loginapi.R;

public class DeviceIDNotSetException extends BaseInitException {
    public DeviceIDNotSetException(){
        super("Device ID Not Set");
    }

    @Override
    public String getMessage() {
        return getContext().getString(R.string.exception_device_id_not_set);
    }
}
