package com.nd.android.adhoc.login.ui;


import com.nd.android.adhoc.loginapi.ILoginResult;

public interface ILoginPresenter {

    void login(String pUserName, String pPassword);
    void onDestroy();


    interface IView{
        void showLoading();
        void cancelLoading();

        void onLoginSuccess(ILoginResult pResult);
        void onLoginFailed(Throwable pThrowable);
    }
}
