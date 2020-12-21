package com.nd.adhoc.assistant.sdk.config;


import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;
import com.nd.adhoc.assistant.sdk.utils.StringUtils;

import java.util.List;

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

    private static final String KEY_USER_ID = "user_id";

    // 设备编码，根据OMO 需求新增的一个字段
    private static final String KEY_DEVICE_CODE = "device_code";

    // 根据 OMO 增加对 groupcode 字段的解析，用于后期查询父节点级当前节点的名称等信息 -- by hyk 20200511
    private static final String KEY_GROUP_CODE = "group_code";

    private static final String KEY_NODE_CODE = "node_code";
    private static final String KEY_NODE_NAME = "node_name";


    private static final String KEY_DEVICE_STATUS_VALUE = "device_status_value";
    private static final String KEY_DEVICE_ID = "device_id";

    private static final String KEY_PREVIOUS_LOGIN_ACCOUNT = "previous_login_account";

    private static final String KEY_WIFI_MAC_REPORTED = "wifi_mac_reported";
    private static final String KEY_LAN_MAC_REPORTED = "lan_mac_reported";

    public AssistantSpConfig(@NonNull Context pContext, @NonNull String pSpName) {
        super(pContext, pSpName);
    }

    public void saveDeviceStatus(int pStatusValue){
        saveInt(KEY_DEVICE_STATUS_VALUE, pStatusValue);
    }

    //默认值为-1
    public int getDeviceStatus(){
        return getInt(KEY_DEVICE_STATUS_VALUE, -1);
    }

    public String getDeviceID() {
        return getString(KEY_DEVICE_ID);
    }

    public void saveDeviceID(String pDeviceID) {
        saveString(KEY_DEVICE_ID, pDeviceID);
    }

    public void saveDeviceIDSync(String pDeviceID) {
        saveStringSync(KEY_DEVICE_ID, pDeviceID);
    }

    public boolean isActivated() {
        DeviceStatus status = DeviceStatus.fromValue(getDeviceStatus());
        if(DeviceStatus.isStatusUnLogin(status)){
            return false;
        }

        return true;
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

    public void savePushIDSync(String pPushID) {
        saveStringSync(KEY_PUSH_ID, pPushID);
    }

    public void clearPushID(){
        savePushID("");
    }

    public void clearPushIDSync(){
        savePushIDSync("");
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

    public void saveUserID(String pUserID) {
        saveString(KEY_USER_ID, pUserID);
    }

    public String getUserID() {
        return getString(KEY_USER_ID);
    }

    public void saveDeviceCode(String pDeviceCode) {
        saveString(KEY_DEVICE_CODE, pDeviceCode);
    }

    public String getDeviceCode() {
        return getString(KEY_DEVICE_CODE);
    }

    public void saveGroupCode(String pGroupCode) {
        saveString(KEY_GROUP_CODE, pGroupCode);
    }

    public String getGroupCode() {
        return getString(KEY_GROUP_CODE);
    }

    public void saveNodeCode(String pNodeCode) {
        saveString(KEY_NODE_CODE, pNodeCode);
    }

    public String getNodeCode() {
        return getString(KEY_NODE_CODE);
    }

    public void saveNodeName(String pNodeName) {
        saveString(KEY_NODE_NAME, pNodeName);
    }

    public String getNodeName() {
        return getString(KEY_NODE_NAME);
    }



    public List<String> getAllPreviousLoginAccount(){
        String accounts = getString(KEY_PREVIOUS_LOGIN_ACCOUNT);

        return StringUtils.splitString(accounts, ",");
    }

    public void addAccountNameToPreviousList(@NonNull String pAccountNum) {
        if (TextUtils.isEmpty(pAccountNum)) {
            return;
        }

        List<String> accounts = getAllPreviousLoginAccount();
        if (accounts.contains(pAccountNum)) {
            return;
        }

        accounts.add(pAccountNum);
        String s = StringUtils.mergeStringList(accounts, ",");
        savePreviousLoginAccount(s);
    }

    private void savePreviousLoginAccount(String pAccounts){
        saveString(KEY_PREVIOUS_LOGIN_ACCOUNT,  pAccounts);
    }

    public void setWifiMacReported(Boolean pReported){
        saveBoolean(KEY_WIFI_MAC_REPORTED, pReported);
    }

    public Boolean isWifiMacReported(){
        return getBoolean(KEY_WIFI_MAC_REPORTED);
    }

    public void setLanMacReported(Boolean pReported){
        saveBoolean(KEY_LAN_MAC_REPORTED, pReported);
    }

    public Boolean isLanMacReported(){
        return getBoolean(KEY_LAN_MAC_REPORTED);
    }

    // 清理数据的时候，不要清理pushID
    public void clearData(){
        saveAccountNum("");
        saveNickname("");
        saveAutoLogin(false);
        saveActivated(false);
        savePolicySetTime(0);
        saveOldTokenStatus(0);
        saveOldDeviceToken("");
        // 清理数据的时候，不要清掉DeviceID。因为这三个值不会变的
//        saveDeviceToken("");
//        saveDeviceID("");
//        saveSerialNum("");
        saveUserID("");
        saveDeviceCode("");
        saveGroupCode("");
        saveDeviceStatus(-1);

        saveNodeCode("");
        saveNodeName("");
    }



}
