package com.nd.android.adhoc.login.enumConst;


public enum ActivateErrorCode {
    Success(0),
    Failed(1);

    private int mValue = 0;

    ActivateErrorCode(int pValue){
        mValue = pValue;
    }

    public int getValue(){
        return mValue;
    }
}
