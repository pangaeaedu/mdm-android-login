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
import com.nd.android.adhoc.loginapi.exception.DeviceIDNotSetException;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

public class DeviceInitiator extends BaseAuthenticator implements IDeviceInitiator {

    private static final String TAG = "DeviceInitiator";

    private BehaviorSubject<String> mConfirmDeviceIDSubject = BehaviorSubject.create();

    private BehaviorSubject<DeviceStatus> mInitSubject = null;
    private Subscription mSubBindPushID = null;

    public DeviceInitiator(IDeviceStatusListener pProcessor) {
        super(pProcessor);

        MdmTransferFactory.getPushModel().addConnectListener(mPushConnectListener);
    }


    private IPushConnectListener mPushConnectListener = new IPushConnectListener() {
        @Override
        public void onConnected() {
            if (mSubBindPushID != null) {
                return;
            }

            mSubBindPushID = mConfirmDeviceIDSubject
                    .flatMap(new Func1<String, Observable<Boolean>>() {
                        @Override
                        public Observable<Boolean> call(String pDeviceID) {
                            try {
                                bindPushIDToDeviceID();
                                return Observable.just(true);
                            } catch (Exception e) {
                                return Observable.error(e);
                            }
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<Boolean>() {
                        @Override
                        public void onCompleted() {
                            mSubBindPushID = null;
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            mSubBindPushID = null;
                        }

                        @Override
                        public void onNext(Boolean pO) {

                        }
                    });
        }

        @Override
        public void onDisconnected() {
            Log.e(TAG, "push sdk disconnected");
        }
    };



    public Observable<DeviceStatus> queryDeviceStatus() {
        return Observable
                .create(new Observable.OnSubscribe<QueryDeviceStatusResponse>() {
                    @Override
                    public void call(Subscriber<? super QueryDeviceStatusResponse> pSubscriber) {
                        try {
                            String deviceID = DeviceInfoManager.getInstance().getDeviceID();
                            String serialNum = DeviceHelper.getSerialNumberThroughControl();

                            if (TextUtils.isEmpty(deviceID)) {
                                pSubscriber.onError(new DeviceIDNotSetException());
                                return;
                            }

                            QueryDeviceStatusResponse result = getHttpService().getDeviceStatus(deviceID, serialNum);
                            Log.e(TAG, "QueryDeviceStatusResponse:"+result.toString());
                            saveLoginInfo(result.getUsername(), result.getNickname());

                            DeviceStatus curStatus = result.getStatus();
                            if(curStatus == DeviceStatus.Unknown || curStatus == DeviceStatus.Enrolled){
                                getConfig().clearData();
                            }

                            if (curStatus == DeviceStatus.Activated) {
                                notifyLogin(getConfig().getAccountNum(), getConfig().getNickname());
                            }

                            mDeviceStatusListener.onDeviceStatusChanged(curStatus);
                            pSubscriber.onNext(result);
                            pSubscriber.onCompleted();
                        } catch (Exception e) {
                            pSubscriber.onError(e);
                        }
                    }
                })
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

    private Observable<String> confirmDeviceID() {
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

                    String deviceID = DeviceIDSPUtils.loadDeviceIDFromSp();
                    Context context = AdhocBasicConfig.getInstance().getAppContext();

                    if (!TextUtils.isEmpty(deviceID)) {
                        DeviceInfoManager.getInstance().setDeviceID(deviceID);
                        mConfirmDeviceIDSubject.onNext(deviceID);
                        DeviceIDSPUtils.startNewThreadToCheckDeviceIDIntegrity(context, deviceID);
                    } else {
                        deviceID = loadSdDeviceIDAndConfirmFromServer();
                        DeviceInfoManager.getInstance().setDeviceID(deviceID);
                        mConfirmDeviceIDSubject.onNext(deviceID);
                        DeviceIDSPUtils.saveDeviceIDToSp(deviceID);
                        DeviceIDSPUtils.startNewThreadToCheckDeviceIDIntegrity(context, deviceID);
                    }

                    pSubscriber.onNext(deviceID);
                    pSubscriber.onCompleted();
                } catch (Exception e) {
                    pSubscriber.onError(e);
                }
            }
        });
    }

    public Observable<DeviceStatus> init() {
        Log.e(TAG, "calling init");
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
                                    mInitSubject.onCompleted();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
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

    private String loadSdDeviceIDAndConfirmFromServer() throws Exception {
        String deviceID = loadDeviceIDFromSDCard();
        ConfirmDeviceIDResponse result = confirmDeviceIDFromServer(deviceID);
        // 如果回来跟本地的不一样，报Bugly
        return result.getDeviceID();
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

        return getHttpService().confirmDeviceID(buildSn, cpuSn, imei, wifiMac,
                blueToothMac, serialNo, androidID,pLocalDeviceID);
    }
}
