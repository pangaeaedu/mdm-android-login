package com.nd.android.adhoc.login;

public interface ILoginProcessor<T extends IPushLoginResult> {
    void onPushLoginResult(T pResult);
}
