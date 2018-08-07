package com.nd.android.adhoc.communicate.constant;

/**
 * Created by HuangYK on 2018/8/6.
 */

public enum AdhocCmdType {


    //从profile中读取的
    CMD_TYPE_PROFILE_MODULE(0),

    //标准指令---需要显示的
    CMD_TYPE_STATUS(1);

    private int mValue;

    AdhocCmdType(int pValue) {
        mValue = pValue;
    }

    public int getValue() {
        return mValue;
    }
}
