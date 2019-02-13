package com.nd.android.adhoc.loginapi.exception;

public class BaseInitException extends Exception {
    protected int mCode = 0;

    public BaseInitException(String pMsg){
        super(pMsg);
    }

    public BaseInitException(int pCode, String pMsg){
        this(pMsg);
        mCode = pCode;
    }


    public int getCode(){
        return mCode;
    }
}
