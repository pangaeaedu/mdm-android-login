package com.nd.android.adhoc.login.processOptimization;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceHelper;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceIDSPUtils;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceInfoManager;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;
import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.ui.activity.ActivityStackManager;
import com.nd.android.adhoc.basic.util.system.AdhocDeviceUtil;
import com.nd.android.adhoc.communicate.impl.MdmTransferFactory;
import com.nd.android.adhoc.communicate.push.listener.IAdhocPushConnectListener;
import com.nd.android.adhoc.communicate.push.listener.IPushConnectListener;
import com.nd.android.adhoc.control.define.IControl_IMEI;
import com.nd.android.adhoc.login.basicService.data.http.ConfirmDeviceIDResponse;
import com.nd.android.adhoc.login.basicService.data.http.QueryDeviceStatusResponse;
import com.nd.android.adhoc.login.enumConst.ActivateUserType;
import com.nd.android.adhoc.login.processOptimization.utils.LoginArgumentUtils;
import com.nd.android.adhoc.loginapi.exception.ConfirmIDServerException;
import com.nd.android.adhoc.loginapi.exception.DeviceTokenNotFoundException;
import com.nd.android.adhoc.loginapi.exception.RetrieveMacException;
import com.nd.android.mdm.basic.ControlFactory;

import java.util.Map;
import java.util.concurrent.TimeoutException;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

public class DeviceInitiator extends BaseAuthenticator implements IDeviceInitiator {

    private static final String TAG = "DeviceInitiator";


    private BehaviorSubject<DeviceStatus> mInitSubject = null;
    private Subscription mSubBindPushID = null;

    public DeviceInitiator(IDeviceStatusListener pProcessor) {
        super(pProcessor);

        MdmTransferFactory.getPushModel().addConnectListener(mPushConnectListener);

        // 补丁：这里是为了解决激活时 由于 push 连接非常快，并且已经回调过 onPushDeviceToken 了 这里才去塞监听，导致监听无法回调的问题  -- by hyk 2021-01-07
        if (MdmTransferFactory.getPushModel().isConnected()) {
            mPushConnectListener.onConnected();
        }
    }


