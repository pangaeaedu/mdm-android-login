package com.nd.android.aioe.device.info.util;

import com.nd.android.adhoc.basic.util.string.robust.CipherUtil;
import com.nd.android.adhoc.basic.util.string.robust.ValueConfig;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class DeviceIDEncryptUtils {

    private static final String MDM_KEY = "903f76ef705ff1da2c19bd9f34482093";
    private static final SimpleDateFormat formatter  = new SimpleDateFormat("yyyyMMddHHmmss");
    public static String encrypt(String pOrigin) {
        try {
            return CipherUtil.getEncryptResult(pOrigin, MDM_KEY);
//            return DesUtils.encrypt(pOrigin, MDM_KEY);
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
            if (pEncrypted.startsWith(ValueConfig.FLAG_VALUE)) {
                return CipherUtil.getDecryptResult(pEncrypted, MDM_KEY);
            }
            return DesUtils.decrypt(pEncrypted, MDM_KEY);
        } catch (Exception pE) {
            pE.printStackTrace();
        }

        return "";
    }
}
