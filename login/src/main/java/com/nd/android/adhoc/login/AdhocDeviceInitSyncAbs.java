package com.nd.android.adhoc.login;

import android.support.annotation.NonNull;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;
import com.nd.android.adhoc.basic.frame.api.initialization.AdhocAppInitPriority;
import com.nd.android.adhoc.basic.frame.api.initialization.AdhocAppInitSyncAbs;
import com.nd.android.adhoc.basic.frame.api.initialization.IAdhocInitCallback;
import com.nd.android.adhoc.login.processOptimization.AssistantAuthenticSystem;
import com.nd.android.adhoc.login.processOptimization.IDeviceInitiator;
import com.nd.sdp.android.serviceloader.annotation.Service;

import rx.Observer;
import rx.schedulers.Schedulers;

@Service(AdhocAppInitSyncAbs.class)
public class AdhocDeviceInitSyncAbs extends AdhocAppInitSyncAbs {

    @Override
    public AdhocAppInitPriority getInitPriority() {
        return AdhocAppInitPriority.HEIGHT;
    }

    @Override
    public void doInitSync(@NonNull final IAdhocInitCallback pCallback) {
        IDeviceInitiator initiator = AssistantAuthenticSystem.getInstance().getDeviceInitiator();

        initiator.init()
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<DeviceStatus>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        pCallback.onSuccess();
                    }

                    @Override
                    public void onNext(DeviceStatus pStatus) {
                        pCallback.onSuccess();
                    }
                });
    }


}
