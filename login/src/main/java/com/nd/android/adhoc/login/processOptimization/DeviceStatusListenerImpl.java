package com.nd.android.adhoc.login.processOptimization;

import android.text.TextUtils;
import android.util.Log;

import com.nd.adhoc.assistant.sdk.AssistantBasicServiceFactory;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceInfoManager;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;
import com.nd.android.adhoc.basic.frame.factory.AdhocFrameFactory;
import com.nd.android.adhoc.communicate.impl.MdmTransferFactory;
import com.nd.android.adhoc.communicate.push.IPushModule;
import com.nd.android.adhoc.policy.api.provider.IAdhocPolicyLifeCycleProvider;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class DeviceStatusListenerImpl extends BaseAbilityProvider implements IDeviceStatusListener {
    private static final String TAG = "DeviceStatusListener";

    private Subscription mSubscription = null;

    // 设备状态是激活的情况下，每次都要去请求PolicySet.
    // 前提是PushID取到了。所以如果PushID没取到，还得等着
    private void onDeviceActivated(){
        if(mSubscription != null){
            return;
        }

        IPushModule module = MdmTransferFactory.getPushModel();
        String pushID = module.getDeviceId();

        String spPushId = AssistantBasicServiceFactory.getInstance().getSpConfig().getPushID();
        Log.e("yhq", "onDeviceActivated");

        if (TextUtils.isEmpty(spPushId) || !spPushId.equals(pushID)) {
            Log.e("yhq", "subject pushId observable");

            mSubscription = DeviceInfoManager.getInstance()
                    .getPushIDSubject().asObservable().take(1)   //取第一个，将长监听转成单次监听，
                    .flatMap(new Func1<String, Observable<Void>>() {// 这样取一次后，后续的调用流就能走到onComplete
                        @Override
                        public Observable<Void> call(String pS) {
                            try {
                                updatePolicy();
//                            requestPolicySet();
                                Log.e("yhq", "requestPolicySet finish");
                                return Observable.just(null);
                            } catch (Exception e) {
                                Log.e("yhq", "requestPolicySet error:" + e.getMessage());
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
            return;
        }

        Log.e("yhq", "subject pushId observable");
        mSubscription = Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    updatePolicy();
//                            requestPolicySet();
                    Log.e("yhq", "requestPolicySet finish");
                    subscriber.onCompleted();
                } catch (Exception e) {
                    Log.e("yhq", "requestPolicySet error:" + e.getMessage());
                    subscriber.onError(e);
                }
            }
        }).subscribe(new Subscriber<Void>() {
            @Override
            public void onCompleted() {
                mSubscription = null;
            }

            @Override
            public void onError(Throwable e) {
                mSubscription = null;
            }

            @Override
            public void onNext(Void aVoid) {

            }
        });

    }

    @Override
    public void onDeviceStatusChanged(DeviceStatus pStatus) {
        Log.e("yhq", "onDeviceStatusChanged:"+pStatus);
        DeviceInfoManager.getInstance().setCurrentStatus(pStatus);
        if (pStatus == DeviceStatus.Activated) {
            onDeviceActivated();
            return;
        }
    }

//    private void requestPolicySet() {
//        try {
//            String deviceID =  DeviceInfoManager.getInstance().getDeviceID();
//            long pTime = getConfig().getPolicySetTime();
//
//            ILoginInfoProvider provider = (ILoginInfoProvider) AdhocFrameFactory.getInstance()
//                    .getAdhocRouter().build(ILoginInfoProvider.PATH).navigation();
//            if (provider == null) {
//                throw new Exception("login info provider not exist");
//            }
//
//            JSONObject object = provider.getDeviceInfo();
//            getHttpService().requestPolicy(deviceID, pTime, object);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    private void updatePolicy(){
        IAdhocPolicyLifeCycleProvider policyLifeCycleProvider =
                (IAdhocPolicyLifeCycleProvider) AdhocFrameFactory.getInstance()
                        .getAdhocRouter().build(IAdhocPolicyLifeCycleProvider.ROUTE_PATH).navigation();
        if (policyLifeCycleProvider == null) {
            return;
        }
        policyLifeCycleProvider.updatePolicy();
    }
}
