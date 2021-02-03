package com.nd.android.adhoc.login.processOptimization.login;

import android.text.TextUtils;
import android.util.Log;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceIDEncryptUtils;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.login.basicService.BasicServiceFactory;
import com.nd.android.adhoc.login.basicService.data.http.LoginUserResponse;
import com.nd.android.adhoc.login.basicService.http.IHttpService;
import com.nd.android.adhoc.loginapi.IUserLoginInterceptor;
import com.nd.android.adhoc.loginapi.exception.UserLoginInterruptException;
import com.nd.sdp.android.serviceloader.AnnotationServiceLoader;

import java.util.Iterator;

import rx.Observable;
import rx.Subscriber;

public class UserLoginThroughServer implements IUserLogin {
    @Override
    public Observable<IUserLoginResult> login(final String pUserName,
                                              final String pPassword) {
        if(TextUtils.isEmpty(pUserName) || TextUtils.isEmpty(pPassword)){
           return Observable.error(new LoginUserOrPwdEmptyException());
        }

        return Observable.create(new Observable.OnSubscribe<IUserLoginResult>() {
            @Override
            public void call(Subscriber<? super IUserLoginResult> pSubscriber) {
                try {
                    String encryptUserName = DeviceIDEncryptUtils.encrypt(pUserName);
                    String encryptPassword = DeviceIDEncryptUtils.encryptPassword(pPassword);

                    LoginUserResponse result = getHttpService().login(encryptUserName,
                            encryptPassword);

                    IUserLoginResult returnResult = new UserLoginResultImpl(result);

                    Iterator<IUserLoginInterceptor> interceptors = AnnotationServiceLoader
                            .load(IUserLoginInterceptor.class).iterator();
                    if (!interceptors.hasNext()) {
                        Logger.i("UserIntercept", "interceptor not found");
                        pSubscriber.onNext(returnResult);
                        pSubscriber.onCompleted();
                        return;
                    }

                    IUserLoginInterceptor firstInterceptor = interceptors.next();
                    boolean needContinue = firstInterceptor.isNeedContinueLogin(pUserName,
                            pPassword);
                    Logger.i("UserIntercept", "interceptor found");
                    if(needContinue){
                        pSubscriber.onNext(returnResult);
                        pSubscriber.onCompleted();
                        return;
                    }

                    pSubscriber.onError(new UserLoginInterruptException());
//                    pSubscriber.onNext(returnResult);
//                    pSubscriber.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
//                    CrashAnalytics.INSTANCE.reportException(e);
                    pSubscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<IUserLoginResult> login(String pUserName, String pPassword, String pValidationCode) {
        return login(pUserName, pPassword);
    }

    private IHttpService getHttpService(){
        return BasicServiceFactory.getInstance().getHttpService();
    }
}
