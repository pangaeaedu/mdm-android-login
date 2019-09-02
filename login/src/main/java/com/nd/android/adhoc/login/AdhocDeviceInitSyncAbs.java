package com.nd.android.adhoc.login;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceInfoManager;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;
import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.frame.api.initialization.AdhocAppInitPriority;
import com.nd.android.adhoc.basic.frame.api.initialization.AdhocAppInitSyncAbs;
import com.nd.android.adhoc.basic.frame.api.initialization.IAdhocInitCallback;
import com.nd.android.adhoc.basic.frame.factory.AdhocFrameFactory;
import com.nd.android.adhoc.loginapi.IInitApi;
import com.nd.android.adhoc.loginapi.LoginApiRoutePathConstants;
import com.nd.android.adhoc.loginapi.exception.RetrieveWifiMacException;
import com.nd.android.adhoc.policy.api.provider.IAdhocPolicyLifeCycleProvider;
import com.nd.sdp.android.serviceloader.annotation.Service;

import rx.Observer;
import rx.schedulers.Schedulers;

@Service(AdhocAppInitSyncAbs.class)
public class AdhocDeviceInitSyncAbs extends AdhocAppInitSyncAbs {

    @Override
    public AdhocAppInitPriority getInitPriority() {
        return AdhocAppInitPriority.LOW;
    }

    @Override
    public void doInitSync(@NonNull final IAdhocInitCallback pCallback) {
        IInitApi api = (IInitApi) AdhocFrameFactory.getInstance().getAdhocRouter()
                .build(LoginApiRoutePathConstants.PATH_LOGINAPI_INIT).navigation();
        if (api == null) {
            pCallback.onFailed(new AdhocException("init api not found"));
            return;
        }

        Log.e("yhq", "init Device");
        api.initDevice()
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<DeviceStatus>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(e instanceof RetrieveWifiMacException){
                            pCallback.onFailed(new AdhocException("retrieve wifi mac error"));
                            return;
                        }

                        Log.e("yhq", "init Device error:" + e.getMessage());
                        if(!TextUtils.isEmpty(DeviceInfoManager.getInstance().getDeviceID())) {
                            updatePolicy();
                        }
                        pCallback.onSuccess();
                    }

                    @Override
                    public void onNext(DeviceStatus pStatus) {
                        pCallback.onSuccess();
                    }
                });
    }

    private void updatePolicy() {
        Log.e("yhq", "init Device updatePolicy");
        IAdhocPolicyLifeCycleProvider policyLifeCycleProvider =
                (IAdhocPolicyLifeCycleProvider) AdhocFrameFactory.getInstance()
                        .getAdhocRouter().build(IAdhocPolicyLifeCycleProvider.ROUTE_PATH).navigation();
        if (policyLifeCycleProvider == null) {
            return;
        }
        policyLifeCycleProvider.updatePolicy();
    }
}
