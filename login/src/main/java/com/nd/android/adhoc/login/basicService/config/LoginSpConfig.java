package com.nd.android.adhoc.login.basicService.config;


import android.support.annotation.NonNull;

public class LoginSpConfig extends BaseSpConfig {
    private static final String KEY_ACTIVATED = "activated";
    private static final String KEY_DEVICE_TOKEN = "device_token";

    public LoginSpConfig(@NonNull String pSpName) {
        super(pSpName);
    }

    public boolean isActivated(){
        return getBoolean(KEY_ACTIVATED);
    }

    public void saveActivated(boolean pActivated){
        saveBoolean(KEY_ACTIVATED, pActivated);
    }

    public String getDeviceToken(){
        return getString(KEY_DEVICE_TOKEN);
    }

    public void saveDeviceToken(String pToken){
        saveString(KEY_DEVICE_TOKEN, pToken);
    }

}
