package com.nd.android.adhoc.login.ui;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.login.LoginManager;
import com.nd.android.adhoc.loginapi.ILoginResult;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginPresenterImpl implements ILoginPresenter {

    private IView mView = null;
    private Subscription mSubscription = null;

    public LoginPresenterImpl(@NonNull IView pView){
        mView = pView;
    }

    @Override
    public void login(@NonNull String pUserName, @NonNull String pPassword) {
        if(mSubscription != null){
            mSubscription.unsubscribe();
            mSubscription = null;
        }

        mView.showLoading();
        mSubscription = LoginManager.getInstance().login(pUserName,pPassword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ILoginResult>() {
                    @Override
                    public void onCompleted() {
                        mSubscription = null;
                        if(mView != null){
                            mView.cancelLoading();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mSubscription = null;
                        if(mView != null){
                            mView.cancelLoading();
                            mView.onLoginFailed(e);
                        }
                    }

                    @Override
                    public void onNext(ILoginResult pResult) {
                        if(mView != null){
                            mView.onLoginSuccess(pResult);
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        if(mSubscription != null){
            mSubscription.unsubscribe();
            mSubscription = null;
        }

        mView = null;
    }

}
