package com.nd.android.aioe.device.info.util;

import android.os.Build;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.util.string.AdhocMD5Util;
import com.nd.android.adhoc.control.define.IControl_DeviceSerial;
import com.nd.android.aioe.device.info.config.DeviceInfoSpConfig;
import com.nd.android.mdm.basic.ControlFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

public class DeviceInfoHelper {

    private static final String TAG = "DeviceStatus";

    @NonNull
    public static String getDeviceToken() {
//        AssistantSpConfig config = AssistantBasicServiceFactory.getInstance().getSpConfig();
//        if(!TextUtils.isEmpty(config.getDeviceToken())){
//            return config.getDeviceToken();
//        }
//
//        return getDeviceTokenFromSystem();

        return DeviceInfoSpConfig.getDeviceID();
    }

//    public static String getV2DeviceToken() {
//
//        String deviceToken = DeviceInfoSpConfig.getDeviceToken();
//        if (!TextUtils.isEmpty(deviceToken)) {
//            return deviceToken;
//        }
//
//        return getDeviceTokenFromSystem();
//    }

    public static String getDeviceTokenFromSystem() {
        String serialNum = getSerialNumber();
        Logger.d(TAG, "DeviceInfoHelper, getDeviceTokenFromSystem serialNum: " + serialNum);

        if (TextUtils.isEmpty(serialNum) || Build.UNKNOWN.equalsIgnoreCase(serialNum)) {
            serialNum = getSerialNumForOPSG();
            Logger.d(TAG, "DeviceInfoHelper, getDeviceTokenFromSystem opsg serial: " + serialNum);
        }

        String id = getUniqueID() + serialNum;
        return AdhocMD5Util.getStringMd5(id);
    }

    public static String getUserToken() {
        return "";
    }

//    public static String getUserToken(){
//        return getDeviceToken();
//    }
//    @NonNull
//    public static String generateSerialNum(){
//        return ;
//    }

    private static String getUniqueID() {
        String m_szDevIDShort = "35" + //we make this look like a valid IMEI
                Build.BOARD.length() % 10 +
                Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 +
                Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 +
                Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 +
                Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 digits

        return m_szDevIDShort;
    }

    public static String getSerialNumForOPSG() {
        String mac = "";
        try {
            InputStream is = Runtime.getRuntime().exec("cat /tmp/factory/sn.txt").getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bf = new BufferedReader(isr);
            String line = bf.readLine();
            if (line != null) {
                mac = line;
            }
            bf.close();
            isr.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mac;
    }

    public static String getSerialNumberThroughControl() {
        IControl_DeviceSerial serial = ControlFactory.getInstance().getControl
                (IControl_DeviceSerial.class);

        String serialNum = null;

        try {
            serialNum = serial == null ? null : serial.getSerialNumber();
        } catch (Exception e) {
            Logger.w(TAG, "get serial by control error: " + e);
        }

        if (!TextUtils.isEmpty(serialNum)) {
            return serialNum;
        }

        return getSerialNumber();
    }

    public static String getDeviceSerialNumberThroughControl() {
        IControl_DeviceSerial serial = ControlFactory.getInstance().getControl
                (IControl_DeviceSerial.class);
        if (serial == null) {
            return "";
        }
        try {
            String serialNum = serial.getDeviceSerialNumber();
            if (TextUtils.isEmpty(serialNum)) {
                return getSerialNumber();
            }

            return serialNum;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return getSerialNumber();
    }


    public static String getSerialNumber() {
        String serialNo = Build.SERIAL;
        if (!TextUtils.isEmpty(serialNo)) {
            return serialNo;
        }
        String[] propertys = {"ro.boot.serialno", "ro.serialno", "ro.serialnocustom"};
        for (String key : propertys) {
            String sn = getAndroidOsSystemProperties(key);
            if (!TextUtils.isEmpty(sn)) {
                return sn;
            }
        }
        return null;
    }

    private static String getAndroidOsSystemProperties(String key) {
        String ret;
        try {
            Method systemProperties_get = Class.forName("android.os.SystemProperties").getMethod("get", String.class);
            if ((ret = (String) systemProperties_get.invoke(null, key)) != null) {
                return ret;
            }
        } catch (Exception e) {
            Logger.e(TAG, "getAndroidOsSystemProperties with key [" + key + "] error: " + e);
        }
        return null;
    }
}
