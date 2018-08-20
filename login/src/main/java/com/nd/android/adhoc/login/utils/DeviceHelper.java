package com.nd.android.adhoc.login.utils;

import android.os.Build;
import android.support.annotation.NonNull;

public class DeviceHelper {

    @NonNull
    public static String getDeviceToken(){
        return getUniqueID();
    }

    public static String getUserToken(){
        return "";
    }
    @NonNull
    public static String generateSerialNum(){
        return android.os.Build.SERIAL;
    }

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
}
