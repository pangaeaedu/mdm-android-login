package com.nd.android.adhoc.login;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.nd.adhoc.assistant.sdk.AssistantBasicServiceFactory;
import com.nd.adhoc.assistant.sdk.config.AssistantSpConfig;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceHelper;
import com.nd.android.adhoc.basic.frame.api.user.IAdhocLoginStatusNotifier;
import com.nd.android.adhoc.basic.frame.constant.AdhocRouteConstant;
import com.nd.android.adhoc.basic.frame.factory.AdhocFrameFactory;
import com.nd.android.adhoc.communicate.impl.MdmTransferFactory;
import com.nd.android.adhoc.communicate.push.listener.IPushConnectListener;
import com.nd.android.adhoc.login.basicService.BasicServiceFactory;
import com.nd.android.adhoc.login.basicService.http.IBindResult;
import com.nd.android.adhoc.login.basicService.http.IHttpService;
import com.nd.android.adhoc.login.basicService.operator.UserActivateOperator;
import com.nd.android.adhoc.login.info.AdhocLoginInfoImpl;
import com.nd.android.adhoc.login.info.AdhocUserInfoImpl;
import com.nd.android.adhoc.login.thirdParty.IThirdPartyLogin;
import com.nd.android.adhoc.login.thirdParty.IThirdPartyLoginCallBack;
import com.nd.android.adhoc.login.thirdParty.uc.UcLogin;
import com.nd.android.adhoc.login.thirdParty.uc.UcLoginResult;
import com.nd.android.adhoc.login.utils.Constants;
import com.nd.android.adhoc.login.utils.EnvUtils;
import com.nd.android.adhoc.loginapi.ILoginInfoProvider;
import com.nd.android.adhoc.loginapi.ILoginResult;
import com.nd.android.mdm.biz.env.MdmEvnFactory;
import com.nd.android.mdm.mdm_feedback_biz.MdmFeedbackReceiveFactory;
import com.nd.smartcan.accountclient.UCManager;

import org.json.JSONObject;

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

                Log.e(TAG, "pushid:"+pushID);

                String existPushID = getConfig().getPushID();
                if (pushID.equalsIgnoreCase(existPushID)) {
                    Log.e(TAG, "device binded:"+existPushID);
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

    private void initUcEnv(){
        UCManager.getInstance().setOrgName(Constants.ORG_NAME);
        EnvUtils.setUcEnv(MdmEvnFactory.getInstance().getCurIndex());
    }

    public Observable<Boolean> init(){
        initUcEnv();
       return mConnectSubject.asObservable()
                .first()
                .map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean pBinded) {
                        if(!pBinded){
                            return false;
                        }

                        if(getConfig().isActivated()){
                            try{
                                requestPolicySet();
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            String accountNum = getConfig().getAccountNum();
                            String nickname = getConfig().getNickname();
                            notifyLogin(accountNum, nickname);
                            return true;
                        }

                        return false;
                    }
                });
    }

    private void bindDeviceAfterReceiveNewPushID(String pPushID) throws Exception{
        String deviceToken = DeviceHelper.getDeviceToken();
        String serialNum = DeviceHelper.getSerialNumber();

        if(TextUtils.isEmpty(deviceToken) || TextUtils.isEmpty(serialNum)){
            throw new Exception("deviceToken or serialNum empty");
        }

        IBindResult result = getHttpService().bindDevice(deviceToken, pPushID, serialNum);

        getConfig().saveAutoLogin(result.isAutoLogin());
        if(!result.isAutoLogin()){
            getConfig().saveNickname("");
            getConfig().saveActivated(false);
        } else {
            getConfig().saveNickname(result.getNickName());
            getConfig().saveActivated(true);
        }
        getConfig().savePushID(pPushID);
        getConfig().saveDeviceToken(deviceToken);
        getConfig().saveSerialNum(serialNum);
    }

    public Observable<ILoginResult> login(@NonNull final String pUserName, @NonNull final String pPassword) {
        return Observable.create(new Observable.OnSubscribe<ILoginResult>() {
            @Override
            public void call(final Subscriber<? super ILoginResult> pSubscriber) {
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
                                public void onSuccess(ILoginResult pResult) {
//                                    String deviceToken = DeviceHelper.getDeviceToken();
                                    try {
//                                        getHttpService().requestPolicy(deviceToken);
                                        requestPolicySet();
                                        getConfig().saveAccountNum(pUserName);

                                        String name = ((UcLoginResult)pResult).getUser()
                                                .getUserInfo().getNickName();
                                        getConfig().saveNickname(name);
                                        getConfig().saveActivated(true);

                                        notifyLogin(pUserName, name);
                                        pSubscriber.onNext(pResult);
                                    } catch (Exception pE) {
                                        pE.printStackTrace();
                                        pSubscriber.onError(pE);
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

    private void requestPolicySet() throws Exception{
        String deviceToken = DeviceHelper.getDeviceToken();
        long pTime = getConfig().getPolicySetTime();
        ILoginInfoProvider provider   = (ILoginInfoProvider) AdhocFrameFactory.getInstance().getAdhocRouter()
                .build(ILoginInfoProvider.PATH).navigation();
        if(provider == null){
            throw new Exception("login info provider not exist");
        }

        JSONObject object = provider.getDeviceInfo();
        getHttpService().requestPolicy(deviceToken, pTime, object);
    }


    private void notifyLogin(String pAccountNum, String pNickName){
        IAdhocLoginStatusNotifier api = (IAdhocLoginStatusNotifier) AdhocFrameFactory.getInstance().getAdhocRouter()
                .build(AdhocRouteConstant.PATH_LOGIN_STATUS_NOTIFIER).navigation();
        if(api == null){
            return;
        }

        AdhocUserInfoImpl userInfo = new AdhocUserInfoImpl(pAccountNum, pNickName);
        AdhocLoginInfoImpl loginInfo = new AdhocLoginInfoImpl(userInfo, null);
        api.onLogin(loginInfo);
    }

    public void logout(){
        getConfig().clearData();
        mConnectSubject = BehaviorSubject.create();
        MdmTransferFactory.getPushModel().start();
    }

    private IThirdPartyLogin getThirdPartyLogin(){
        String orgName = MdmEvnFactory.getInstance().getCurEnvironment().getOrg();
        return new UcLogin(orgName);
    }

    private AssistantSpConfig getConfig(){
        return AssistantBasicServiceFactory.getInstance().getSpConfig();
    }

    private IHttpService getHttpService(){
        return BasicServiceFactory.getInstance().getHttpService();
    }

}
