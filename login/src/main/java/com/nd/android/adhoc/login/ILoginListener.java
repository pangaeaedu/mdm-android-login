package com.nd.android.adhoc.login;

public interface ILoginListener {
    void onSuccess(ILoginResult pResult);
    void onFailed(Throwable pException);
}
