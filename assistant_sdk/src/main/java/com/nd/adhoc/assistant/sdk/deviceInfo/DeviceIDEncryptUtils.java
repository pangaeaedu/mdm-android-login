package com.nd.adhoc.assistant.sdk.deviceInfo;

import com.alibaba.druid.util.Base64;

public class DeviceIDEncryptUtils {

    public static String encrypt(String pOrigin){
        return Base64.byteArrayToBase64(pOrigin.getBytes());
    }

    public static String decrypt(String pEncrypted){
        return new String(Base64.base64ToByteArray(pEncrypted));
    }
}
