package com.nd.android.adhoc.login.thirdParty;

public interface IThirdPartyLogin {
    void login(String pUserName, String pPassword, IThirdPartyLoginCallBack pCallBack);
}
