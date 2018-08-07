package com.nd.android.adhoc.login.thirdParty.uc;

import android.support.annotation.NonNull;
import android.util.Log;

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

public class UcLogin implements IThirdPartyLogin {
    private static final String TAG = "UcLogin";
    protected String mOrgName = "";

    public UcLogin(String pOrgName){
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

                Observable.create(new Observable.OnSubscribe<CurrentUser>() {
                    @Override
                    public void call(Subscriber<? super CurrentUser> subscriber) {
                        //目前只支持整数格式的id
                        try {
                            User user = currentUser.getUserInfo();
                            if (user == null) {
                                subscriber.onError(new UcUserNullException());
                                return;
                            }

//                            //步骤2：获取UCtoken
//                            String mUserToken = currentUser.getMacToken().getAccessToken();
//                            Log.e("HYK", "UCManager onSuccess: mUserToken = " + mUserToken);
//                            if (TextUtils.isEmpty(mUserToken)) {
//                                subscriber.onError(new UcUserNullException());
//                                return;
//                            }
//
//                            String nickname = user.getNickName();
//                            if (!TextUtils.isEmpty(nickname)) {
//                                mNickName = nickname;
//                            } else {
//                                // 保證 nickname 有值
//                                mNickName = mAccount;
//                            }
                            subscriber.onNext(currentUser);
                            subscriber.onCompleted();

                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                })
                        .compose(RxJavaUtils.<CurrentUser>applyDefaultSchedulers())
                        .subscribe(new Subscriber<CurrentUser>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                pCallBack.onFailed(e);
                            }

                            @Override
                            public void onNext(CurrentUser pUser) {
                                // 这里只是回调登录成功，然后由上层去处理登录之后要做的操作。
                                // 例如：弹出一个 设备校验的 对话框进行校验 -- HYK 20180312
//                                pCallback.onSuccess(mAccount, mPassword);
                                UcLoginResult result = new UcLoginResult(pUser);
                                pCallBack.onSuccess(result);
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
}
