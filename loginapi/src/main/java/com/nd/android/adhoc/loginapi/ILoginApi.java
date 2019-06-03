package com.nd.android.adhoc.loginapi;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;
import com.nd.android.adhoc.router_api.facade.template.IProvider;

import rx.Observable;

public interface ILoginApi extends IProvider {
//    Observable<ILoginResult> login(@NonNull final String pUserName,
//                                   @NonNull final String pPassword);
    void enterLoginUI(@NonNull Context pContext);
    void logout();

    Observable<DeviceStatus> login(@NonNull final String pUserName,
                                   @NonNull final String pPassword);

    Observable<String> getNickName();

    Observable<String> getUserID();

    Observable<UserInfo> getUserInfo();
}
