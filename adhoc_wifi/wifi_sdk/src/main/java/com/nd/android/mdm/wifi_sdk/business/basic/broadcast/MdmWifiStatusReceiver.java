package com.nd.android.mdm.wifi_sdk.business.basic.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.service.adapter.AdhocServiceCompat;
import com.nd.android.mdm.wifi_sdk.business.basic.constant.MdmWifiConstant;

/**
 * Created by HuangYK on 2018/3/17.
 */

public class MdmWifiStatusReceiver extends BroadcastReceiver {

    public void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        AdhocBasicConfig.getInstance().getAppContext().registerReceiver(this, intentFilter);
    }

    public void unregisterReceiver() {
        AdhocBasicConfig.getInstance().getAppContext().unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }

        Intent serviceIntent = new Intent(context, MdmWifiStatusIntentService.class);
        serviceIntent.putExtra(MdmWifiConstant.WIFI_ACTION, action);

        switch (action) {
//            case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION: // startScan() 扫描成功后的通知
//                mReceiverCallback.onScanResultsAvailable();
//                break;
            case WifiManager.NETWORK_STATE_CHANGED_ACTION: // wifi 连接状态发生改变
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                serviceIntent.putExtra(WifiManager.EXTRA_NETWORK_INFO, info);

//                NetworkInfo.DetailedState state = info.getDetailedState();
//                if (null != state) {
//                    mReceiverCallback.onNetworkStateChanged(state);
//                }
                break;
            case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION: // 针对 wifi 模式连接状态改变的监听 和  NETWORK 不同，这里只针对wifi，不是针对整个网络
                SupplicantState supplicantState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                if (supplicantState == null) {
                    break;
                }

                int error = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 0);
                serviceIntent.putExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, error);
//                mReceiverCallback.onSupplicantStateChange(supplicantState, error);
                break;
            default:
                break;
        }

        AdhocServiceCompat.startService(context, serviceIntent);

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            AdhocBasicConfig.getInstance().getAppContext().startForegroundService(serviceIntent);
//        } else {
//            AdhocBasicConfig.getInstance().getAppContext().startService(serviceIntent);
//        }
    }
}
