package com.nd.android.adhoc.login.basicService.config;


import android.support.annotation.NonNull;

public class LoginSpConfig extends BaseSpConfig {
    private static final String KEY_ACTIVATED = "activated";
    private static final String KEY_DEVICE_TOKEN = "device_token";
    private static final String KEY_PUSH_ID = "push_id";
    private static final String KEY_SERIAL_NUM = "serial_num";

    private static final String KEY_AUTO_LOGIN = "auto_login";
    private static final String KEY_NICKNAME = "nick_name";

    public LoginSpConfig(@NonNull String pSpName) {
        super(pSpName);
    }

    public boolean isActivated() {
        return getBoolean(KEY_ACTIVATED);
    }

    public void saveActivated(boolean pActivated) {
        saveBoolean(KEY_ACTIVATED, pActivated);
    }

    public String getDeviceToken() {
        return getString(KEY_DEVICE_TOKEN);
    }

    public void saveDeviceToken(String pToken) {
        saveString(KEY_DEVICE_TOKEN, pToken);
    }

    public String getPushID() {
        return getString(KEY_PUSH_ID);
    }

    public void savePushID(String pPushID) {
        saveString(KEY_PUSH_ID, pPushID);
    }

    public String getSerialNum() {
        return getString(KEY_SERIAL_NUM);
    }

    public void saveSerialNum(String pSerialNum) {
        saveString(KEY_SERIAL_NUM, pSerialNum);
    }

    public void saveAutoLogin(boolean pAutoLogin) {
        saveBoolean(KEY_AUTO_LOGIN, pAutoLogin);
    }

    public Boolean isAutoLogin() {
        return getBoolean(KEY_AUTO_LOGIN);
    }

    public void saveNickname(String pNickname) {
        saveString(KEY_NICKNAME, pNickname);
    }

    public String getNickname() {
        return getString(KEY_NICKNAME);
    }

    public void clearData(){
        saveNickname("");
        saveAutoLogin(false);
        saveActivated(false);
        savePushID("");
    }
}
