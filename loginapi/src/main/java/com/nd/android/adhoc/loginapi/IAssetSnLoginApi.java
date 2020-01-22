package com.nd.android.adhoc.loginapi;

import com.nd.android.adhoc.router_api.facade.template.IProvider;

import rx.Observable;

public interface IAssetSnLoginApi extends IProvider {
    /**
     * 登录后给该设备设置设备编码，以在后台显示
     * @param strDeviceToken     devicetoken
     * @param strAssetCode　　设备编码
     */
    Observable<Boolean> setAssetSn(String strDeviceToken, String strAssetCode) throws Exception;
}
