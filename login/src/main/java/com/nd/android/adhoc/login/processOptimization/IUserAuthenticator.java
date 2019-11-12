package com.nd.android.adhoc.login.processOptimization;

import android.support.annotation.NonNull;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;

import rx.Observable;

public interface IUserAuthenticator {

    Observable<DeviceStatus> login(@NonNull final String pUserName,
                                   @NonNull final String pPassword);

    Observable<DeviceStatus> login(@NonNull final String pUserName,
                                   @NonNull final String pPassword,
                                   @NonNull final String pValidationCode);
    void logout();
}
