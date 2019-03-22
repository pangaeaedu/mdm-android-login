package com.nd.adhoc.assistant.sdk.deviceInfo;

import com.nd.adhoc.assistant.sdk.utils.DesUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class DeviceIDEncryptUtils {

    private static final String MDM_KEY = "903f76ef705ff1da2c19bd9f34482093";
    private static final SimpleDateFormat formatter  = new SimpleDateFormat("yyyyMMddHHmmss");
    public static String encrypt(String pOrigin) {
        try {
            return DesUtils.encrypt(pOrigin, MDM_KEY);
        } catch (Exception pE) {
            pE.printStackTrace();
        }

        return "";
    }

    public static String encryptPassword(String pPassword){
        String prefixSix = UUID.randomUUID().toString()
                .replace("-", "").substring(0, 6);
        String time = formatter.format(new Date());

        String result = prefixSix+time+pPassword;
        return encrypt(result);
    }

    public static String decrypt(String pEncrypted){
        try {
            return DesUtils.decrypt(pEncrypted, MDM_KEY);
        } catch (Exception pE) {
            pE.printStackTrace();
        }

        return "";
    }
}
