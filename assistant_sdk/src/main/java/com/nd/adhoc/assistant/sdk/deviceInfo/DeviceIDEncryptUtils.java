package com.nd.adhoc.assistant.sdk.deviceInfo;

import com.nd.adhoc.assistant.sdk.utils.AES256Cipher;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class DeviceIDEncryptUtils {

    private static final String MDM_KEY = "903f76ef705ff1da2c19bd9f34482093";
    private static final SimpleDateFormat formatter  = new SimpleDateFormat("yyyyMMddHHmmss");
    public static String encrypt(String pOrigin){
        return AES256Cipher.getEncryptResult(pOrigin, MDM_KEY);
    }

    public static String encryptPassword(String pPassword){
        String prefixSix = UUID.randomUUID().toString()
                .replace("-", "").substring(0, 6);
        String time = formatter.format(new Date());

        String result = prefixSix+time+pPassword;
        return encrypt(result);
    }

    public static String decrypt(String pEncrypted){
        return AES256Cipher.getDecryptResult(pEncrypted, MDM_KEY);
    }
}
