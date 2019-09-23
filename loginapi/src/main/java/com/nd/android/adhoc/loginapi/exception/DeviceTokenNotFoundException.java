package com.nd.android.adhoc.loginapi.exception;

/**
 * Created by Administrator on 2019/9/4 0004.
 */

public class DeviceTokenNotFoundException extends Exception {
    public DeviceTokenNotFoundException() {
        super("device token not found in server");
    }
}
