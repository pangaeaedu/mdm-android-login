package com.nd.android.adhoc.login;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.login.basicService.data.http.EnrollUserInfoResult;
import com.nd.android.adhoc.login.processOptimization.BaseAbilityProvider;
import com.nd.android.adhoc.loginapi.IAssetSnLoginApi;
import com.nd.android.adhoc.router_api.facade.annotation.Route;

import rx.Observable;
import rx.Subscriber;

@Route(path = LoginRoutePathConstants.PATH_LOGIN_ASSET_SN_LOGIN)
public class AssetSnLoginApiImpl extends BaseAbilityProvider implements IAssetSnLoginApi {
    private static final String TAG = "AssetSnLoginApiImpl";

    @Override
    public Observable<Boolean> setAssetSn(final String strDeviceToken, final String strAssetCode) throws Exception {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> pSubscriber) {
                try {
                    EnrollUserInfoResult response = getHttpService().setAssetCode(strDeviceToken, strAssetCode);

                    if (null != response && 0 == response.getErrcode()) {
                        pSubscriber.onNext(true);
                        pSubscriber.onCompleted();
                        return;
                    }else {
                        pSubscriber.onError(new AdhocException("setAssetSn returns non zero value"));
                    }
                } catch (Exception pE) {
                    Logger.e(TAG, pE.getMessage());
                    pSubscriber.onError(pE);
                }
            }
        });
    }

    @Override
    public void init(@NonNull Context pContext) {

    }
}
