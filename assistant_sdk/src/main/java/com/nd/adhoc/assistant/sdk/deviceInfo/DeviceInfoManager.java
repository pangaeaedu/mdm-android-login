package com.nd.adhoc.assistant.sdk.deviceInfo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.nd.adhoc.assistant.sdk.AssistantBasicServiceFactory;
import com.nd.adhoc.assistant.sdk.config.AssistantSpConfig;
import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.log.Logger;

import rx.subjects.BehaviorSubject;

public class DeviceInfoManager {

    private static final DeviceInfoManager ourInstance = new DeviceInfoManager();

    public static DeviceInfoManager getInstance() {
        return ourInstance;
    }

    private DeviceInfoManager() {
    }

    private UserLoginConfig mUserLoginConfig = null;

    private String mDeviceID = "";
    private DeviceStatus mDeviceStatus = null;

    private BehaviorSubject<String> mPushIDSubject = BehaviorSubject.create();

    private BehaviorSubject<String> mConfirmDeviceIDSubject = BehaviorSubject.create();

    public void setUserLoginConfig(UserLoginConfig pLoginConfig){
        mUserLoginConfig = pLoginConfig;
    }

    @NonNull
    public UserLoginConfig getUserLoginConfig(){
        return mUserLoginConfig;
    }

    public void setDeviceID(@NonNull String pDeviceID) {
        mDeviceID = pDeviceID;
        notifyDeviceID(mDeviceID);
    }

    public String getDeviceID() {
        return mDeviceID;
    }

    public DeviceStatus getCurrentStatus() {
        if (mDeviceStatus == null) {
            int status = getConfig().getDeviceStatus();
            mDeviceStatus = DeviceStatus.fromValue(status);
        }

        return mDeviceStatus;
    }

    public void setCurrentStatus(DeviceStatus pStatus) {
        Logger.e("yhq", "setCurrentStatus:"+pStatus.getValue());
        mDeviceStatus = pStatus;
        getConfig().saveDeviceStatus(pStatus.getValue());
    }

    public BehaviorSubject<String> getPushIDSubject() {
        return mPushIDSubject;
    }

    public void notifyPushID(String pPushID) {
        mPushIDSubject.onNext(pPushID);
    }

    public BehaviorSubject<String> getConfirmDeviceIDSubject(){
        return mConfirmDeviceIDSubject;
    }

    public void notifyDeviceID(String pDeviceID) {
        mConfirmDeviceIDSubject.onNext(pDeviceID);
    }


    private static AssistantSpConfig getConfig() {
        return AssistantBasicServiceFactory.getInstance().getSpConfig();
    }

    public void reset(){
        clearDeviceID();

        mConfirmDeviceIDSubject.onCompleted();
        mConfirmDeviceIDSubject = BehaviorSubject.create();

        resetStatusAndPushIDSubject();
    }

    private void clearDeviceID(){
        Logger.i("yhq", "clearDeviceID");
        mDeviceID = "";
        Context context = AdhocBasicConfig.getInstance().getAppContext();
        DeviceIDSPUtils.saveDeviceIDToSp("");
        DeviceIDFileUtils.saveDeviceIDToSdFile(context, "");
        DeviceIDFileUtils.saveDeviceIDToCacheFile(context, "");
    }


    public void resetStatusAndPushIDSubject(){
        mDeviceStatus = null;
        mPushIDSubject.onCompleted();
        mPushIDSubject = BehaviorSubject.create();
    }
}
