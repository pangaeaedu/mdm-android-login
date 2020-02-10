package com.nd.android.mdm.wifi_sdk.business.basic.broadcast;

import android.app.IntentService;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.service.AdhocIntentService;
import com.nd.android.mdm.wifi_sdk.business.basic.constant.MdmWifiConstant;

/**
 * Created by HuangYK on 2019/3/2 0002.
 */

public class MdmWifiStatusIntentService extends IntentService {

    private static final String TAG = "MdmWifiStatusIntentService";

    public MdmWifiStatusIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Bundle bundle;

        if (intent == null ||
                (bundle = intent.getExtras()) == null) {
            return;
        }

        String action = bundle.getString(MdmWifiConstant.WIFI_ACTION);
        if (TextUtils.isEmpty(action)) {
            return;
        }

        switch (action) {
            case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION: // startScan() 扫描成功后的通知
                MdmWifiStatusListenerManager.getInstance().onScanResultsAvailable();
                break;
            case WifiManager.NETWORK_STATE_CHANGED_ACTION: // wifi 连接状态发生改变
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                NetworkInfo.DetailedState state = info.getDetailedState();
                if (null != state) {
                    MdmWifiStatusListenerManager.getInstance().onNetworkStateChanged(state);
                }
                break;
            case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION: // 针对 wifi 模式连接状态改变的监听 和  NETWORK 不同，这里只针对wifi，不是针对整个网络
                SupplicantState supplicantState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                if (supplicantState == null) {
                    break;
                }

                int error = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 0);
                MdmWifiStatusListenerManager.getInstance().onSupplicantStateChange(supplicantState, error);
                break;
            default:
                break;
        }


    }
}
