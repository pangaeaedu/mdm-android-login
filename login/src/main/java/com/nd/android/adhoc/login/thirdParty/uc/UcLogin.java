package com.nd.android.adhoc.login.thirdParty.uc;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.nd.android.adhoc.login.basicService.BasicServiceFactory;
import com.nd.android.adhoc.login.basicService.data.http.ActivateHttpResult;
import com.nd.android.adhoc.login.basicService.data.push.UserActivateResult;
import com.nd.android.adhoc.login.basicService.http.IHttpService;
import com.nd.android.adhoc.login.eventListener.IUserActivateListener;
import com.nd.android.adhoc.login.exception.UcLoginCancelException;
import com.nd.android.adhoc.login.exception.UcUserNullException;
import com.nd.android.adhoc.login.thirdParty.IThirdPartyLogin;
import com.nd.android.adhoc.login.thirdParty.IThirdPartyLoginCallBack;
import com.nd.android.adhoc.login.utils.DeviceHelper;
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

    private BehaviorSubject<UserActivateResult> mActivateSubject = BehaviorSubject.create();

    private String mCurSessionID = "";
    private IUserActivateListener mActivateListener = new IUserActivateListener() {
        @Override
        public void onUserActivateResult(UserActivateResult pResult) {
            Log.e(TAG, "onUserActivateResult"+pResult.mSessionID);
            if (TextUtils.isEmpty(mCurSessionID)) {
                return;
            }

            if (!mCurSessionID.equalsIgnoreCase(pResult.mSessionID)) {
                return;
            }

            mActivateSubject.onNext(pResult);
        }
    };

    public UcLogin(@NonNull String pOrgName) {
        if (TextUtils.isEmpty(pOrgName)) {
            throw new RuntimeException("org name is empty");
        }

        mOrgName = pOrgName;
        BasicServiceFactory.getInstance().addActivateListener(mActivateListener);
    }

    @Override
    public void login(@NonNull String pUserName, @NonNull String pPassword,
                      @NonNull final IThirdPartyLoginCallBack pCallBack) {
        UCManager.getInstance()
                .login(pUserName, pPassword, mOrgName, new LoginCallback() {
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

                                            String deviceToken = DeviceHelper.generateDeviceToken();
                                            ActivateHttpResult result = getHttpService()
                                                    .activateUser(deviceToken, deviceToken);
                                            mCurSessionID = result.mSessionID;
                                            Log.e(TAG, "Sessiong ID: "+mCurSessionID);

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
                                                .flatMap(new Func1<UserActivateResult, Observable<UcLoginResult>>() {
                                                    @Override
                                                    public Observable<UcLoginResult> call(UserActivateResult pResult) {
                                                        if (pResult == null) {
                                                            return Observable.error(new Exception
                                                                    ("activate response null"));
                                                        }

                                                        return Observable.just(new UcLoginResult(pUser,
                                                                pResult));
                                                    }
                                                });
                                    }
                                })
                                .compose(RxJavaUtils.<UcLoginResult>applyDefaultSchedulers())
                                .subscribe(new Subscriber<UcLoginResult>() {
                                    @Override
                                    public void onCompleted() {
                                        BasicServiceFactory.getInstance().removeActivateListener(mActivateListener);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        e.printStackTrace();
                                        pCallBack.onFailed(e);
                                        BasicServiceFactory.getInstance().removeActivateListener(mActivateListener);
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

    private IHttpService getHttpService() {
        return BasicServiceFactory.getInstance().getHttpService();
    }
}
