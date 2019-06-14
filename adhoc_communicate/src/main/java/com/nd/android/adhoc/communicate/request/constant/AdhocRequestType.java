package com.nd.android.adhoc.communicate.request.constant;

import android.support.annotation.NonNull;

/**
 * Created by HuangYK on 2019/6/4.
 */

public enum AdhocRequestType {

    ADHOC_REQUEST_HTTP(1),
    ADHOC_REQUEST_PUSH(2);

    private int mValue;

    AdhocRequestType(int pValue) {
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
    public static AdhocRequestType getTypeByValue(int pValue) {
        AdhocRequestType[] array = AdhocRequestType.values();
        for (AdhocRequestType flag : array) {
            if (flag.mValue == pValue) {
                return flag;
            }
        }
        return ADHOC_REQUEST_HTTP;
    }
}
