package com.nd.android.aioe.device.status.biz.api.cache;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.sp.ISharedPreferenceModel;
import com.nd.android.adhoc.basic.sp.SharedPreferenceFactory;
import com.nd.android.aioe.device.status.biz.api.constant.DeviceStatus;

import java.util.concurrent.atomic.AtomicBoolean;

public class DeviceStatusCache {

    private static final String TAG = "DeviceStatusCache";

    private static final String KEY_DEVICE_STATUS_VALUE = "device_status_value";
    private static final String KEY_DEVICE_IS_DELETED = "device_is_deleted";

    private static final String KEY_LAST_UPDATE_TIME = "device_status_update_time";

    // 内存缓存
    private static DeviceStatus sCurDeviceStatus;

    private static final ISharedPreferenceModel sPreferences;

    private static final AtomicBoolean sAllowCheckStatus = new AtomicBoolean(false);

    static {
        sPreferences = SharedPreferenceFactory.getInstance().getModel(AdhocBasicConfig.getInstance().getAppContext(), "assistant_data", Context.MODE_PRIVATE);
    }

    public static DeviceStatus getDeviceStatus() {
        synchronized (TAG) {
            if (sCurDeviceStatus == null) {
                sCurDeviceStatus = DeviceStatus.fromValue(getDeviceStatusFromSp());
                sCurDeviceStatus.setIsDeleted(getDeviceIsDeletedFromSp());
            }

            return sCurDeviceStatus;
        }
    }


    public static void setDeviceStatus(@NonNull DeviceStatus pDeviceStatus) {
        synchronized (TAG){
            sCurDeviceStatus = pDeviceStatus;
            saveDeviceStatusToSp(sCurDeviceStatus.getValue());
            saveDeviceIsDeletedToSp(sCurDeviceStatus.isDeleted());
            refreshDeviceStatusUpdateTime();
        }
    }

    public static long getLastUpdateTime(){
        return sPreferences.getLong(KEY_LAST_UPDATE_TIME, 0L);
    }

    public static void setAllowCheckStatus(boolean isAllowCheckStatus){
        sAllowCheckStatus.set(isAllowCheckStatus);
    }

    public static boolean isAllowCheckStatus(){
        return sAllowCheckStatus.get();
    }

    private static void saveDeviceStatusToSp(int pStatusValue) {
        sPreferences.putInt(KEY_DEVICE_STATUS_VALUE, pStatusValue).commit();
    }

    private static int getDeviceStatusFromSp() {
        return sPreferences.getInt(KEY_DEVICE_STATUS_VALUE, DeviceStatus.Init.getValue());
    }
    private static void saveDeviceIsDeletedToSp(boolean pIsDeleted) {
        sPreferences.putBoolean(KEY_DEVICE_IS_DELETED, pIsDeleted).commit();
    }

    private static boolean getDeviceIsDeletedFromSp() {
        return sPreferences.getBoolean(KEY_DEVICE_IS_DELETED);
    }

    private static void refreshDeviceStatusUpdateTime() {
        sPreferences.putLong(KEY_LAST_UPDATE_TIME, System.currentTimeMillis()).commit();
    }



}
