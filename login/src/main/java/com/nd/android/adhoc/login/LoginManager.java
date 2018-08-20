package com.nd.android.adhoc.login;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.nd.android.adhoc.communicate.impl.MdmTransferFactory;
import com.nd.android.adhoc.communicate.push.listener.IPushConnectListener;
import com.nd.android.adhoc.login.basicService.BasicServiceFactory;
import com.nd.android.adhoc.login.basicService.operator.UserActivateOperator;
import com.nd.android.adhoc.login.basicService.config.LoginSpConfig;
import com.nd.android.adhoc.login.basicService.http.IBindResult;
import com.nd.android.adhoc.login.basicService.http.IHttpService;
import com.nd.android.adhoc.login.thirdParty.IThirdPartyLogin;
import com.nd.android.adhoc.login.thirdParty.IThirdPartyLoginCallBack;
import com.nd.android.adhoc.login.thirdParty.IThirdPartyLoginResult;
import com.nd.android.adhoc.login.thirdParty.uc.UcLogin;
import com.nd.android.adhoc.login.utils.DeviceHelper;
import com.nd.android.mdm.biz.env.MdmEvnFactory;
import com.nd.android.mdm.mdm_feedback_biz.MdmFeedbackReceiveFactory;

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

    private BehaviorSubject<Boolean> mConnectSubject = BehaviorSubject.create();

    private IPushConnectListener mPushConnectListener = new IPushConnectListener() {
        @Override
        public void onConnected() {
            synchronized (LoginManager.getInstance()) {
                String pushID = MdmTransferFactory.getPushModel().getDeviceId();
                Log.e(TAG, "push sdk connected");
                if (TextUtils.isEmpty(pushID)) {
                    Log.e(TAG, "return a empty PushID");
                    return;
                }

                String existPushID = getConfig().getPushID();
                if (pushID.equalsIgnoreCase(existPushID)) {
                    Log.e(TAG, "device binded");
                    mConnectSubject.onNext(true);
                    return;
                }

                try {
                    bindDeviceAfterReceiveNewPushID(pushID);
                    mConnectSubject.onNext(true);
                } catch (Exception pE) {
                    pE.printStackTrace();
                    mConnectSubject.onNext(false);
                }
            }
        }

        @Override
        public void onDisconnected() {
            Log.e(TAG, "push sdk disconnected");
        }
    };

    private LoginManager() {
        MdmFeedbackReceiveFactory.addCmdOperator(new UserActivateOperator());

        MdmTransferFactory.getPushModel().addConnectListener(mPushConnectListener);

        MdmTransferFactory.getPushModel().start();
    }

    public Observable<Boolean> init(){
       return mConnectSubject.asObservable()
                .first()
                .map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean pBinded) {
                        if(!pBinded){
                            return false;
                        }
                        if(getConfig().isAutoLogin()){
                            return true;
                        }

                       return false;
                    }
                });
    }

    private void bindDeviceAfterReceiveNewPushID(String pPushID) throws Exception{
        String deviceToken = DeviceHelper.getDeviceToken();
        String serialNum = DeviceHelper.generateSerialNum();

        if(TextUtils.isEmpty(deviceToken) || TextUtils.isEmpty(serialNum)){
            throw new Exception("deviceToken or serialNum empty");
        }

        IBindResult result = getHttpService().bindDevice(deviceToken, pPushID, serialNum);

        getConfig().saveAutoLogin(result.isAutoLogin());
        if(!result.isAutoLogin()){
            getConfig().saveNickname("");
        } else {
            getConfig().saveNickname(result.getNickName());
        }
        getConfig().savePushID(pPushID);
        getConfig().saveDeviceToken(deviceToken);
        getConfig().saveSerialNum(serialNum);
    }

    public Observable<IThirdPartyLoginResult> login(@NonNull final String pUserName, @NonNull final String pPassword) {
        return Observable.create(new Observable.OnSubscribe<IThirdPartyLoginResult>() {
            @Override
            public void call(final Subscriber<? super IThirdPartyLoginResult> pSubscriber) {
                try {
                    if (TextUtils.isEmpty(pUserName) || TextUtils.isEmpty(pPassword)) {
                        pSubscriber.onError(new Exception("empty username or password"));
                        return;
                    }

                    String pushID = MdmTransferFactory.getPushModel().getDeviceId();
                    String existPushID = getConfig().getPushID();

                    if(TextUtils.isEmpty(pushID)){
                        pSubscriber.onError(new Exception("get push id empty"));
                        return;
                    }

                    if(!pushID.equalsIgnoreCase(existPushID)){
                        bindDeviceAfterReceiveNewPushID(pushID);
                    }

                    getThirdPartyLogin()
                            .login(pUserName, pPassword, new IThirdPartyLoginCallBack() {
                                @Override
                                public void onSuccess(IThirdPartyLoginResult pResult) {
                                    String deviceToken = DeviceHelper.getDeviceToken();
                                    try {
                                        getHttpService().requestPolicy(deviceToken);

                                        getConfig().saveNickname(pUserName);
                                        pSubscriber.onNext(pResult);
                                    } catch (Exception pE) {
                                        pE.printStackTrace();
                                    }

                                }

                                @Override
                                public void onFailed(Throwable pThrowable) {
                                    pSubscriber.onError(pThrowable);
                                }
                            });
                }catch (Exception e){
                    pSubscriber.onError(e);
                }

            }
        });
    }

    public void logout(){
        getConfig().clearData();
        mConnectSubject = BehaviorSubject.create();
        MdmTransferFactory.getPushModel().start();
    }

    public boolean isActivated(){
        return false;
    }

    private IThirdPartyLogin getThirdPartyLogin(){
        String orgName = MdmEvnFactory.getInstance().getCurEnvironment().getOrg();
        return new UcLogin(orgName);
    }

    private LoginSpConfig getConfig(){
        return BasicServiceFactory.getInstance().getConfig();
    }

    private IHttpService getHttpService(){
        return BasicServiceFactory.getInstance().getHttpService();
    }

}
