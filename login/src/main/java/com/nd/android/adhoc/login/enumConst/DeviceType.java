package com.nd.android.adhoc.login.enumConst;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;

public enum DeviceType {
    Android(1),
    Ios(2),
    Pc(3),
    AndroidRobot(5002);

    private int mValue = 1;

    DeviceType(int pValue){
        mValue = pValue;
    }

    public int getValue(){
        return mValue;
    }

    /**
     * getTypeByString
     * 根据字符串获取枚举值
     *
     * @param pValue value
     * @return DeviceType
     */
    @NonNull
    static DeviceType getDeviceTypeByValue(int pValue) {
        DeviceType[] array = DeviceType.values();
        for (DeviceType flag : array) {
            if (flag.mValue == pValue) {
                return flag;
            }
        }
        return Android;
    }


    private static final String KEY_DEVICE_TYPE = "ADHOC_DEVICE_TYPE";

    private static DeviceType sDeviceType;

    public static DeviceType getDeviceType() {
        if (sDeviceType == null) {
            try {
                Context context = AdhocBasicConfig.getInstance().getAppContext();
                ApplicationInfo appInfo = context.getPackageManager()
                        .getApplicationInfo(context.getPackageName(),
                                PackageManager.GET_META_DATA);

                if (!appInfo.metaData.containsKey(KEY_DEVICE_TYPE)) {
                    return DeviceType.Android;
                }

                int value = appInfo.metaData.getInt(KEY_DEVICE_TYPE);

                sDeviceType = DeviceType.getDeviceTypeByValue(value);

            } catch (Exception e) {
                sDeviceType = DeviceType.Android;
            }
        }

        return sDeviceType;
    }
}
