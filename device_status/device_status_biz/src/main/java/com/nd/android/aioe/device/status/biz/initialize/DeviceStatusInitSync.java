package com.nd.android.aioe.device.status.biz.initialize;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.frame.api.initialization.AdhocAppInitPriority;
import com.nd.android.adhoc.basic.frame.api.initialization.AdhocAppInitSyncAbs;
import com.nd.android.adhoc.basic.frame.api.initialization.IAdhocInitCallback;
import com.nd.android.adhoc.basic.frame.factory.AdhocFrameFactory;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.aioe.device.status.biz.api.provider.IDeviceStatusProvider;
import com.nd.sdp.android.serviceloader.annotation.Service;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

@Service(AdhocAppInitSyncAbs.class)
public class DeviceStatusInitSync extends AdhocAppInitSyncAbs {

    private static final String TAG = "DeviceStatusInitSync";

    @Override
    public AdhocAppInitPriority getInitPriority() {
        return AdhocAppInitPriority.LOW;
    }

    @Override
    public void doInitSync(@NonNull final IAdhocInitCallback pCallback) {

        final IDeviceStatusProvider deviceStatusProvider = (IDeviceStatusProvider) AdhocFrameFactory.getInstance().getAdhocRouter()
                .build(IDeviceStatusProvider.ROUTE_PATH).navigation();

        if (deviceStatusProvider == null) {
            pCallback.onFailed(new AdhocException("IDeviceStatusProvider implement not found"));
            return;
        }

        Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    deviceStatusProvider.updateDeviceStatus();
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                } catch (AdhocException e) {
                    Logger.e(TAG, "updateDeviceStatus error: " + e);
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO：如果失败
//                        if(e instanceof RetrieveMacException){
//                            pCallback.onFailed(new AdhocException("retrieve wifi mac error"));
//                            return;
//                        }
//
//                        Logger.e("yhq", "init Device error:" + e.getMessage());
//                        if(!TextUtils.isEmpty(DeviceInfoManager.getInstance().getDeviceID())) {
//                            updatePolicy();
//                        }
//                        pCallback.onSuccess();
//
//                        DeviceInfoManager.getInstance().setNeedQueryStatusFromServer(1);
//                        if(1 == DeviceInfoManager.getInstance().getNeedQueryStatusFromServer()
//                                && PushSdkModule.getInstance().isConnected()){
//                            Logger.i("yhq", "use DeviceInitiator to check status");
//                            new DeviceInitiator(new IDeviceStatusListener(){
//                                @Override
//                                public void onDeviceStatusChanged(DeviceStatus pStatus) {
//
//                                }
//                            }).checkLocalStatusAndServer();
//                        }
                    }

                    @Override
                    public void onNext(Void aVoid) {

                    }
                });

        // 这里默认返回，实际上 这边的目的 只是先启动一下 设备状态同步的业务
        pCallback.onSuccess();

    }
}
