package com.nd.android.adhoc.login.processOptimization;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.nd.adhoc.assistant.sdk.AssistantBasicServiceFactory;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceHelper;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceIDSPUtils;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceInfoManager;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;
import com.nd.adhoc.assistant.sdk.deviceInfo.UserLoginConfig;
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
    }

    public void checkLocalStatusAndServer(){
        reallyQueryDeviceStatusFromServer(DeviceInfoManager.getInstance().getDeviceID())
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<DeviceStatus>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(DeviceStatus statusOfServer) {
                        if(null != statusOfServer){
                            DeviceStatus statusOfLocal = DeviceInfoManager.getInstance().getCurrentStatus();
                            if(null == statusOfLocal){
                                return;
                            }

                            mDeviceStatusListener.onDeviceStatusChanged(statusOfServer);
                            if(DeviceStatus.isStatusUnLogin(statusOfServer) && !DeviceStatus.isStatusUnLogin(statusOfLocal)){
                                DeviceIDSPUtils.saveDeviceIDToSp("");
                                Logger.e(TAG, "differnet status, exit");
                                System.exit(0);
                            }
                        }
                    }
                });
    }

    private IPushConnectListener mPushConnectListener = new IAdhocPushConnectListener() {
        @Override
        public void onPushDeviceToken(String deviceToken) {
            Log.e("yhq", "onPushDeviceToken:" + deviceToken);
            onConnected();
        }

        @Override
        public void onConnected() {
            Log.e("yhq", "push sdk onConnected");
            if (mSubBindPushID != null) {
                return;
            }

            if(1 == DeviceInfoManager.getInstance().getNeedQueryStatusFromServer()){
                Log.e("yhq", "check status when connected");
                checkLocalStatusAndServer();
            }
            Log.e("yhq", "before call subject");
            mSubBindPushID = DeviceInfoManager.getInstance().getConfirmDeviceIDSubject().take(1)
                    .flatMap(new Func1<String, Observable<Boolean>>() {
                        @Override
                        public Observable<Boolean> call(String pDeviceID) {
                            return Observable.create(new Observable.OnSubscribe<Boolean>() {
                                @Override
                                public void call(Subscriber<? super Boolean> pSubscriber) {
                                    try {
                                        Log.e("yhq", "before call bindPushIDToDeviceID");
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
                            Log.e("yhq", "bind push id complete");
                            mSubBindPushID = null;
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            Log.e("yhq", "bind push id error:" + e.getMessage());
                            mSubBindPushID = null;

                            if (e instanceof DeviceTokenNotFoundException) {
                                clearSpDeviceIDThenQuit();
                            }
                        }

                        @Override
                        public void onNext(Boolean pO) {
                            Log.e("yhq", "bind push id onNext");
                            mSubBindPushID = null;
                        }
                    });
        }

        @Override
        public void onDisconnected() {
            Log.e("yhq", "push sdk onDisconnected");
        }
    };


    private void clearSpDeviceIDThenQuit() {
        Log.e("yhq", "clearSpDeviceIDThenQuit");

        DeviceIDSPUtils.saveDeviceIDToSp("");

        ActivityStackManager.INSTANCE.closeAllActivitys();
        System.exit(0);

    }

    public Observable<DeviceStatus> actualQueryDeviceStatus(final String pDeviceID) {
        Log.e("yhq", "actualQueryDeviceStatus:" + pDeviceID);
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
        Log.e("yhq", "queryDeviceStatus");
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
        Log.e("yhq", "confirmDeviceID");
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

                    Log.e("yhq", "third sp device id:" + deviceID);
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
                    Log.e("yhq", e.getMessage());
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
                        // 这里要去获取当前登录后的归属节点信息
                        .map(new Func1<DeviceStatus, DeviceStatus>() {
                            @Override
                            public DeviceStatus call(DeviceStatus deviceStatus) {
                                if (DeviceStatus.Activated == deviceStatus) {
                                    try {
                                        String deviceId = AssistantBasicServiceFactory.getInstance().getSpConfig().getDeviceID();
                                        String serialNum = DeviceHelper.getSerialNumberThroughControl();
                                        UserLoginConfig loginConfig = DeviceInfoManager.getInstance().getUserLoginConfig();
                                        QueryDeviceStatusResponse result = getHttpService().getDeviceStatus(deviceId, serialNum,
                                                loginConfig.getAutoLogin(), loginConfig.getNeedGroup());
                                        if (DeviceStatus.Activated == result.getStatus()) {
                                            // 记录当前的节点 code 和 名称
                                            getConfig().saveNodeCode(result.getNodecode());
                                            getConfig().saveNodeName(result.getNodename());
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                return deviceStatus;
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Subscriber<DeviceStatus>() {
                            @Override
                            public void onCompleted() {
                                synchronized (DeviceInitiator.this) {
                                    Log.e("yhq", "init complete");
                                    mInitSubject.onCompleted();
                                    mInitSubject = null;
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("yhq", "init error:" + e.getMessage());
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
            Log.e("yhq", "use second version:" + secondVersionID);
            return secondVersionID;
        }

        Log.e("yhq", "use first version:" + firstVersionID);
        return firstVersionID;
    }

    private String loadDeviceIDFromSDCard() {
        Context context = AdhocBasicConfig.getInstance().getAppContext();
        String sdCardDeviceID = DeviceIDSPUtils.loadDeviceIDFromSdCard(context);
        String deviceID = "";
        if (!TextUtils.isEmpty(sdCardDeviceID)) {
            Log.e("yhq", "sd card device id:" + sdCardDeviceID);
            deviceID = sdCardDeviceID;
        } else {
            deviceID = DeviceIDSPUtils.generateDeviceID();
            Log.e("yhq", "generate device id:" + deviceID);
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
                Log.e("yhq", "confirm device id round:" + i);

                if (TextUtils.isEmpty(wifiMac)) {
                    wifiMac = AdhocDeviceUtil.getWifiMac(context);
                }

                Log.e("yhq", "input buildSn:" + buildSn + " cpuSn:" + cpuSn + " imei:" + imei+ " imei2:" + imei2
                        + " wifiMac:" + wifiMac + " lanMac:" + lanMac + " blueToothMac:" + blueToothMac + " serialNo:" + serialNo
                        + " androidID:" + androidID + " localDeviceID:" + pLocalDeviceID);
                Map<String, Object> hardwareMaps = LoginArgumentUtils.genHardwareMap(buildSn,
                        cpuSn, imei, wifiMac, lanMac, blueToothMac, serialNo, androidID, imei2);
                response = getHttpService().confirmDeviceID(hardwareMaps, pLocalDeviceID);
                if (response != null) {
                    Log.e("yhq", "deviceID response:" + response.getDeviceID());
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
            Log.e("yhq", "confirm device id error, quit");
            sendFailedAndQuitApp(120);
        }

        throw new TimeoutException("after retry 3 time, confirm deivce id still timeout");
    }
}
