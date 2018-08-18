package com.nd.android.mdm.wifi_sdk.business.service.intfc;

import com.nd.android.mdm.wifi_sdk.business.bean.MdmWifiVendor;

import rx.Observable;

/**
 * wifi 厂商信息业务服务接口
 * <p>
 * Created by HuangYK on 2018/3/14.
 */
public interface IMdmWifiVendorBizService {

    Observable<MdmWifiVendor> getWifiVendor(String pBSSID);
}
