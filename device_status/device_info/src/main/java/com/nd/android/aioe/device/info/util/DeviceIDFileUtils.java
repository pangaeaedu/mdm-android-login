package com.nd.android.aioe.device.info.util;

import android.content.Context;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.util.storage.AdhocFileReadUtil;
import com.nd.android.adhoc.basic.util.storage.AdhocFileWriteUtil;
import com.nd.android.adhoc.basic.util.storage.AdhocStorageUtil;
import com.nd.android.adhoc.basic.util.string.AdhocMD5Util;

public class DeviceIDFileUtils {

    public static String loadDeviceIDFromSdCard(Context pContext) {
        String idInCache = loadFromCacheFile(pContext);
        String idInSdFile = loadFromSDCardFile(pContext);
        if(TextUtils.isEmpty(idInCache) && !TextUtils.isEmpty(idInSdFile)){
            return idInSdFile;
        }

        if(TextUtils.isEmpty(idInSdFile) && !TextUtils.isEmpty(idInCache)){
            return idInCache;
        }

        if(idInCache.equalsIgnoreCase(idInSdFile)){
            return idInCache;
        }

        return "";
    }

    public static String loadFromSDCardFile(Context pContext) {
        String path = getSdCardFilePath(pContext);
        if (TextUtils.isEmpty(path)) {
            return "";
        }
        return loadDeviceIDFromFile(path);
    }

    public static String loadFromCacheFile(Context pContext) {
        String path = getCacheFilePath(pContext);
        if (TextUtils.isEmpty(path)) {
            return "";
        }
        return loadDeviceIDFromFile(path);
    }

    private static String loadDeviceIDFromFile(String pFilePath) {
        StringBuilder builder = AdhocFileReadUtil.readFile(pFilePath, "UTF-8");
        if (builder == null) {
            return "";
        }

        String encrypted = builder.toString();
        if(TextUtils.isEmpty(encrypted)){
            return "";
        }

//        return new String(Base64.base64ToByteArray(encrypted));
        return DeviceIDEncryptUtils.decrypt(encrypted);
    }

    public static boolean saveDeviceIDToCacheFile(Context pContext, String pEncryptDeviceID) {
        String path = getCacheFilePath(pContext);
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        return AdhocFileWriteUtil.writeFile(path, pEncryptDeviceID, false);
    }

    public static boolean saveDeviceIDToSdFile(Context pContext, String pEncryptDeviceID) {
        String path = getSdCardFilePath(pContext);
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        return AdhocFileWriteUtil.writeFile(path, pEncryptDeviceID, false);
    }


    @Nullable
    private static String getCacheFilePath(Context pContext) {
        try {
            String innerDir = AdhocStorageUtil.getSDCardCacheDir(pContext);
            innerDir = AdhocStorageUtil.makesureFileSepInTheEnd(innerDir);
            String pageName = AdhocMD5Util.getStringMd5(pContext.getPackageName());

            return innerDir + pageName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Nullable
    private static String getSdCardFilePath(Context pContext) {
        try {
            String sdDir = AdhocStorageUtil.getSdCardPath();
            sdDir = AdhocStorageUtil.makesureFileSepInTheEnd(sdDir);
            sdDir += "assistant";
            sdDir = AdhocStorageUtil.makesureFileSepInTheEnd(sdDir);

            String pageName = AdhocMD5Util.getStringMd5(pContext.getPackageName());
            return sdDir + pageName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
