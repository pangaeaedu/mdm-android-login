package com.nd.android.mdm.wifi_sdk.business.basic.broadcast;

import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.support.annotation.NonNull;

/**
 * Created by HuangYK on 2018/3/17.
 */

public interface IMdmWifiStatusReceiverCallback {

    void onScanResultsAvailable();

    void onNetworkStateChanged(@NonNull NetworkInfo.DetailedState pState);

    void onSupplicantStateChange(@NonNull SupplicantState pState, int pErrorCode);

}
