package com.nd.android.aioe.device.info.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.aioe.device.info.config.DeviceInfoSpConfig;
import com.nd.android.aioe.device.status.biz.api.DeviceStatus;

import rx.subjects.BehaviorSubject;

public class DeviceInfoManager {

    private static final DeviceInfoManager ourInstance = new DeviceInfoManager();

    public static DeviceInfoManager getInstance() {
        return ourInstance;
    }

    private DeviceInfoManager() {
    }

//    private UserLoginConfig mUserLoginConfig = null;

    private String mDeviceID = "";
    private DeviceStatus mDeviceStatus = null;
    private int mNeedQueryStatusFromServer = 0;///0：初始化状态　1：需要查询服务端状态　2：已获取服务端状态

    private BehaviorSubject<String> mPushIDSubject = BehaviorSubject.create();

    private BehaviorSubject<String> mConfirmDeviceIDSubject = BehaviorSubject.create();

//    public void setUserLoginConfig(UserLoginConfig pLoginConfig){
//        mUserLoginConfig = pLoginConfig;
//    }

//    @NonNull
//    public UserLoginConfig getUserLoginConfig(){
//        return mUserLoginConfig;
//    }

    public void setDeviceID(@NonNull String pDeviceID) {
        mDeviceID = pDeviceID;
        notifyDeviceID(mDeviceID);
    }

    public String getDeviceID() {
        return mDeviceID;
    }

    public DeviceStatus getCurrentStatus() {
        if (mDeviceStatus == null) {
            int status = DeviceInfoSpConfig.getDeviceStatus();
            mDeviceStatus = DeviceStatus.fromValue(status);
        }

        return mDeviceStatus;
    }

    public void setCurrentStatus(DeviceStatus pStatus) {
        Logger.e("yhq", "setCurrentStatus:"+pStatus.getValue());
        mDeviceStatus = pStatus;
        mNeedQueryStatusFromServer = 2;
        DeviceInfoSpConfig.saveDeviceStatus(pStatus.getValue());
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

    public int getNeedQueryStatusFromServer() {
        return mNeedQueryStatusFromServer;
    }

    public void setNeedQueryStatusFromServer(int needQueryStatusFromServer) {
        if(needQueryStatusFromServer >mNeedQueryStatusFromServer){
            mNeedQueryStatusFromServer = needQueryStatusFromServer;
        }

    }
}
