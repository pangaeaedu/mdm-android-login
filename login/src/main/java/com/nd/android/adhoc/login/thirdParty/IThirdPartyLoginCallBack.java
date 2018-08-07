package com.nd.android.adhoc.login.thirdParty;

public interface IThirdPartyLoginCallBack {
    void onSuccess(IThirdPartyLoginResult pResult);
    void onFailed(Throwable pThrowable);
}
