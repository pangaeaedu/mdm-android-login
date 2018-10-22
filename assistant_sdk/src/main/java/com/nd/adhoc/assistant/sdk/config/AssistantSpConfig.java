package com.nd.adhoc.assistant.sdk.config;


import android.content.Context;
import android.support.annotation.NonNull;

public class AssistantSpConfig extends BaseSpConfig {
    private static final String KEY_ACTIVATED = "activated";

    private static final String KEY_OLD_DEVICE_TOKEN = "old_device_token";
    private static final String KEY_OLD_TOKEN_STATUS = "old_token_status";

    private static final String KEY_DEVICE_TOKEN = "device_token";
    private static final String KEY_PUSH_ID = "push_id";
    private static final String KEY_SERIAL_NUM = "serial_num";

    private static final String KEY_AUTO_LOGIN = "auto_login";
    private static final String KEY_NICKNAME = "nick_name";

    private static final String KEY_ACCOUNT_NUM = "account_num";

    private static final String KEY_POLICYSET_TIME = "policyset_time";

    public AssistantSpConfig(@NonNull Context pContext, @NonNull String pSpName) {
        super(pContext, pSpName);
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

    public int getOldTokenStatus() {
        return getInt(KEY_OLD_TOKEN_STATUS, 0);
    }

    public void saveOldTokenStatus(int pStatus) {
        saveInt(KEY_OLD_TOKEN_STATUS, pStatus);
    }

    public String getOldDeviceToken() {
        return getString(KEY_OLD_DEVICE_TOKEN);
    }

    public void saveOldDeviceToken(String pToken) {
        saveString(KEY_OLD_DEVICE_TOKEN, pToken);
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

    public String getAccountNum(){
        return getString(KEY_ACCOUNT_NUM);
    }

    public void saveAccountNum(String pAccountNum){
        saveString(KEY_ACCOUNT_NUM, pAccountNum);
    }

    public void savePolicySetTime(long pTime){
        saveLong(KEY_POLICYSET_TIME, pTime);
    }

    public long getPolicySetTime(){
        return getLong(KEY_POLICYSET_TIME, 0);
    }

    public void clearData(){
        saveAccountNum("");
        saveNickname("");
        saveAutoLogin(false);
        saveActivated(false);
        savePushID("");
        savePolicySetTime(0);
    }

}
