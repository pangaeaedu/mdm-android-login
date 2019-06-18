package com.nd.android.adhoc.communicate.request.constant;

import android.support.annotation.NonNull;

/**
 * Created by HuangYK on 2019/6/4.
 */

public enum AdhocNetworkChannel {

    CHANNEL_HTTP(0),
    CHANNEL_PUSH(1);

    private int mValue;

    AdhocNetworkChannel(int pValue) {
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
    public static AdhocNetworkChannel getTypeByValue(int pValue) {
        AdhocNetworkChannel[] array = AdhocNetworkChannel.values();
        for (AdhocNetworkChannel flag : array) {
            if (flag.mValue == pValue) {
                return flag;
            }
        }
        return CHANNEL_HTTP;
    }
}
