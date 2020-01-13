package com.nd.android.adhoc.login.processOptimization.login;

import rx.Observable;

public interface IUserLogin {
    Observable<IUserLoginResult> login(String pUserName, String pPassword);

    Observable<IUserLoginResult> login(String pUserName, String pPassword, String pValidationCode);
}
