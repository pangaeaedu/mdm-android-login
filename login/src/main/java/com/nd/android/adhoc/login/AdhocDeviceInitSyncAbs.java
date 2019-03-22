package com.nd.android.adhoc.login;

import android.support.annotation.NonNull;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;
import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.frame.api.initialization.AdhocAppInitPriority;
import com.nd.android.adhoc.basic.frame.api.initialization.AdhocAppInitSyncAbs;
import com.nd.android.adhoc.basic.frame.api.initialization.IAdhocInitCallback;
import com.nd.android.adhoc.basic.frame.factory.AdhocFrameFactory;
import com.nd.android.adhoc.loginapi.IInitApi;
import com.nd.android.adhoc.loginapi.LoginApiRoutePathConstants;
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
        IInitApi api = (IInitApi) AdhocFrameFactory.getInstance().getAdhocRouter()
                .build(LoginApiRoutePathConstants.PATH_LOGINAPI_INIT).navigation();
        if(api == null){
            pCallback.onFailed(new AdhocException("init api not found"));
            return;
        }

//        IDeviceInitiator initiator = AssistantAuthenticSystem.getInstance().getDeviceInitiator();
//        initiator.init()
        api.initDevice()
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