    private IPushConnectListener mPushConnectListener = new IAdhocPushConnectListener() {
        @Override
        public void onPushDeviceToken(String deviceToken) {
            Logger.i("yhq", "onPushDeviceToken:");
            Logger.d("yhq", "onPushDeviceToken:" + deviceToken);
            onConnected();
        }

        @Override
        public void onConnected() {
            Logger.i("yhq", "push sdk onConnected");
            if (mSubBindPushID != null) {
                return;
            }

            Logger.i("yhq", "before call subject");
            mSubBindPushID = DeviceInfoManager.getInstance().getConfirmDeviceIDSubject().take(1)
                    .flatMap(new Func1<String, Observable<Boolean>>() {
                        @Override
                        public Observable<Boolean> call(String pDeviceID) {
                            return Observable.create(new Observable.OnSubscribe<Boolean>() {
                                @Override
                                public void call(Subscriber<? super Boolean> pSubscriber) {
                                    try {
                                        Logger.i("yhq", "before call bindPushIDToDeviceID");
                                        bindPushIDToDeviceID();
                                        HardwareInfoCompleteManager.getInstance()
                                                .reportHardwareInfoIfNecessary();

                                        pSubscriber.onNext(true);
                                        pSubscriber.onCompleted();
                                    } catch (Exception e) {
                                        pSubscriber.onError(e);
                                    }
                                }
                            });

                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<Boolean>() {
                        @Override
                        public void onCompleted() {
                            Logger.i("yhq", "bind push id complete");
                            mSubBindPushID = null;
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            Logger.e("yhq", "bind push id error:" + e.getMessage());
                            mSubBindPushID = null;

                            if (e instanceof DeviceTokenNotFoundException) {
                                clearSpDeviceIDThenQuit();
                            }
                        }

                        @Override
                        public void onNext(Boolean pO) {
                            Logger.i("yhq", "bind push id onNext");
                            mSubBindPushID = null;
                        }
                    });
        }

        @Override
        public void onDisconnected() {
            Logger.i("yhq", "push sdk onDisconnected");
        }
    };


    private void clearSpDeviceIDThenQuit() {
        Logger.i("yhq", "clearSpDeviceIDThenQuit");

        DeviceIDSPUtils.saveDeviceIDToSp("");

        ActivityStackManager.INSTANCE.closeAllActivitys();
        System.exit(0);
    }

    public Observable<DeviceStatus> actualQueryDeviceStatus(final String pDeviceID) {
        Logger.i("yhq", "actualQueryDeviceStatus");
        Logger.d("yhq", "actualQueryDeviceStatus:" + pDeviceID);
        return queryDeviceStatusFromServer(pDeviceID)
                .flatMap(new Func1<QueryDeviceStatusResponse, Observable<DeviceStatus>>() {
                    @Override
                    public Observable<DeviceStatus> call(QueryDeviceStatusResponse pResponse) {
                        if (pResponse.isAutoLogin() && pResponse.getStatus() == DeviceStatus.Enrolled) {
                            return activeUser(ActivateUserType.AutoLogin,
                                    pResponse.getSelSchoolGroupCode(),
                                    pResponse.getRootCode(),
                                    "");
                        }

                        return Observable.just(pResponse.getStatus());
                    }
                });
    }

    public Observable<DeviceStatus> queryDeviceStatus() {
        Logger.i("yhq", "queryDeviceStatus");
        final String deviceID = DeviceInfoManager.getInstance().getDeviceID();
        if (TextUtils.isEmpty(deviceID)) {
            return confirmDeviceID()
                    .flatMap(new Func1<String, Observable<DeviceStatus>>() {
                        @Override
                        public Observable<DeviceStatus> call(String pConfirmedDeviceID) {
                            return actualQueryDeviceStatus(pConfirmedDeviceID);
                        }
                    });
        }

        return actualQueryDeviceStatus(deviceID);
    }

    private Observable<String> confirmDeviceID() {
        Logger.i("yhq", "confirmDeviceID");
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> pSubscriber) {
                try {
                    String memDeviceID = DeviceInfoManager.getInstance().getDeviceID();
                    if (!TextUtils.isEmpty(memDeviceID)) {
                        pSubscriber.onNext(memDeviceID);
                        pSubscriber.onCompleted();
                        return;
                    }

                    String deviceID = DeviceIDSPUtils.loadThirdVersionDeviceIDFromSp();
                    Context context = AdhocBasicConfig.getInstance().getAppContext();

                    Logger.d("yhq", "third sp device id:" + deviceID);
                    if (!TextUtils.isEmpty(deviceID)) {
                        DeviceInfoManager.getInstance().setDeviceID(deviceID);

                        DeviceIDSPUtils.startNewThreadToCheckDeviceIDIntegrity(context, deviceID);
                    } else {
                        deviceID = loadDeviceIDFromPrevSpOrSDCard();


                        ConfirmDeviceIDResponse result = confirmDeviceIDFromServer(deviceID);

                        if (!result.isSuccess()) {
                            pSubscriber.onError(new ConfirmIDServerException("result not success"));
                            return;
                        }

                        deviceID = result.getDeviceID();
                        getConfig().clearData();
                        DeviceInfoManager.getInstance().setDeviceID(deviceID);
                        DeviceIDSPUtils.saveDeviceIDToSp(deviceID);
                        DeviceIDSPUtils.startNewThreadToCheckDeviceIDIntegrity(context, deviceID);
                    }

                    pSubscriber.onNext(deviceID);
                    pSubscriber.onCompleted();
                } catch (Exception e) {
                    Logger.e("yhq", e.getMessage());
                    pSubscriber.onError(e);
                }
            }
        });
    }

    public Observable<DeviceStatus> init() {
        Observable<DeviceStatus> temp;
        synchronized (DeviceInitiator.this) {
            if (mInitSubject == null) {
                mInitSubject = BehaviorSubject.create();
                confirmDeviceID()
                        .flatMap(new Func1<String, Observable<DeviceStatus>>() {
                            @Override
                            public Observable<DeviceStatus> call(String pDeviceID) {
                                return queryDeviceStatus();
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Subscriber<DeviceStatus>() {
                            @Override
                            public void onCompleted() {
                                synchronized (DeviceInitiator.this) {
                                    Logger.i("yhq", "init complete");
                                    mInitSubject.onCompleted();
                                    mInitSubject = null;
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Logger.e("yhq", "init error:" + e.getMessage());
                                e.printStackTrace();

                                synchronized (DeviceInitiator.this) {
                                    mInitSubject.onError(e);
                                    mInitSubject = null;
                                }
                            }

                            @Override
                            public void onNext(DeviceStatus pStatus) {
                                mInitSubject.onNext(pStatus);
                            }
                        });
            }
            temp = mInitSubject;
        }
        return temp.asObservable();

    }

    @Override
    public void uninit() {

    }

    private String loadDeviceIDFromPrevSpOrSDCard() throws Exception {
        String secondVersionID = DeviceIDSPUtils.loadSecondVersionDeviceIDFromSp();
        String firstVersionID = DeviceIDSPUtils.loadFirstVersionDeviceIDFromSp();

        // 之前版本不存在。
        if (TextUtils.isEmpty(secondVersionID) && TextUtils.isEmpty(firstVersionID)) {
            String sd = loadDeviceIDFromSDCard();

            return sd;
        }

        if (!TextUtils.isEmpty(secondVersionID)) {
            Logger.d("yhq", "use second version:" + secondVersionID);
            return secondVersionID;
        }

        Logger.d("yhq", "use first version:" + firstVersionID);
        return firstVersionID;
    }

    private String loadDeviceIDFromSDCard() {
        Context context = AdhocBasicConfig.getInstance().getAppContext();
        String sdCardDeviceID = DeviceIDSPUtils.loadDeviceIDFromSdCard(context);
        String deviceID = "";
        if (!TextUtils.isEmpty(sdCardDeviceID)) {
            Logger.d("yhq", "sd card device id:" + sdCardDeviceID);
            deviceID = sdCardDeviceID;
        } else {
            deviceID = DeviceIDSPUtils.generateDeviceID();
            Logger.d("yhq", "generate device id:" + deviceID);
        }

        return deviceID;
    }

    private ConfirmDeviceIDResponse confirmDeviceIDFromServer(String pLocalDeviceID) throws Exception {
        Context context = AdhocBasicConfig.getInstance().getAppContext();
        String buildSn = AdhocDeviceUtil.getBuildSN(context);
        String cpuSn = AdhocDeviceUtil.getCpuSN();
        String wifiMac = AdhocDeviceUtil.getWifiMac(context);

        String lanMac = AdhocDeviceUtil.getEthernetMac();

        String imei = AdhocDeviceUtil.getIMEI(context);
        String imei2 = null;

        IControl_IMEI control_imei = ControlFactory.getInstance().getControl(IControl_IMEI.class);
        if(control_imei != null){
            imei = control_imei.getIMEI(0);
            imei2 = control_imei.getIMEI(1);
        }

        if (TextUtils.isEmpty(wifiMac) && TextUtils.isEmpty(lanMac)) {
            throw new RetrieveMacException();
        }

        String blueToothMac = AdhocDeviceUtil.getBloothMac();
        String serialNo = DeviceHelper.getSerialNumberThroughControl();
        String androidID = AdhocDeviceUtil.getAndroidId(context);
        boolean isAutoLogin = isAutoLogin();

        ConfirmDeviceIDResponse response = null;
        for (int i = 0; i <= 2; i++) {
            try {
                Logger.i("yhq", "confirm device id round:" + i);

                if (TextUtils.isEmpty(wifiMac)) {
                    wifiMac = AdhocDeviceUtil.getWifiMac(context);
                }

                Logger.d("yhq", "input buildSn:" + buildSn + " cpuSn:" + cpuSn + " imei:" + imei+ " imei2:" + imei2
                        + " wifiMac:" + wifiMac + " lanMac:" + lanMac + " blueToothMac:" + blueToothMac + " serialNo:" + serialNo
                        + " androidID:" + androidID + " localDeviceID:" + pLocalDeviceID);
                Map<String, Object> hardwareMaps = LoginArgumentUtils.genHardwareMap(buildSn,
                        cpuSn, imei, wifiMac, lanMac, blueToothMac, serialNo, androidID, imei2);
                response = getHttpService().confirmDeviceID(hardwareMaps, pLocalDeviceID);
                if (response != null) {
                    Logger.d("yhq", "deviceID response:" + response.getDeviceID());
                    return response;
                }
            } catch (Exception pE) {
                pE.printStackTrace();
                if (!isAutoLogin && !(pE instanceof ConfirmIDServerException)) {
                    throw pE;
                }
            }
        }

        //查询设备状态时发现异常，如果是自动登录，并且是未激活的设备，退出
        if (isAutoLogin) {
            Logger.i("yhq", "confirm device id error, quit");
            sendFailedAndQuitApp(120);
        }

        throw new TimeoutException("after retry 3 time, confirm deivce id still timeout");
    }
}
