package com.nd.android.adhoc.login.processOptimization;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceHelper;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceIDSPUtils;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceInfoManager;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;
import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.util.system.AdhocDeviceUtil;
import com.nd.android.adhoc.communicate.impl.MdmTransferFactory;
import com.nd.android.adhoc.communicate.push.listener.IPushConnectListener;
import com.nd.android.adhoc.login.basicService.data.http.ConfirmDeviceIDResponse;
import com.nd.android.adhoc.login.basicService.data.http.QueryDeviceStatusResponse;
import com.nd.android.adhoc.login.enumConst.ActivateUserType;
import com.nd.android.adhoc.loginapi.exception.ConfirmIDServerException;

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


    private IPushConnectListener mPushConnectListener = new IPushConnectListener() {
        @Override
        public void onConnected() {
            Log.e("yhq", "push sdk onConnected");
            if (mSubBindPushID != null) {
                return;
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
            Log.e(TAG, "push sdk onDisconnected");
        }
    };

    public Observable<DeviceStatus> actualQueryDeviceStatus(final String pDeviceID) {
        Log.e("yhq", "actualQueryDeviceStatus");
        return queryDeviceStatusFromServer(pDeviceID)
                .flatMap(new Func1<QueryDeviceStatusResponse, Observable<DeviceStatus>>() {
                    @Override
                    public Observable<DeviceStatus> call(QueryDeviceStatusResponse pResponse) {
                        if (pResponse.isAutoLogin() && pResponse.getStatus() == DeviceStatus.Enrolled) {
                            return activeUser(ActivateUserType.AutoLogin, "");
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

                    if (!TextUtils.isEmpty(deviceID)) {
                        DeviceInfoManager.getInstance().setDeviceID(deviceID);

                        DeviceIDSPUtils.startNewThreadToCheckDeviceIDIntegrity(context, deviceID);
                    } else {
                        deviceID = loadDeviceIDFromPrevSpOrSDCard();
                        ConfirmDeviceIDResponse result = confirmDeviceIDFromServer(deviceID);

                        if(!result.isSuccess()){
                            pSubscriber.onError(new ConfirmIDServerException("result not success"));
                            return;
                        }

                        getConfig().clearData();
                        DeviceInfoManager.getInstance().setDeviceID(result.getDeviceID());

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
                                Log.e("yhq", "init error:"+e.getMessage());
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
        if(TextUtils.isEmpty(secondVersionID) && TextUtils.isEmpty(firstVersionID)) {
            return loadDeviceIDFromSDCard();
        }

        if (!TextUtils.isEmpty(secondVersionID)) {
            return secondVersionID;
        }

        return firstVersionID;
    }

    private String loadDeviceIDFromSDCard(){
        Context context = AdhocBasicConfig.getInstance().getAppContext();
        String sdCardDeviceID = DeviceIDSPUtils.loadDeviceIDFromSdCard(context);
        String deviceID = "";
        if (!TextUtils.isEmpty(sdCardDeviceID)) {
            deviceID = sdCardDeviceID;
        } else {
            deviceID = DeviceIDSPUtils.generateDeviceID();
        }

        return deviceID;
    }

    private ConfirmDeviceIDResponse confirmDeviceIDFromServer(String pLocalDeviceID) throws Exception {
        Context context = AdhocBasicConfig.getInstance().getAppContext();
        String buildSn = AdhocDeviceUtil.getBuildSN(context);
        String cpuSn = AdhocDeviceUtil.getCpuSN();
        String imei = AdhocDeviceUtil.getIMEI(context);
        String wifiMac = AdhocDeviceUtil.getWifiMac(context);
        String blueToothMac = AdhocDeviceUtil.getBloothMac();
        String serialNo = DeviceHelper.getSerialNumberThroughControl();
        String androidID = AdhocDeviceUtil.getAndroidId(context);

//        Log.e("yhq", "input buildSn:"+buildSn+" cpuSn:"+cpuSn+" imei:"+imei
//        +" wifiMac:"+wifiMac+" blueToothMac:"+blueToothMac+" serialNo:"+serialNo
//        +" androidID:"+androidID+" localDeviceID:"+pLocalDeviceID);
        ConfirmDeviceIDResponse response =  getHttpService().confirmDeviceID(buildSn, cpuSn, imei,
                wifiMac, blueToothMac, serialNo, androidID,pLocalDeviceID);
//        Log.e("yhq", "deviceID response:"+response.getDeviceID());
        return response;
    }
}
