package com.nd.android.adhoc.login.thirdParty.uc;

import android.support.annotation.NonNull;
import android.util.Log;

import com.nd.android.adhoc.login.basicService.ActivateArgument;
import com.nd.android.adhoc.login.basicService.BasicServiceFactory;
import com.nd.android.adhoc.login.basicService.data.ActivateCmdData;
import com.nd.android.adhoc.login.basicService.http.IHttpService;
import com.nd.android.adhoc.login.exception.UcLoginCancelException;
import com.nd.android.adhoc.login.exception.UcUserNullException;
import com.nd.android.adhoc.login.thirdParty.IThirdPartyLogin;
import com.nd.android.adhoc.login.thirdParty.IThirdPartyLoginCallBack;
import com.nd.sdp.im.common.utils.rx.RxJavaUtils;
import com.nd.smartcan.accountclient.CurrentUser;
import com.nd.smartcan.accountclient.LoginCallback;
import com.nd.smartcan.accountclient.UCManager;
import com.nd.smartcan.accountclient.core.AccountException;
import com.nd.smartcan.accountclient.core.User;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

public class UcLogin implements IThirdPartyLogin {
    private static final String TAG = "UcLogin";
    protected String mOrgName = "";

    private BehaviorSubject<ActivateCmdData> mActivateSubject = BehaviorSubject.create();

    public UcLogin(String pOrgName) {
        mOrgName = pOrgName;

    }

    @Override
    public void login(@NonNull String pUserName, @NonNull String pPassword,
                      @NonNull final IThirdPartyLoginCallBack pCallBack) {
        UCManager.getInstance().login(pUserName, pPassword, mOrgName, new LoginCallback() {
            @Override
            public void onSuccess(final CurrentUser currentUser) {
                if (currentUser == null) {
                    pCallBack.onFailed(new UcUserNullException());
                    return;
                }
                Log.e(TAG, "UCManager login: onSuccess");
                Observable
                        .create(new Observable.OnSubscribe<CurrentUser>() {
                            @Override
                            public void call(Subscriber<? super CurrentUser> subscriber) {
                                try {
                                    User user = currentUser.getUserInfo();
                                    if (user == null) {
                                        subscriber.onError(new UcUserNullException());
                                        return;
                                    }

                                    ActivateArgument argument =
                                            new ActivateArgument("", "", currentUser);
                                    getHttpService().activateUser(argument);

                                    subscriber.onNext(currentUser);
                                    subscriber.onCompleted();
                                } catch (Exception e) {
                                    subscriber.onError(e);
                                }
                            }
                        })
                        .flatMap(new Func1<CurrentUser, Observable<UcLoginResult>>() {
                            @Override
                            public Observable<UcLoginResult> call(final CurrentUser pUser) {
                                return mActivateSubject.asObservable().first()
                                        .flatMap(new Func1<ActivateCmdData, Observable<UcLoginResult>>() {
                                            @Override
                                            public Observable<UcLoginResult> call(ActivateCmdData pActivateCmdData) {
                                                if (pActivateCmdData == null) {
                                                    return Observable.error(new Exception("cmd " + "data null"));
                                                }
                                                return Observable.just(new UcLoginResult(pUser,
                                                        pActivateCmdData));
                                            }
                                        });
                            }
                        })
                        .compose(RxJavaUtils.<UcLoginResult>applyDefaultSchedulers())
                        .subscribe(new Subscriber<UcLoginResult>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                pCallBack.onFailed(e);
                            }

                            @Override
                            public void onNext(UcLoginResult pLoginResult) {
                                pCallBack.onSuccess(pLoginResult);
                            }
                        });
            }

            @Override
            public void onCanceled() {
                pCallBack.onFailed(new UcLoginCancelException());
            }

            @Override
            public void onFailed(AccountException e) {
                pCallBack.onFailed(e);
            }
        });
    }

    private IHttpService getHttpService(){
        return BasicServiceFactory.getInstance().getHttpService();
    }
}
