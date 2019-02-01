package com.nd.android.adhoc.login.processOptimization;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceIDSPUtils;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceInfoManager;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;
import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.util.system.AdhocDeviceUtil;
import com.nd.android.adhoc.communicate.impl.MdmTransferFactory;
import com.nd.android.adhoc.communicate.push.IPushModule;
import com.nd.android.adhoc.communicate.push.listener.IPushConnectListener;
import com.nd.android.adhoc.login.basicService.data.http.GetDeviceStatusResult;
import com.nd.android.adhoc.login.basicService.data.http.GetTokenResult;
import com.nd.android.adhoc.loginapi.exception.ConfirmIDServerException;
import com.nd.android.adhoc.loginapi.exception.QueryDeviceStatusServerException;

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

    private void bindPushIDToDeviceID() throws Exception {
        IPushModule module = MdmTransferFactory.getPushModel();
        String pushID = module.getDeviceId();
        String existPushID = getConfig().getPushID();

        if (TextUtils.isEmpty(pushID)) {
            throw new Exception("get push id from push module return empty");
        }

        if(pushID.equalsIgnoreCase(existPushID)){
            DeviceInfoManager.getInstance().notifyPushID(pushID);
            return;
        }

        //Push ID 不一样以后，要先清理掉本地的PushID
        getConfig().clearPushID();
        String serialNum = AdhocDeviceUtil.getSerialNumber();
        String deviceID =  DeviceInfoManager.getInstance().getDeviceID();
        int channelType = module.getChannelType();

        getHttpService().bindDeviceWithChannelType(deviceID, pushID, serialNum, channelType);
        getConfig().savePushID(pushID);
        DeviceInfoManager.getInstance().notifyPushID(pushID);
    }

    public Observable<DeviceStatus> queryDeviceStatus() {
        return Observable
                .create(new Observable.OnSubscribe<DeviceStatus>() {
                    @Override
                    public void call(Subscriber<? super DeviceStatus> pSubscriber) {
                        try {
                            String deviceID =  DeviceInfoManager.getInstance().getDeviceID();
                            String serialNum = AdhocDeviceUtil.getSerialNumber();

                            if(TextUtils.isEmpty(deviceID)){
                                pSubscriber.onError(new ConfirmIDServerException());
                                return;
                            }

                            GetDeviceStatusResult result = getHttpService().getDeviceStatus(deviceID, serialNum);
                            if(!result.isSuccess()){
                                pSubscriber.onError(new QueryDeviceStatusServerException());
                                return;
                            }

                            DeviceStatus curStatus = result.getStatus();
                            if (curStatus == DeviceStatus.Activated) {
                                notifyLogin(getConfig().getAccountNum(), getConfig().getNickname());
                            }

                            mDeviceStatusListener.onDeviceStatusChanged(curStatus);
                            pSubscriber.onNext(curStatus);
                            pSubscriber.onCompleted();
                        } catch (Exception e) {
                            pSubscriber.onError(e);
                        }
                    }
                });
    }

    private Observable<String> confirmDeviceID(){
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> pSubscriber) {
                try {
                    String deviceID = DeviceIDSPUtils.loadDeviceIDFromSp();
                    Context context = AdhocBasicConfig.getInstance().getAppContext();

                    if (!TextUtils.isEmpty(deviceID)) {
                        DeviceInfoManager.getInstance().setDeviceID(deviceID);
                        mConfirmDeviceIDSubject.onNext(deviceID);
                        DeviceIDSPUtils.startNewThreadToCheckDeviceIDIntegrity(context, deviceID);
                    } else {
                        deviceID = confirmDeviceIDFromServerWhileIDNotFoundInSp();
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
        if (mInitSubject == null) {
            mInitSubject = BehaviorSubject.create();
            confirmDeviceID()
                    .flatMap(new Func1<String, Observable<DeviceStatus>>() {
                        @Override
                        public Observable<DeviceStatus> call(String pDeviceID) {
                            return queryDeviceStatus();
                        }
                    })
                    .subscribe(new Subscriber<DeviceStatus>() {
                        @Override
                        public void onCompleted() {
                            mInitSubject.onCompleted();
                            mInitSubject = null;
                        }

                        @Override
                        public void onError(Throwable e) {
                            mInitSubject.onError(e);
                            mInitSubject = null;
                        }

                        @Override
                        public void onNext(DeviceStatus pStatus) {
                            mInitSubject.onNext(pStatus);
                        }
                    });
        }

        return mInitSubject.asObservable();
    }

    @Override
    public void uninit() {

    }

    private String confirmDeviceIDFromServerWhileIDNotFoundInSp() throws Exception{
        Context context = AdhocBasicConfig.getInstance().getAppContext();
        String sdCardDeviceID = DeviceIDSPUtils.loadDeviceIDFromSdCard(context);
        String deviceID = "";
        if(TextUtils.isEmpty(sdCardDeviceID)){
            deviceID = sdCardDeviceID;
        } else {
            deviceID = DeviceIDSPUtils.generateDeviceID();
        }

        GetTokenResult result = confirmDeviceIDFromServer(deviceID);
        if(!result.isSuccess()){
            throw new ConfirmIDServerException();
        }

        return result.getDeviceID();
    }

    private GetTokenResult confirmDeviceIDFromServer(String pLocalDeviceID) throws Exception{

        String buildSn = AdhocDeviceUtil.getBuildSN(AdhocBasicConfig.getInstance().getAppContext());
        String cpuSn = AdhocDeviceUtil.getCpuSN();
        String imei = AdhocDeviceUtil.getIMEI(AdhocBasicConfig.getInstance().getAppContext());
        String wifiMac = AdhocDeviceUtil.getWifiMac(AdhocBasicConfig.getInstance().getAppContext());
        String blueToothMac = AdhocDeviceUtil.getBloothMac();
        String serialNo = AdhocDeviceUtil.getSerialNumber();
        String androidID = "";

        return getHttpService().confirmDeviceID(buildSn, cpuSn, imei, wifiMac,
                blueToothMac, serialNo, androidID,pLocalDeviceID);
    }
}
