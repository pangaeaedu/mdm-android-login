package com.nd.android.aioe.device.info.util;

import android.Manifest;
import android.content.Context;
import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.sp.SharedPreferenceFactory;
import com.nd.android.adhoc.basic.util.permission.AdhocRxPermissions;
import com.nd.android.aioe.device.info.config.DeviceInfoSpConfig;

import java.util.UUID;

import rx.Observer;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class DeviceIDSPUtils {

    private static final String TAG = "DeviceStatus";

    // DeviceID不一样，报日志
    public static void startNewThreadToCheckDeviceIDIntegrity(final Context pContext,
                                                              final String pDeviceID) {
        AdhocRxPermissions.getInstance(pContext)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean pBoolean) {
                        String encrypt = DeviceIDEncryptUtils.encrypt(pDeviceID);

                        String idInCache = DeviceIDFileUtils.loadFromCacheFile(pContext);
                        if (!pDeviceID.equalsIgnoreCase(idInCache)) {
                           boolean bOK = DeviceIDFileUtils.saveDeviceIDToCacheFile(pContext, encrypt);
                            if(!bOK){
                                Logger.w(TAG, "saveDeviceIDToCacheFile failed");
                            }
                        }

                        String idInSdFile = DeviceIDFileUtils.loadFromSDCardFile(pContext);
                        if (!pDeviceID.equalsIgnoreCase(idInSdFile)) {
                            boolean bOK = DeviceIDFileUtils.saveDeviceIDToSdFile(pContext, encrypt);

                            if(!bOK){
                                Logger.w(TAG, "SaveDeviceIDToSdFile failed");
                            } else {
                                Logger.i(TAG, "DeviceIDSPUtils, saveDeviceIDToSdFile success");
                                Logger.d(TAG, "DeviceIDSPUtils, saveDeviceIDToSdFile success:" + pDeviceID);
                            }
                        }

                        return true;
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean pBoolean) {

                    }
                });
    }

    public static void saveDeviceIDToSp(String pDeviceID){
        saveDeviceIDToSp_V3(pDeviceID);
        saveDeviceIDToSp_V2(pDeviceID);
        saveDeviceIDToSp_V1(pDeviceID);
    }


    public static String loadDeviceIDFromSp_V1(){
        Context context = AdhocBasicConfig.getInstance().getAppContext();
        return SharedPreferenceFactory.getInstance().getModel(context).getString("devtoken", null);
    }

    public static void saveDeviceIDToSp_V1(String pDeviceID){
        Context context = AdhocBasicConfig.getInstance().getAppContext();
        SharedPreferenceFactory.getInstance().getModel(context).putString("devtoken", pDeviceID);
    }

    public static String loadDeviceIDFromSp_V2(){
        return DeviceInfoSpConfig.getDeviceToken();
    }

    public static void saveDeviceIDToSp_V2(String pDeviceID){
        DeviceInfoSpConfig.saveDeviceToken(pDeviceID);
    }

    public static String loadDeviceIDFromSp_V3(){
        return DeviceInfoSpConfig.getDeviceID();
    }

    public static void saveDeviceIDToSp_V3(String pDeviceID){
        DeviceInfoSpConfig.saveDeviceID(pDeviceID);
    }

    public static void saveDeviceIDToSpSync_V3(String pDeviceID){
        DeviceInfoSpConfig.saveDeviceIDSync(pDeviceID);
    }

    public static String loadDeviceIDFromSdCard(Context pContext){
        return DeviceIDFileUtils.loadDeviceIDFromSdCard(pContext);
    }

    @NonNull
    public static String generateDeviceID(){
        return "V3"+UUID.randomUUID().toString().replace("-", "");
    }

}
