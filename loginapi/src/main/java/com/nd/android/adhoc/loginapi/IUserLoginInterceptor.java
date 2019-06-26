package com.nd.android.adhoc.loginapi;

public interface IUserLoginInterceptor {
    boolean isNeedContinueLogin(String pRawUserName, String pRawPassword) throws Exception;
}
