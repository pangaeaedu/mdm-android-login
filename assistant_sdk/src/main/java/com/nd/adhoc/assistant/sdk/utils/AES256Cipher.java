package com.nd.adhoc.assistant.sdk.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/*
 * AES 加密算法
 */
public class AES256Cipher{
    private static SecretKeySpec getKeySpec(String key) {
        StringBuffer pad = new StringBuffer();
        pad.append(key);
        if (key.length()<32){
            for (int i=0; i<32-key.length(); ++i){
                pad.append('\0');
            }
        }
        byte[] keyBytes = pad.toString().getBytes();
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static byte[] aesEncrypt(byte []content, String key) throws BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec spec = getKeySpec(key);
        Cipher cipherEnc = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipherEnc.init(Cipher.ENCRYPT_MODE, spec);
        byte[] result = cipherEnc.doFinal(content);
        return result;
    }

    public static byte[] aesDecrypt(byte []content, int position, int length, String key) throws BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec spec = getKeySpec(key);
        Cipher cipherDec = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipherDec.init(Cipher.DECRYPT_MODE, spec);
        byte[] result = cipherDec.doFinal(content, position, length);
        return result;
    }

    public static String getEncryptResult(String content, String key) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }

        if (TextUtils.isEmpty(key)) {
            return content;
        }

        try {
            String result = Base64.encodeToString(AES256Cipher.aesEncrypt(content.getBytes(), key), Base64.NO_WRAP);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getDecryptResult(String content, String key){
        if(TextUtils.isEmpty(content)){
            return "";
        }

        if(TextUtils.isEmpty(key)){
            return content;
        }

        try {
            byte[] b = Base64.decode(content, Base64.NO_WRAP);
            String result = new String(AES256Cipher.aesDecrypt(b, 0, b.length, key));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}