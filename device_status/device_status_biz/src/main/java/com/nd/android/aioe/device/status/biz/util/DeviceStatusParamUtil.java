package com.nd.android.aioe.device.status.biz.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.util.system.AdhocDeviceUtil;
import com.nd.android.adhoc.control.define.IControl_IMEI;
import com.nd.android.aioe.device.info.util.DeviceInfoHelper;
import com.nd.android.mdm.basic.ControlFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019/9/3 0003.
 */

public class DeviceStatusParamUtil {

    private static final String TAG = "DeviceStatusParamUtil";

//    @NonNull
//    public static Map<String, Object> genHardwareMap(String  pBuildSn, String pCpuSn,
//                                                     String pIMEI, String pWifiMac, String pLanMac,
//                                                     String pBlueToothMac, String pSerialNo, String pAndroidID){
//        return genHardwareMap(pBuildSn, pCpuSn, pIMEI, pWifiMac, pLanMac, pBlueToothMac, pSerialNo, pAndroidID, null);
//    }

    @NonNull
    public static Map<String, Object> genHardwareMap() throws AdhocException {
//    String  pBuildSn, String pCpuSn, String pIMEI, String pWifiMac, String pLanMac, String pBlueToothMac, String pSerialNo, String pAndroidID, String pIMEI2)

        Context context = AdhocBasicConfig.getInstance().getAppContext();
        String buildSn = AdhocDeviceUtil.getBuildSN(context);
        String cpuSn = AdhocDeviceUtil.getCpuSN();
        String wifiMac = AdhocDeviceUtil.getWifiMac(context);

        String lanMac = AdhocDeviceUtil.getEthernetMac();

        String imei = AdhocDeviceUtil.getIMEI(context);
        String imei2 = null;

        IControl_IMEI control_imei = ControlFactory.getInstance().getControl(IControl_IMEI.class);
        if (control_imei != null) {
            imei = control_imei.getIMEI(0);
            imei2 = control_imei.getIMEI(1);
        }

        if (TextUtils.isEmpty(wifiMac) && TextUtils.isEmpty(lanMac)) {
            throw new AdhocException("get wifiMac and lanMac failed");
        }


        String blueToothMac = AdhocDeviceUtil.getBloothMac();
        String serialNo = DeviceInfoHelper.getSerialNumberThroughControl();
        String androidID = AdhocDeviceUtil.getAndroidId(context);

        Logger.d(TAG, "doConfirmDeviceID, input buildSn:" + buildSn + " cpuSn:" + cpuSn + " imei:" + imei + " imei2:" + imei2
                + " wifiMac:" + wifiMac + " lanMac:" + lanMac + " blueToothMac:" + blueToothMac + " serialNo:" + serialNo
                + " androidID:" + androidID);

        Map<String, Object> mapHardware = new HashMap<>();

        if (!TextUtils.isEmpty(buildSn)) {
            mapHardware.put("build_sn", buildSn);
        }

        if (!TextUtils.isEmpty(cpuSn)) {
            mapHardware.put("cpu_sn", cpuSn);
        }

        if (!TextUtils.isEmpty(imei)) {
            mapHardware.put("imei", imei);
        }

        if (!TextUtils.isEmpty(imei2)) {
            mapHardware.put("imei2", imei2);
        }

        if (!TextUtils.isEmpty(wifiMac)) {
            mapHardware.put("wifi_mac", wifiMac);
        }

        if (!TextUtils.isEmpty(lanMac)) {
            mapHardware.put("lan_mac", lanMac);
        }

        if (!TextUtils.isEmpty(blueToothMac)) {
            mapHardware.put("btooth_mac", blueToothMac);
        }

        if (!TextUtils.isEmpty(serialNo)) {
            mapHardware.put("serial_no", serialNo);
        }

        if (!TextUtils.isEmpty(androidID)) {
            mapHardware.put("android_id", androidID);
        }

        return mapHardware;
    }
}
