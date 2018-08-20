package com.nd.android.adhoc.login.utils;

import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.lang.reflect.Method;

public class DeviceHelper {
    private static Method systemProperties_get = null;

    @NonNull
    public static String getDeviceToken(){
        return getUniqueID();
    }

    public static String getUserToken(){
        return getDeviceToken();
    }
//    @NonNull
//    public static String generateSerialNum(){
//        return ;
//    }

    private static String getUniqueID(){
        String m_szDevIDShort = "35" + //we make this look like a valid IMEI
                Build.BOARD.length()%10 +
                Build.BRAND.length()%10 +
                Build.CPU_ABI.length()%10 +
                Build.DEVICE.length()%10 +
                Build.DISPLAY.length()%10 +
                Build.HOST.length()%10 +
                Build.ID.length()%10 +
                Build.MANUFACTURER.length()%10 +
                Build.MODEL.length()%10 +
                Build.PRODUCT.length()%10 +
                Build.TAGS.length()%10 +
                Build.TYPE.length()%10 +
                Build.USER.length()%10 ; //13 digits

        return m_szDevIDShort;
    }

    public static String getSerialNumber() {
        String serialNo = null;
        String[] propertys = {"ro.boot.serialno", "ro.serialno"};
        for (String key : propertys) {
            String s = getAndroidOsSystemProperties(key);
            if (!TextUtils.isEmpty(s)) {
                serialNo = s;
                return serialNo;
            }
        }
        return serialNo;
    }

    private static String getAndroidOsSystemProperties(String key) {
        String ret = null;
        try {
            systemProperties_get = Class.forName("android.os.SystemProperties").getMethod("get", String.class);
            if ((ret = (String) systemProperties_get.invoke(null, key)) != null) {
                return ret;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ret;
        }
        return ret;
    }
}
