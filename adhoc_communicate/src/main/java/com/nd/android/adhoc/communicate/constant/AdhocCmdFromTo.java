package com.nd.android.adhoc.communicate.constant;

import android.support.annotation.NonNull;

/**
 * Created by HuangYK on 2018/8/6.
 */

public enum AdhocCmdFromTo {

    MDM_CMD_ADHOC(1),
    MDM_CMD_DRM(2),
    MDM_CMD_DATABASE(3),


    MDM_CMD_UNKNOW(-1);


    private int mValue;

    AdhocCmdFromTo(int pValue) {
        mValue = pValue;
    }

    public int getValue() {
        return mValue;
    }

    /**
     * getTypeByValue
     * 根据字符串获取枚举值
     *
     * @param pValue 名称
     * @return MdmCmdFromTo
     */
    @NonNull
    public static AdhocCmdFromTo getTypeByValue(int pValue) {
        AdhocCmdFromTo[] array = AdhocCmdFromTo.values();
        for (AdhocCmdFromTo flag : array) {
            if (flag.mValue == pValue) {
                return flag;
            }
        }
        return MDM_CMD_UNKNOW;
    }
}
