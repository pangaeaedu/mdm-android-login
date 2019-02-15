package com.nd.android.adhoc.login.enumConst;

public enum ActivateUserError {
    Processing("0010"),
    AutoLoginFailed("0030");

    private String mValue = "";

    ActivateUserError(String pValue){
        mValue = pValue;
    }

    public String getValue(){
        return mValue;
    }
}
