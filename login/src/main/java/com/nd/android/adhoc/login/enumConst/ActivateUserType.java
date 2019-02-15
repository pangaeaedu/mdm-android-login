package com.nd.android.adhoc.login.enumConst;

public enum ActivateUserType {
    Uc("uc"),
    AutoLogin("autologin");

    private String mValue = "";

    ActivateUserType(String pValue){
        mValue = pValue;
    }

    public String getValue(){
        return mValue;
    }
}
