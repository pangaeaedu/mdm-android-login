package com.nd.android.adhoc.login.eventListener;

public interface ILoginListener {
    void onSuccess(ILoginResult pResult);
    void onFailed(Throwable pException);
}
