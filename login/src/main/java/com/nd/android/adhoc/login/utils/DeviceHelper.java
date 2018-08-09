package com.nd.android.adhoc.login.utils;

import android.os.Build;
import android.support.annotation.NonNull;

public class DeviceHelper {

    @NonNull
    public static String generateDeviceToken(){
        return getUniqeID();
    }

    @NonNull
    public static String generateSerialNum(){
        return android.os.Build.SERIAL;
    }

//    private String getIMEI(Context pContext){
//        try{
//            TelephonyManager TelephonyMgr = (TelephonyManager)pContext
//                    .getSystemService(TELEPHONY_SERVICE);
//            String szImei = TelephonyMgr.getDeviceId();
//            return szImei;
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        return "";
//    }

    private static String getUniqeID(){
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

//    private String getAndroidID(Context pContext){
//        String m_szAndroidID = Settings.Secure.getString(pContext.getContentResolver(),
//                Settings.Secure.ANDROID_ID);
//    }

}
