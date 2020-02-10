package com.nd.android.adhoc.login.processOptimization;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.nd.adhoc.assistant.sdk.AssistantBasicServiceFactory;
import com.nd.adhoc.assistant.sdk.config.AssistantSpConfig;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceInfoManager;
import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.util.system.AdhocDeviceUtil;
import com.nd.android.adhoc.login.basicService.BasicServiceFactory;
import com.nd.android.adhoc.login.basicService.http.IHttpService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019/11/27 0027.
 */

public class HardwareInfoCompleteManager {
    private static final String TAG = "Hardware";
    private static final HardwareInfoCompleteManager ourInstance = new HardwareInfoCompleteManager();

    public static HardwareInfoCompleteManager getInstance() {
        return ourInstance;
    }

    private HardwareInfoCompleteManager() {
    }


    /*
    这个上报是因为opsg上，重置系统，所有的硬件标识都会被修改，目前观察到不被改的只有三个
    wifi mac, lan mac, serial no。而我们比较设备是否命中，是至少要两个一模一样。
    旧版上，如果wifi mac没有取到，而之前又没有去取lan mac。那么就只剩一个SerialNo有用了
    这个时候覆盖安装新版本，并不会把后续的硬件信息补上去。
     */
    public synchronized void reportHardwareInfoIfNecessary() {
        try {
            boolean isWifiMacReported = getConfig().isWifiMacReported();
            boolean isLanMacReported = getConfig().isLanMacReported();
            if (isWifiMacReported && isLanMacReported) {
                Log.d(TAG, "lan mac and wifi mac reported");
                return;
            }

            Context context = AdhocBasicConfig.getInstance().getAppContext();

            String wifiMac = "";
            String lanMac = "";
            Map<String, Object> hardwareInfo = new HashMap<>();

            if (!isWifiMacReported) {
                wifiMac = AdhocDeviceUtil.getWifiMac(context);
                if (!TextUtils.isEmpty(wifiMac)) {
                    hardwareInfo.put("wifi_mac", wifiMac);
                } else {
                    Log.e(TAG, "wifi mac not found");
                }
            }

            if (!isLanMacReported) {
                lanMac = AdhocDeviceUtil.getEthernetMac();
                if (!TextUtils.isEmpty(lanMac)) {
                    hardwareInfo.put("lan_mac", lanMac);
                } else {
                    Log.e(TAG, "lan mac not found");
                }
            }

            if (TextUtils.isEmpty(wifiMac) && TextUtils.isEmpty(lanMac)) {
                Log.e(TAG, "lan mac and wifi mac not found");
                return;
            }

            String deviceID = DeviceInfoManager.getInstance().getDeviceID();
            hardwareInfo.put("device_token", deviceID);
            boolean bOk = getHttpService().reportHardwareInfo(deviceID, hardwareInfo);
            if (bOk) {
                if (!TextUtils.isEmpty(wifiMac)) {
                    Log.e(TAG, "wifi mac reported");
                    getConfig().setWifiMacReported(true);
                }

                if (!TextUtils.isEmpty(lanMac)) {
                    Log.e(TAG, "lan mac reported");
                    getConfig().setLanMacReported(true);
                }
            }
        } catch (Exception pE) {
            pE.printStackTrace();
        }
    }


    protected AssistantSpConfig getConfig() {
        return AssistantBasicServiceFactory.getInstance().getSpConfig();
    }

    protected IHttpService getHttpService() {
        return BasicServiceFactory.getInstance().getHttpService();
    }
}
