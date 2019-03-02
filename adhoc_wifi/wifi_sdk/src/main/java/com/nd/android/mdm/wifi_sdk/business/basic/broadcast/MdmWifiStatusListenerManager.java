package com.nd.android.mdm.wifi_sdk.business.basic.broadcast;

import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.log.Logger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by HuangYK on 2019/3/2 0002.
 */

public class MdmWifiStatusListenerManager {

    private static final String TAG = "MdmWifiStatusListenerManager";

    private volatile static MdmWifiStatusListenerManager sInstance = null;


    private List<IMdmWifiStatusListener> mWifiStatusListener = new CopyOnWriteArrayList<>();

    public static MdmWifiStatusListenerManager getInstance() {
        if (sInstance == null) {
            synchronized (MdmWifiStatusListenerManager.class) {
                if (sInstance == null) {
                    sInstance = new MdmWifiStatusListenerManager();
                }
            }
        }
        return sInstance;
    }

    public void addListener(@NonNull IMdmWifiStatusListener pListener) {
        if (mWifiStatusListener.contains(pListener)) {
            return;
        }

        mWifiStatusListener.add(pListener);
    }

    public void removeListener(IMdmWifiStatusListener pListener) {
        mWifiStatusListener.remove(pListener);
    }

    public void onScanResultsAvailable() {
        for (IMdmWifiStatusListener listener : mWifiStatusListener) {
            try {
                listener.onScanResultsAvailable();
            } catch (Exception e) {
                Logger.w(TAG, "onScanResultsAvailable error: " + e);
            }
        }
    }

    public void onNetworkStateChanged(@NonNull NetworkInfo.DetailedState pState) {
        for (IMdmWifiStatusListener listener : mWifiStatusListener) {
            try {
                listener.onNetworkStateChanged(pState);
            } catch (Exception e) {
                Logger.w(TAG, "onNetworkStateChanged error: " + e);
            }
        }
    }

    public void onSupplicantStateChange(@NonNull SupplicantState pState, int pErrorCode) {
        for (IMdmWifiStatusListener listener : mWifiStatusListener) {
            try {
                listener.onSupplicantStateChange(pState, pErrorCode);
            } catch (Exception e) {
                Logger.w(TAG, "onSupplicantStateChange error: " + e);
            }
        }
    }

}
