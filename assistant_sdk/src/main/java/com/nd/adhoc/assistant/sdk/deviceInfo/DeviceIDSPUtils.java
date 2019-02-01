package com.nd.adhoc.assistant.sdk.deviceInfo;

import android.Manifest;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.druid.util.Base64;
import com.nd.adhoc.assistant.sdk.AssistantBasicServiceFactory;
import com.nd.adhoc.assistant.sdk.config.AssistantSpConfig;
import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.sp.SharedPreferenceFactory;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.UUID;

import rx.Observer;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class DeviceIDSPUtils {

    private static final String TAG = "DeviceIDSPUtils";

    public static void startNewThreadToCheckDeviceIDIntegrity(final Context pContext,
                                                              final String pDeviceID) {
        RxPermissions.getInstance(pContext)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean pBoolean) {
                        String encrypt = Base64.byteArrayToBase64(pDeviceID.getBytes());

                        String idInCache = DeviceIDFileUtils.loadFromCacheFile(pContext);
                        if (!pDeviceID.equalsIgnoreCase(idInCache)) {
                           boolean bOK = DeviceIDFileUtils.saveDeviceIDToCacheFile(pContext, encrypt);
                            if(!bOK){
                                Log.e(TAG, "saveDeviceIDToCacheFile failed");
                            }
                        }

                        String idInSdFile = DeviceIDFileUtils.loadFromSDCardFile(pContext);
                        if (!pDeviceID.equalsIgnoreCase(idInSdFile)) {
                            boolean bOK = DeviceIDFileUtils.saveDeviceIDToSdFile(pContext, encrypt);
                            if(!bOK){
                                Log.e(TAG, "SaveDeviceIDToSdFile failed");
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

    public static String loadDeviceIDFromSp(){
        String deviceID = loadThirdVersionDeviceIDFromSp();
        if(!TextUtils.isEmpty(deviceID)){
            return deviceID;
        }

        deviceID = loadSecondVersionDeviceIDFromSp();
        if(!TextUtils.isEmpty(deviceID)){
            return deviceID;
        }

        deviceID = loadFirstVersionDeviceIDFromSp();
        if(!TextUtils.isEmpty(deviceID)){
            return deviceID;
        }

        return "";
    }

    public static void saveDeviceIDToSp(String pDeviceID){
        saveDeviceIDToThirdVersionSp(pDeviceID);
        saveDeviceIDToSecondVersionSp(pDeviceID);
        saveDeviceIDToFirstVersionSp(pDeviceID);
    }


    private static String loadFirstVersionDeviceIDFromSp(){
        Context context = AdhocBasicConfig.getInstance().getAppContext();
        return SharedPreferenceFactory.getInstance().getModel(context).getString("devtoken", null);
    }

    private static void saveDeviceIDToFirstVersionSp(String pDeviceID){
        Context context = AdhocBasicConfig.getInstance().getAppContext();
        SharedPreferenceFactory.getInstance().getModel(context).putString("devtoken", pDeviceID);
    }

    private static String loadSecondVersionDeviceIDFromSp(){
        AssistantSpConfig config = AssistantBasicServiceFactory.getInstance().getSpConfig();
        return config.getDeviceToken();
    }

    private static void saveDeviceIDToSecondVersionSp(String pDeviceID){
        AssistantSpConfig config = AssistantBasicServiceFactory.getInstance().getSpConfig();
        config.saveDeviceToken(pDeviceID);
    }

    private static String loadThirdVersionDeviceIDFromSp(){
        AssistantSpConfig config = AssistantBasicServiceFactory.getInstance().getSpConfig();
        return config.getDeviceID();
    }

    private static void saveDeviceIDToThirdVersionSp(String pDeviceID){
        AssistantSpConfig config = AssistantBasicServiceFactory.getInstance().getSpConfig();
        config.saveDeviceID(pDeviceID);
    }

    public static String loadDeviceIDFromSdCard(Context pContext){
        return DeviceIDFileUtils.loadDeviceIDFromSdCard(pContext);
    }

    public static String generateDeviceID(){
        return UUID.randomUUID().toString().replace("-", "");
    }

}
