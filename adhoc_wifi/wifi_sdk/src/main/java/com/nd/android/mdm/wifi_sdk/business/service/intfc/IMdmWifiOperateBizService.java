package com.nd.android.mdm.wifi_sdk.business.service.intfc;

import com.nd.android.mdm.wifi_sdk.business.bean.MdmWifiItemInfo;

import java.util.List;

import rx.Observable;

/**
 * Created by HuangYK on 2018/3/19.
 */

public interface IMdmWifiOperateBizService {

    Observable<List<MdmWifiItemInfo>> getWifiItemList();

    Observable<Boolean> connectWifi(MdmWifiItemInfo pItemInfo);

}
