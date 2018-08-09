package com.nd.android.adhoc.login;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.nd.android.adhoc.communicate.impl.MdmTransferFactory;
import com.nd.android.adhoc.communicate.push.listener.IPushConnectListener;
import com.nd.android.adhoc.login.basicService.BasicServiceFactory;
import com.nd.android.adhoc.login.basicService.config.LoginSpConfig;
import com.nd.android.adhoc.login.basicService.http.IBindResult;
import com.nd.android.adhoc.login.basicService.http.IHttpService;
import com.nd.android.adhoc.login.thirdParty.IThirdPartyLogin;
import com.nd.android.adhoc.login.thirdParty.IThirdPartyLoginCallBack;
import com.nd.android.adhoc.login.thirdParty.IThirdPartyLoginResult;
import com.nd.android.adhoc.login.thirdParty.uc.UcLogin;
import com.nd.android.adhoc.login.utils.DeviceHelper;
import com.nd.android.mdm.biz.env.MdmEvnFactory;
import com.nd.smartcan.accountclient.CurrentUser;
import com.nd.smartcan.accountclient.core.AccountException;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

public class LoginManager {
    private static final String TAG = "LoginManager";

    private static final LoginManager ourInstance = new LoginManager();

    public static LoginManager getInstance() {
        return ourInstance;
    }

    private BehaviorSubject<Boolean> mPushConnectSubject = BehaviorSubject.create();
    private BehaviorSubject<Boolean> mReceiveSubject = BehaviorSubject.create();

    private IPushConnectListener mPushConnectListener = new IPushConnectListener() {
        @Override
        public void onConnected() {
            String pushID = MdmTransferFactory.getPushModel().getDeviceId();
            Log.e(TAG, "push sdk connected");
            if(TextUtils.isEmpty(pushID)){
                Log.e(TAG, "return a empty PushID");
                return;
            }

            String existPushID = getConfig().getPushID();
            if(pushID.equalsIgnoreCase(existPushID)){
                Log.e(TAG, "device binded");
                mPushConnectSubject.onNext(true);
                return;
            }

            try {
                onPushIDRetrieved(pushID);
                mPushConnectSubject.onNext(true);
            } catch (Exception pE) {
                pE.printStackTrace();
            }
        }

        @Override
        public void onDisconnected() {
            Log.e(TAG, "push sdk disconnected");
        }
    };

    private LoginManager() {
        startPushSDK();
    }

    private void startPushSDK(){
        MdmTransferFactory.getPushModel().addConnectListener(mPushConnectListener);
        MdmTransferFactory.getPushModel().start();
    }

    public Observable<Boolean> init(){
       return mPushConnectSubject.asObservable()
                .first()
                .map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean pBinded) {
                        if(getConfig().isAutoLogin()){
                            return true;
                        }

                        if(TextUtils.isEmpty(getConfig().getNickname())){
                            return false;
                        }

                        return true;
                    }
                });
    }

    private void onPushIDRetrieved(String pPushID) throws Exception{
        String deviceToken = DeviceHelper.generateDeviceToken();
        String serialNum = DeviceHelper.generateSerialNum();

        if(TextUtils.isEmpty(deviceToken) || TextUtils.isEmpty(serialNum)){
            throw new Exception("deviceToken or serialNum empty");
        }

        IBindResult result = getHttpService().bindDevice(deviceToken, pPushID, serialNum);

        getConfig().saveAutoLogin(result.isAutoLogin());
        getConfig().saveNickname(result.getNickName());
        getConfig().savePushID(pPushID);
        getConfig().saveDeviceToken(deviceToken);
        getConfig().saveSerialNum(serialNum);
    }

    public Observable<Void> login(@NonNull final String pUserName, @NonNull final String pPassword) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(final Subscriber<? super Void> pSubscriber) {
                if (TextUtils.isEmpty(pUserName) || TextUtils.isEmpty(pPassword)) {
                    pSubscriber.onError(new Exception("empty username or password"));
                    return;
                }

                getThirdPartyLogin()
                        .login(pUserName, pPassword, new IThirdPartyLoginCallBack() {
                            @Override
                            public void onSuccess(IThirdPartyLoginResult pResult) {
                                CurrentUser user = (CurrentUser)pResult.getData();
                                try {
                                    String nickname = user.getUserInfo().getNickName();
                                    if(TextUtils.isEmpty(nickname)){
                                        getConfig().saveNickname(pUserName);
                                    } else {
                                        getConfig().saveNickname(nickname);
                                    }
                                } catch (AccountException pE) {
                                    pSubscriber.onError(pE);
                                }
                            }

                            @Override
                            public void onFailed(Throwable pThrowable) {
                                pSubscriber.onError(pThrowable);
                            }
                        });
            }
        });
    }

    private IThirdPartyLogin getThirdPartyLogin(){
        String orgName = MdmEvnFactory.getInstance().getCurEnvironment().getOrg();
        return new UcLogin(orgName);
    }

    private Observable<ILoginResult> getLoginObservable(){
       return Observable.create(new Observable.OnSubscribe<ILoginResult>() {
            @Override
            public void call(Subscriber<? super ILoginResult> pSubscriber) {
                try {
                    boolean isActivated = getConfig().isActivated();
                    if(isActivated){
                        ILoginResult result = new LoginResultImpl();
                        pSubscriber.onNext(result);
                        pSubscriber.onCompleted();
                    }
                }catch (Exception e){
                    pSubscriber.onError(e);
                }
            }
        });
    }
    public void logout(){
        MdmTransferFactory.getPushModel().stop();
    }

    private LoginSpConfig getConfig(){
        return BasicServiceFactory.getInstance().getConfig();
    }

    private IHttpService getHttpService(){
        return BasicServiceFactory.getInstance().getHttpService();
    }
}
