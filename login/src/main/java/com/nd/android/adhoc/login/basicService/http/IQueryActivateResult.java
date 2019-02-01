package com.nd.android.adhoc.login.basicService.http;

import com.nd.android.adhoc.login.processOptimization.ActivateUserError;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;

public interface IQueryActivateResult {
    boolean isSuccess();
    ActivateUserError getActivateError();
    DeviceStatus getDeviceStatus();
}
