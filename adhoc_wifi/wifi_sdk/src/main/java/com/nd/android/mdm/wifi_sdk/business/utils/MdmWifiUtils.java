package com.nd.android.mdm.wifi_sdk.business.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.mdm.wifi_sdk.business.basic.constant.MdmWifiBand;

import java.util.List;


/**
 * Created by Administrator on 2016/12/30 0030.
 */

public class MdmWifiUtils {

    private static final String TAG = "MdmWifiUtils";

    private static final Pair<Integer, Integer> GHZ2_RANGE = new Pair<>(2400, 2499);
    private static final Pair<Integer, Integer> GHZ5_RANGE = new Pair<>(4900, 5899);

    public static int getChannelByFrequency(int frequency) {
        if (frequency >= 2412 && frequency <= 2484) {
            return (frequency - 2412) / 5 + 1;
        } else if (frequency >= 5170 && frequency <= 5825) {
            return (frequency - 5170) / 5 + 34;
        } else {
            return -1;
        }
    }

    public static boolean isInBandRange(int frequency, @NonNull MdmWifiBand band) {
        int start = (band == MdmWifiBand.GHZ2_4 ? GHZ2_RANGE.first : GHZ5_RANGE.first);
        int end = (band == MdmWifiBand.GHZ2_4 ? GHZ2_RANGE.second : GHZ5_RANGE.second);
        return frequency >= start && frequency <= end;
    }

    public static WifiConfiguration isExsits(Context context, String SSID) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        if (existingConfigs != null) {
            for (WifiConfiguration existingConfig : existingConfigs) {
                if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                    return existingConfig;
                }
            }
        }
        return null;
    }

    public static String removeDoubleQuotes(String string) {
        if (string == null) return null;
        final int length = string.length();
        if ((length > 1) && (string.charAt(0) == '"') && (string.charAt(length - 1) == '"')) {
            return string.substring(1, length - 1);
        }
        return string;
    }


    /**
     * 连接wifi
     *
     * @param context
     * @param SSID
     * @param password
     * @param type
     * @return
     */
    public static WifiConfiguration createWifiInfo(Context context, String SSID, String password, int type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        config.status = WifiConfiguration.Status.DISABLED;
        config.priority = 40;

        WifiConfiguration tempConfig = isExsits(context, SSID);
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (tempConfig != null) {
            wifiManager.removeNetwork(tempConfig.networkId);
        }
        // WIFICIPHER_NOPASS
        if (type == 1) {
            // config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            // config.wepTxKeyIndex = 0;
            // config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            // config.allowedAuthAlgorithms.clear();
            // config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            // config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            // config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            // config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            // config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        }
        // WIFICIPHER_WEP
        if (type == 2) {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        // WIFICIPHER_WPA
        if (type == 3) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }


    // 添加一个网络并连接
    public static boolean addNetwork(Context context, WifiConfiguration wcg) {
        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wm == null) {
            return false;
        }
        int wcgID = wm.addNetwork(wcg);
        if (wcgID == -1) {
            return false;
        }
        boolean result = wm.enableNetwork(wcgID, true);
        Logger.i(TAG, "add net id:" + wcgID + " result:" + result);
        return result && wm.saveConfiguration();
    }


    /**
     * 获取当前网络SSID
     *
     * @param context
     * @return
     */
    public static String getCurrentNetworkSSID(Context context) {
        if (isWifi(context)) {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                return wifiInfo.getSSID();
            }
        }
        return null;
    }

    public static int getCurrentNetworkLinkSpeed(Context context) {
        if (isWifi(context)) {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                return wifiInfo.getLinkSpeed();
            }
        }
        return 0;
    }

    public static int getCurrentNetworkRSSI(Context context) {
        if (isWifi(context)) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                return wifiInfo.getRssi();
            }
        }
        return -200;
    }

    /**
     * 判断当前网络是否wifi
     *
     * @param context
     * @return
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager connectMgr = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectMgr.getActiveNetworkInfo();
        if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }
}
