package com.nd.android.adhoc.loginapi.exception;

import android.content.Context;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;

public class BaseInitException extends Exception {
    protected int mCode = 0;

    public BaseInitException(String pMsg){
        super(pMsg);
    }

    public BaseInitException(int pCode, String pMsg){
        this(pMsg);
        mCode = pCode;
    }

    protected Context getContext(){
        return AdhocBasicConfig.getInstance().getAppContext();
    }

    public int getCode(){
        return mCode;
    }
}
