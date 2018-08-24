package com.nd.android.adhoc.login.thirdParty;

import com.nd.android.adhoc.loginapi.ILoginResult;

public interface IThirdPartyLoginCallBack {
    void onSuccess(ILoginResult pResult);
    void onFailed(Throwable pThrowable);
}
