package com.nd.android.adhoc.login.ui;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;

public interface ILoginPresenter {

    void login(String pUserName, String pPassword);
    void onDestroy();


    interface IView{
        void showLoading();
        void cancelLoading();

        void onLoginSuccess(DeviceStatus pResult);
        void onLoginFailed(Throwable pThrowable);
    }
}
