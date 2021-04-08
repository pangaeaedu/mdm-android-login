package com.nd.android.mdm.monitor.listener;

import android.content.Context;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.common.toast.AdhocToastModule;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.util.net.AdhocNetworkIpUtil;
import com.nd.android.adhoc.basic.util.thread.AdhocMainLooper;
import com.nd.android.adhoc.communicate.connect.listener.IAdhocConnectListener;
import com.nd.android.adhoc.communicate.impl.MdmTransferFactory;
import com.nd.android.aioe.device.info.cache.DeviceIdCache;
import com.nd.android.mdm.monitor.MonitorModule;
import com.nd.android.mdm.monitor.info.AdhocBatteryInfo;
import com.nd.android.mdm.monitor.message.BatteryChangeMessage;
import com.nd.android.mdm.monitor.message.ModelMessage;
import com.nd.sdp.android.serviceloader.annotation.Service;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by HuangYK on 2018/12/17.
 */
@Service(IAdhocConnectListener.class)
public class AdhocConnectListener implements IAdhocConnectListener {
    private static final String TAG = "AdhocConnectListener";

    @Override
    public void onConnectionAvaialble() {
        JSONObject deviceInfo = null;
        try {
            deviceInfo = MonitorModule.getInstance().getDevInfoJson();
        } catch (JSONException e) {
            Logger.w(TAG, "onConnectionAvaialble getDevInfoJson error: " + e);
        }

        MdmTransferFactory.getCommunicationModule()
                .sendLoginInfo(DeviceIdCache.getDeviceId(), deviceInfo);

        AdhocMainLooper.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AdhocToastModule.getInstance().showToast("adhoc on Connection Avaialble.");
            }
        });

        Context context = AdhocBasicConfig.getInstance().getAppContext();
        String mac = AdhocNetworkIpUtil.getLocalMacAddressFromIp(context, AdhocNetworkIpUtil.getCurrentIp(context));

        AdhocBatteryInfo batteryInfo = MonitorModule.getInstance().getBatteryInfo();
        // 发送电量改变消息
        new BatteryChangeMessage(batteryInfo.level, batteryInfo.isCharging, mac).send();
//            // 发送手机型号
        new ModelMessage(mac).send();
//            // 发送穿戴信息
//            new VRWearMessage(mac).send();
//            // 发送usb接入状况
//            new UsbAttachMessage(mac, isUsbAttached()).send();
//            // 发送是否支持辅助功能
//            new AccessibilityMessage(mac, isAccessibilitySettingsOn()).send();
    }
}
