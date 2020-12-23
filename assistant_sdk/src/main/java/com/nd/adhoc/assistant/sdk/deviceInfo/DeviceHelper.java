package com.nd.adhoc.assistant.sdk.deviceInfo;

import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.nd.adhoc.assistant.sdk.AssistantBasicServiceFactory;
import com.nd.adhoc.assistant.sdk.config.AssistantSpConfig;
import com.nd.adhoc.assistant.sdk.utils.MD5ArithmeticUtils;
import com.nd.android.adhoc.control.define.IControl_DeviceSerial;
import com.nd.android.mdm.basic.ControlFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;

public class DeviceHelper {
    private static Method systemProperties_get = null;

    @NonNull
    public static String getDeviceToken(){
//        AssistantSpConfig config = AssistantBasicServiceFactory.getInstance().getSpConfig();
//        if(!TextUtils.isEmpty(config.getDeviceToken())){
//            return config.getDeviceToken();
//        }
//
//        return getDeviceTokenFromSystem();

        AssistantSpConfig config = AssistantBasicServiceFactory.getInstance().getSpConfig();
        return config.getDeviceID();
    }

    public static String getV2DeviceToken(){
                AssistantSpConfig config = AssistantBasicServiceFactory.getInstance().getSpConfig();
        if(!TextUtils.isEmpty(config.getDeviceToken())){
            return config.getDeviceToken();
        }

        return getDeviceTokenFromSystem();
    }

    public static String getDeviceTokenFromSystem(){
        String serialNum = getSerialNumber();
        Log.e("serial", "normal serial:"+serialNum);
        if(TextUtils.isEmpty(serialNum) || serialNum.equalsIgnoreCase(Build.UNKNOWN)){
            serialNum = getSerialNumForOPSG();
            Log.e("serial", "opsg serial:"+serialNum);
        }

        String id = getUniqueID()+serialNum;
        try {
            return MD5ArithmeticUtils.getMd5(id);
        } catch (NoSuchAlgorithmException pE) {
            pE.printStackTrace();
        }

        return id;
    }

    public static String getUserToken(){
        return "";
    }

//    public static String getUserToken(){
//        return getDeviceToken();
//    }
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

    public static String getSerialNumberThroughControl(){
        IControl_DeviceSerial serial = ControlFactory.getInstance().getControl
                (IControl_DeviceSerial.class);
        try {
            String serialNum = serial.getSerialNumber();
            if(TextUtils.isEmpty(serialNum)){
                return getSerialNumber();
            }

            return serialNum;
        }catch (Exception e){
            e.printStackTrace();
        }

        return getSerialNumber();
    }


    public static String getDeviceSerialNumberThroughControl(){
        IControl_DeviceSerial serial = ControlFactory.getInstance().getControl
                (IControl_DeviceSerial.class);
        if(serial == null){
            return "";
        }
        try {
            String serialNum = serial.getDeviceSerialNumber();
            if(TextUtils.isEmpty(serialNum)){
                return getSerialNumber();
            }

            return serialNum;
        }catch (Exception e){
            e.printStackTrace();
        }

        return getSerialNumber();
    }


    public static String getSerialNumber() {
        String serialNo = android.os.Build.SERIAL;
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
            systemProperties_get = Class.forName("android.os.SystemProperties").getMethod("get", String.class);
            if ((ret = (String) systemProperties_get.invoke(null, key)) != null) {
                return ret;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
