package com.nd.android.aioe.device.status.biz.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019/9/3 0003.
 */

public class DeviceStatusParamUtil {

//    @NonNull
//    public static Map<String, Object> genHardwareMap(String  pBuildSn, String pCpuSn,
//                                                     String pIMEI, String pWifiMac, String pLanMac,
//                                                     String pBlueToothMac, String pSerialNo, String pAndroidID){
//        return genHardwareMap(pBuildSn, pCpuSn, pIMEI, pWifiMac, pLanMac, pBlueToothMac, pSerialNo, pAndroidID, null);
//    }

    @NonNull
    public static Map<String, Object> genHardwareMap(String  pBuildSn, String pCpuSn,
                                                     String pIMEI, String pWifiMac, String pLanMac,
                                                     String pBlueToothMac, String pSerialNo, String pAndroidID, String pIMEI2){
        Map<String, Object> mapHardware = new HashMap<>();

        if(!TextUtils.isEmpty(pBuildSn)) {
            mapHardware.put("build_sn", pBuildSn);
        }

        if(!TextUtils.isEmpty(pCpuSn)){
            mapHardware.put("cpu_sn", pCpuSn);
        }

        if(!TextUtils.isEmpty(pIMEI)) {
            mapHardware.put("imei", pIMEI);
        }

        if(!TextUtils.isEmpty(pIMEI)) {
            mapHardware.put("imei2", pIMEI2);
        }

        if(!TextUtils.isEmpty(pWifiMac)) {
            mapHardware.put("wifi_mac", pWifiMac);
        }

        if(!TextUtils.isEmpty(pLanMac)) {
            mapHardware.put("lan_mac", pLanMac);
        }

        if(!TextUtils.isEmpty(pBlueToothMac)) {
            mapHardware.put("btooth_mac", pBlueToothMac);
        }

        if(!TextUtils.isEmpty(pSerialNo)){
            mapHardware.put("serial_no", pSerialNo);
        }

        if(!TextUtils.isEmpty(pAndroidID)) {
            mapHardware.put("android_id", pAndroidID);
        }

        return mapHardware;
    }
}
