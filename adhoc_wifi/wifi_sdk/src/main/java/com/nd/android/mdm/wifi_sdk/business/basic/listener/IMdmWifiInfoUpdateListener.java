package com.nd.android.mdm.wifi_sdk.business.basic.listener;

import com.nd.android.mdm.wifi_sdk.business.bean.MdmWifiInfo;

/**
 * Created by HuangYK on 2018/3/15.
 */

public interface IMdmWifiInfoUpdateListener {
    void onInfoUpdated(MdmWifiInfo pWifiInfo);
}
