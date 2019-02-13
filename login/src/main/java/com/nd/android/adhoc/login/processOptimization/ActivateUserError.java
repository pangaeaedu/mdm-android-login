package com.nd.android.adhoc.login.processOptimization;

public enum ActivateUserError {
    Processing(""),
    UserVerifyFailed("001"),
    UserBinded("002"),
    DeviceBinded("003"),
    OtherError("004");


    private String mValue = "";

    ActivateUserError(String pValue){
        mValue = pValue;
    }

    public String getValue(){
        return mValue;
    }
}
