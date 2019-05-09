package com.nd.android.adhoc.login.processOptimization.login;

import android.text.TextUtils;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceIDEncryptUtils;
import com.nd.android.adhoc.basic.log.CrashAnalytics;
import com.nd.android.adhoc.login.basicService.BasicServiceFactory;
import com.nd.android.adhoc.login.basicService.data.http.LoginUserResponse;
import com.nd.android.adhoc.login.basicService.http.IHttpService;

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
                    pSubscriber.onNext(returnResult);
                    pSubscriber.onCompleted();
                }catch (Exception e){
                    CrashAnalytics.INSTANCE.reportException(e);
                    pSubscriber.onError(e);
                }
            }
        });
    }

    private IHttpService getHttpService(){
        return BasicServiceFactory.getInstance().getHttpService();
    }
}