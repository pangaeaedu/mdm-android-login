package com.nd.android.adhoc.login;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.nd.adhoc.assistant.sdk.AssistantBasicServiceFactory;
import com.nd.adhoc.assistant.sdk.config.AssistantSpConfig;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceHelper;
import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.frame.api.user.IAdhocLoginStatusNotifier;
import com.nd.android.adhoc.basic.frame.constant.AdhocRouteConstant;
import com.nd.android.adhoc.basic.frame.factory.AdhocFrameFactory;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.util.system.AdhocDeviceUtil;
import com.nd.android.adhoc.basic.util.thread.AdhocRxJavaUtil;
import com.nd.android.adhoc.communicate.impl.MdmTransferFactory;
import com.nd.android.adhoc.communicate.push.IPushModule;
import com.nd.android.adhoc.communicate.push.listener.IPushConnectListener;
import com.nd.android.adhoc.login.basicService.BasicServiceFactory;
import com.nd.android.adhoc.login.basicService.data.http.GetOldTokenResult;
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
import rx.Observer;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
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
            AdhocRxJavaUtil.safeSubscribe(Observable.create(new Observable.OnSubscribe<Object>() {
                @Override
                public void call(Subscriber<? super Object> pSubscriber) {
                    try {
                        doOnPushChannelConnected();
                        pSubscriber.onNext(null);
                        pSubscriber.onCompleted();
                    }catch (Exception e){
                        pSubscriber.onError(e);
                    }
                }
            }).subscribeOn(Schedulers.io()));
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

    private void initUcEnv() {
        UCManager.getInstance().setOrgName(Constants.ORG_NAME);
        EnvUtils.setUcEnv(MdmEvnFactory.getInstance().getCurIndex());
    }

    public Observable<Boolean> init() {
        initUcEnv();
        return startToBindDevice();
    }

    private Observable<Boolean> startToBindDevice() {
        boolean connected = MdmTransferFactory.getPushModel().isConnected();
        boolean activated = getConfig().isActivated();
        Observable<Boolean> obs = mConnectSubject.asObservable()
                .first()
                .map(loginFunc());

        //已连接或者已激活
        if (connected || activated) {
            Observable
                    .create(new Observable.OnSubscribe<Void>() {
                        @Override
                        public void call(Subscriber<? super Void> pSubscriber) {
                            doOnPushChannelConnected();
                            pSubscriber.onNext(null);
                            pSubscriber.onCompleted();
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<Void>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(Void pVoid) {

                        }
                    });
        }

        return obs;
    }

    protected void doOnPushChannelConnected() {
        synchronized (LoginManager.getInstance()) {
            String pushID = MdmTransferFactory.getPushModel().getDeviceId();
            Log.e(TAG, "push sdk connected");
            if (TextUtils.isEmpty(pushID)) {
                Log.e(TAG, "return a empty PushID");
                return;
            }

            Log.e(TAG, "pushid:" + pushID);

            String existPushID = getConfig().getPushID();
            if (pushID.equalsIgnoreCase(existPushID)) {
                Log.e(TAG, "device binded:" + existPushID);
                mConnectSubject.onNext(true);
                return;
            }

            try {
                bindWithNewPushIDReturnLoginStatus(pushID);
                mConnectSubject.onNext(true);
            } catch (Exception pE) {
                pE.printStackTrace();
                mConnectSubject.onNext(false);
            }
        }
    }

    private Func1<Boolean, Boolean> loginFunc() {
        return new Func1<Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean pBinded) {
                if (!pBinded) {
                    return false;
                }

                if (getConfig().isActivated()) {
                    try {
                        requestPolicySet();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String accountNum = getConfig().getAccountNum();
                    String nickname = getConfig().getNickname();
                    notifyLogin(accountNum, nickname);
                    return true;
                }

                return false;
            }
        };
    }

    private String getDeviceToken() throws Exception {
        String deviceToken = DeviceHelper.getDeviceToken();

        if (TextUtils.isEmpty(deviceToken)) {
            throw new Exception("deviceToken or serialNum empty");
        }

        String oldToken = getConfig().getOldDeviceToken();
        int oldTokenStatus = getConfig().getOldTokenStatus();

        if (oldTokenStatus == 2) {
            if (!TextUtils.isEmpty(oldToken)) {
                deviceToken = oldToken;
            }
        } else {
            String buildSn = AdhocDeviceUtil.getBuildSN(AdhocBasicConfig.getInstance().getAppContext());
            String cpuSn = AdhocDeviceUtil.getCpuSN();
            String imei = AdhocDeviceUtil.getIMEI(AdhocBasicConfig.getInstance().getAppContext());
            String wifiMac = AdhocDeviceUtil.getWifiMac(AdhocBasicConfig.getInstance().getAppContext());
            String blueToothMac = AdhocDeviceUtil.getBloothMac();
            String serialNo = AdhocDeviceUtil.getSerialNumber();
            String Token = DeviceHelper.getDeviceTokenFromSystem();

            GetOldTokenResult oldTokenResult = getHttpService().getOldDeviceToken(buildSn,
                    cpuSn, imei, wifiMac, blueToothMac, serialNo, Token);

            oldToken = oldTokenResult.getOld_device_token();
            getConfig().saveOldDeviceToken(oldToken);
            getConfig().saveOldTokenStatus(2);

            Log.e(TAG, "OldToken:" + oldTokenResult.getOld_device_token()
                    + " " + "Status:" + oldTokenResult.getStatus()
                    + " nickname:" + oldTokenResult.getNick_name()
                    + " pushID:" + oldTokenResult.getPush_id());
            if (!TextUtils.isEmpty(oldToken)) {
                deviceToken = oldToken;
                getConfig().saveNickname(oldTokenResult.getNick_name());
                getConfig().saveActivated(true);
            }
        }

        return deviceToken;
    }

    private boolean bindWithNewPushIDReturnLoginStatus(String pPushID) throws Exception {
        String serialNum = DeviceHelper.getSerialNumber();

        if (TextUtils.isEmpty(serialNum)) {
            throw new Exception("serialNum empty");
        }

        try {
            String deviceToken = getDeviceToken();

            IPushModule pushModule = MdmTransferFactory.getPushModel();
            IBindResult result = getHttpService().bindDeviceWithChannelType(deviceToken, pPushID,
                    serialNum, pushModule.getChannelType());

            getConfig().saveAutoLogin(result.isAutoLogin());
            if (result.isAutoLogin()) {
                getConfig().saveNickname(result.getNickName());
                getConfig().saveActivated(true);
            }

            getConfig().savePushID(pPushID);
            getConfig().saveDeviceToken(deviceToken);
            getConfig().saveSerialNum(serialNum);

            if(getConfig().isActivated()){
                return true;
            }

            return result.isAutoLogin();
        } catch (Exception e) {
            getConfig().clearData();

            throw e;
        }
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

                    if (TextUtils.isEmpty(pushID)) {
                        pSubscriber.onError(new Exception("get push id empty"));
                        return;
                    }

                    if (!pushID.equalsIgnoreCase(existPushID)) {
                        boolean bAuto = bindWithNewPushIDReturnLoginStatus(pushID);
                        if (bAuto) {
                            requestPolicySet();
                            notifyLogin(getConfig().getAccountNum(), getConfig().getNickname());
                            pSubscriber.onNext(null);
                            pSubscriber.onCompleted();
                            return;
                        }
                    }

                    getThirdPartyLogin()
                            .login(pUserName, pPassword, new IThirdPartyLoginCallBack() {
                                @Override
                                public void onSuccess(ILoginResult pResult) {
                                    try {
                                        requestPolicySet();
                                        getConfig().saveAccountNum(pUserName);

                                        String name = ((UcLoginResult) pResult).getUser()
                                                .getUserInfo().getNickName();
                                        getConfig().saveNickname(name);
                                        getConfig().saveActivated(true);

                                        notifyLogin(pUserName, name);
                                        pSubscriber.onNext(pResult);
                                        pSubscriber.onCompleted();
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
                } catch (Exception e) {
                    pSubscriber.onError(e);
                }

            }
        });
    }

    private void requestPolicySet() throws Exception {
        Logger.e(TAG, "requestPolicySet");
        String deviceToken = DeviceHelper.getDeviceToken();
        long pTime = getConfig().getPolicySetTime();
        ILoginInfoProvider provider = (ILoginInfoProvider) AdhocFrameFactory.getInstance().getAdhocRouter()
                .build(ILoginInfoProvider.PATH).navigation();
        if (provider == null) {
            throw new Exception("login info provider not exist");
        }

        JSONObject object = provider.getDeviceInfo();
        getHttpService().requestPolicy(deviceToken, pTime, object);
    }


    private void notifyLogin(String pAccountNum, String pNickName) {
        IAdhocLoginStatusNotifier api = (IAdhocLoginStatusNotifier) AdhocFrameFactory.getInstance().getAdhocRouter()
                .build(AdhocRouteConstant.PATH_LOGIN_STATUS_NOTIFIER).navigation();
        if (api == null) {
            return;
        }

        AdhocUserInfoImpl userInfo = new AdhocUserInfoImpl(pAccountNum, pNickName);
        AdhocLoginInfoImpl loginInfo = new AdhocLoginInfoImpl(userInfo, null);
        api.onLogin(loginInfo);
    }

    public void logout() {
        getConfig().clearData();
        mConnectSubject = BehaviorSubject.create();
        MdmTransferFactory.getPushModel().stop();
//        AdhocImplement.getInstance().stop();
//        boolean connected = MdmTransferFactory.getPushModel().isConnected();
//        if (connected) {
//            doOnPushChannelConnected();
//        } else {
        IAdhocLoginStatusNotifier api = (IAdhocLoginStatusNotifier) AdhocFrameFactory.getInstance().getAdhocRouter()
                .build(AdhocRouteConstant.PATH_LOGIN_STATUS_NOTIFIER).navigation();
        if (api == null) {
            return;
        }

        api.onLogout();
//        }
        //这里为什么要startToBindDevice呢？？？
//        startToBindDevice().subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<Boolean>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onNext(Boolean pBoolean) {
//
//                    }
//                });

    }

    private IThirdPartyLogin getThirdPartyLogin() {
        String orgName = MdmEvnFactory.getInstance().getCurEnvironment().getOrg();
        return new UcLogin(orgName);
    }

    private AssistantSpConfig getConfig() {
        return AssistantBasicServiceFactory.getInstance().getSpConfig();
    }

    private IHttpService getHttpService() {
        return BasicServiceFactory.getInstance().getHttpService();
    }

}
