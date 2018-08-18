package com.nd.android.mdm.wifi_sdk.business.basic.listener;

import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;

/**
 * Created by HuangYK on 2018/3/14.
 */

public interface IMdmWifiStateChangeListener {

    void onSupplicantStateChange(SupplicantState pState, WifiInfo pWifiInfo);

    void onNetworkStateChange(NetworkInfo.DetailedState pState, WifiInfo pWifiInfo);

}
