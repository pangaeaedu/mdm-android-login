package com.nd.android.adhoc.login.ui;

import androidx.annotation.NonNull;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;
import com.nd.android.adhoc.basic.frame.factory.AdhocFrameFactory;
import com.nd.android.adhoc.loginapi.ILoginApi;
import com.nd.android.adhoc.loginapi.LoginApiRoutePathConstants;

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
        if (mSubscription != null) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }

        mView.showLoading();

        ILoginApi api = (ILoginApi) AdhocFrameFactory.getInstance().getAdhocRouter()
                .build(LoginApiRoutePathConstants.PATH_LOGINAPI_LOGIN).navigation();
        if (api == null) {
            if (mView != null) {
                mView.cancelLoading();
                mView.onLoginFailed(new Exception("login api not found"));
            }
            return;
        }

        mSubscription = api.login(pUserName, pPassword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DeviceStatus>() {
                    @Override
                    public void onCompleted() {
                        mSubscription = null;
                        if (mView != null) {
                            mView.cancelLoading();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mSubscription = null;
                        if (mView != null) {
                            mView.cancelLoading();
                            mView.onLoginFailed(e);
                        }
                    }

                    @Override
                    public void onNext(DeviceStatus pResult) {
                        if (mView != null) {
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
