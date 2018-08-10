package com.nd.android.adhoc.login.enumConst;

public enum DeviceType {
    Android(1),
    Ios(2),
    Pc(3);

    private int mValue = 1;

    DeviceType(int pValue){
        mValue = pValue;
    }

    public int getValue(){
        return mValue;
    }
}
