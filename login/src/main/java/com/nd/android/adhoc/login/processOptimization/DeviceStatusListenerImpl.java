package com.nd.android.adhoc.login.processOptimization;

import android.util.Log;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceInfoManager;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;
import com.nd.android.adhoc.basic.frame.factory.AdhocFrameFactory;
import com.nd.android.adhoc.loginapi.ILoginInfoProvider;

import org.json.JSONObject;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class DeviceStatusListenerImpl extends BaseServiceProvider implements IDeviceStatusListener {
    private static final String TAG = "DeviceStatusListener";

    private Subscription mSubscription = null;

    private void onDeviceActivated(){
        if(mSubscription == null){
            return;
        }

        mSubscription =  DeviceInfoManager.getInstance().getPushIDSubject()
                .asObservable()
                .flatMap(new Func1<String, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(String pS) {
                        try {
                            requestPolicySet();
                            return Observable.just(null);
                        } catch (Exception e) {
                            return Observable.error(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {
                        mSubscription = null;
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mSubscription = null;
                    }

                    @Override
                    public void onNext(Void pVoid) {

                    }
                });
    }

    @Override
    public void onDeviceStatusChanged(DeviceStatus pStatus) {
        Log.w(TAG, "onDeviceStatusChanged:" + pStatus);
        DeviceInfoManager.getInstance().setCurrentStatus(pStatus);
        if (pStatus == DeviceStatus.Activated) {
            onDeviceActivated();
            return;
        }
    }

    private void requestPolicySet() {
        try {
            String deviceID =  DeviceInfoManager.getInstance().getDeviceID();
            long pTime = getConfig().getPolicySetTime();
            ILoginInfoProvider provider = (ILoginInfoProvider) AdhocFrameFactory.getInstance().getAdhocRouter()
                    .build(ILoginInfoProvider.PATH).navigation();
            if (provider == null) {
                throw new Exception("login info provider not exist");
            }

            JSONObject object = provider.getDeviceInfo();
            getHttpService().requestPolicy(deviceID, pTime, object);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
