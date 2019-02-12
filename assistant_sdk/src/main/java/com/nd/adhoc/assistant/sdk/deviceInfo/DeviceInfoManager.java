package com.nd.adhoc.assistant.sdk.deviceInfo;

import android.support.annotation.NonNull;

import com.nd.adhoc.assistant.sdk.AssistantBasicServiceFactory;
import com.nd.adhoc.assistant.sdk.config.AssistantSpConfig;

import rx.subjects.BehaviorSubject;

public class DeviceInfoManager {

    private static final DeviceInfoManager ourInstance = new DeviceInfoManager();

    public static DeviceInfoManager getInstance() {
        return ourInstance;
    }

    private DeviceInfoManager() {
    }

    private String mDeviceID = "";
    private DeviceStatus mDeviceStatus = null;

    private BehaviorSubject<String> mPushIDSubject = BehaviorSubject.create();

    public void setDeviceID(@NonNull String pDeviceID) {
        mDeviceID = pDeviceID;
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
        mDeviceStatus = pStatus;
        getConfig().saveDeviceStatus(pStatus.getValue());
    }

    public BehaviorSubject<String> getPushIDSubject() {
        return mPushIDSubject;
    }

    public void notifyPushID(String pPushID) {
        mPushIDSubject.onNext(pPushID);
    }

    private static AssistantSpConfig getConfig() {
        return AssistantBasicServiceFactory.getInstance().getSpConfig();
    }
}
