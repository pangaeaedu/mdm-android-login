package com.nd.android.aioe.device.info.config;


import android.content.Context;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.sp.ISharedPreferenceModel;
import com.nd.android.adhoc.basic.sp.SharedPreferenceFactory;
import com.nd.android.adhoc.basic.util.string.AdhocTextUtil;

import java.util.List;

public class DeviceInfoSpConfig {

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


//    private static final String KEY_DEVICE_STATUS_VALUE = "device_status_value";
    private static final String KEY_DEVICE_ID = "device_id";

    private static final String KEY_PREVIOUS_LOGIN_ACCOUNT = "previous_login_account";

    private static final String KEY_WIFI_MAC_REPORTED = "wifi_mac_reported";
    private static final String KEY_LAN_MAC_REPORTED = "lan_mac_reported";

    private static String mstrPushId = "";

    private static final ISharedPreferenceModel sPreferences;

    static {
        sPreferences = SharedPreferenceFactory.getInstance().getModel(AdhocBasicConfig.getInstance().getAppContext(), "assistant_data", Context.MODE_PRIVATE);
    }

    public DeviceInfoSpConfig getInstant() {
        return DeviceInfoSpConfig.this;
    }


//    public static void saveDeviceStatus(int pStatusValue) {
//        sPreferences.putInt(KEY_DEVICE_STATUS_VALUE, pStatusValue).apply();
//    }

    //默认值为-1
//    public static int getDeviceStatus() {
//        return sPreferences.getInt(KEY_DEVICE_STATUS_VALUE, -1);
//    }

    public static String getDeviceID() {
        return sPreferences.getString(KEY_DEVICE_ID);
    }

    public static void saveDeviceID(String pDeviceID) {
        sPreferences.putString(KEY_DEVICE_ID, pDeviceID).apply();
    }

    public static void saveDeviceIDSync(String pDeviceID) {
        sPreferences.putString(KEY_DEVICE_ID, pDeviceID).commit();
    }

//    public static boolean isActivated() {
//        DeviceStatus status = DeviceStatus.fromValue(getDeviceStatus());
//        return !status.isUnActivated();
//    }

    public static void saveActivated(boolean pActivated) {
        sPreferences.putBoolean(KEY_ACTIVATED, pActivated).apply();
    }

    public static String getDeviceToken() {
        return sPreferences.getString(KEY_DEVICE_TOKEN);
    }

    public static void saveDeviceToken(String pToken) {
        sPreferences.putString(KEY_DEVICE_TOKEN, pToken).apply();
    }

    public static int getOldTokenStatus() {
        return sPreferences.getInt(KEY_OLD_TOKEN_STATUS, 0);
    }

    public static void saveOldTokenStatus(int pStatus) {
        sPreferences.putInt(KEY_OLD_TOKEN_STATUS, pStatus).apply();
    }

    public static String getOldDeviceToken() {
        return sPreferences.getString(KEY_OLD_DEVICE_TOKEN);
    }

    public static void saveOldDeviceToken(String pToken) {
        sPreferences.putString(KEY_OLD_DEVICE_TOKEN, pToken).apply();
    }

    public static String getPushID() {
        return mstrPushId;
    }

    public static void savePushID(String pPushID) {
        mstrPushId = pPushID;
        sPreferences.putString(KEY_PUSH_ID, pPushID).apply();
    }

    public static void savePushIDSync(String pPushID) {
        mstrPushId = pPushID;
        sPreferences.putString(KEY_PUSH_ID, pPushID).commit();
    }

    public static void clearPushID() {
        savePushID("");
    }

    public static void clearPushIDSync() {
        savePushIDSync("");
    }

    public static String getSerialNum() {
        return sPreferences.getString(KEY_SERIAL_NUM);
    }

    public static void saveSerialNum(String pSerialNum) {
        sPreferences.putString(KEY_SERIAL_NUM, pSerialNum).apply();
    }

    public static void saveAutoLogin(boolean pAutoLogin) {
        sPreferences.putBoolean(KEY_AUTO_LOGIN, pAutoLogin).apply();
    }

    public static Boolean isAutoLogin() {
        return sPreferences.getBoolean(KEY_AUTO_LOGIN);
    }

    public static void saveNickname(String pNickname) {
        sPreferences.putString(KEY_NICKNAME, pNickname).apply();
    }

    public static String getNickname() {
        return sPreferences.getString(KEY_NICKNAME);
    }

    public static String getAccountNum() {
        return sPreferences.getString(KEY_ACCOUNT_NUM);
    }

    public static void saveAccountNum(String pAccountNum) {
        sPreferences.putString(KEY_ACCOUNT_NUM, pAccountNum).apply();
    }

    public static void savePolicySetTime(long pTime) {
        sPreferences.putLong(KEY_POLICYSET_TIME, pTime).apply();
    }

    public static long getPolicySetTime() {
        return sPreferences.getLong(KEY_POLICYSET_TIME, 0);
    }

    public static void saveUserID(String pUserID) {
        sPreferences.putString(KEY_USER_ID, pUserID).apply();
    }

    public static String getUserID() {
        return sPreferences.getString(KEY_USER_ID);
    }

    public static void saveDeviceCode(String pDeviceCode) {
        sPreferences.putString(KEY_DEVICE_CODE, pDeviceCode).apply();
    }

    public static String getDeviceCode() {
        return sPreferences.getString(KEY_DEVICE_CODE);
    }

    public static void saveGroupCode(String pGroupCode) {
        sPreferences.putString(KEY_GROUP_CODE, pGroupCode).apply();
    }

    public static String getGroupCode() {
        return sPreferences.getString(KEY_GROUP_CODE);
    }

    public static void saveNodeCode(String pNodeCode) {
        sPreferences.putString(KEY_NODE_CODE, pNodeCode).apply();
    }

    public static String getNodeCode() {
        return sPreferences.getString(KEY_NODE_CODE);
    }

    public static void saveNodeName(String pNodeName) {
        sPreferences.putString(KEY_NODE_NAME, pNodeName).apply();
    }

    public static String getNodeName() {
        return sPreferences.getString(KEY_NODE_NAME);
    }


    public static List<String> getAllPreviousLoginAccount() {
        String accounts = sPreferences.getString(KEY_PREVIOUS_LOGIN_ACCOUNT);

        return AdhocTextUtil.split(accounts, ",");
    }

    public static void addAccountNameToPreviousList(@NonNull String pAccountNum) {
        if (TextUtils.isEmpty(pAccountNum)) {
            return;
        }

        List<String> accounts = getAllPreviousLoginAccount();
        if (accounts.contains(pAccountNum)) {
            return;
        }

        accounts.add(pAccountNum);
        String s = AdhocTextUtil.merge(accounts, ",");
        savePreviousLoginAccount(s);
    }

    private static void savePreviousLoginAccount(String pAccounts) {
        sPreferences.putString(KEY_PREVIOUS_LOGIN_ACCOUNT, pAccounts).apply();
    }

    public static void setWifiMacReported(Boolean pReported) {
        sPreferences.putBoolean(KEY_WIFI_MAC_REPORTED, pReported).apply();
    }

    public static Boolean isWifiMacReported() {
        return sPreferences.getBoolean(KEY_WIFI_MAC_REPORTED);
    }

    public static void setLanMacReported(Boolean pReported) {
        sPreferences.putBoolean(KEY_LAN_MAC_REPORTED, pReported).apply();
    }

    public static Boolean isLanMacReported() {
        return sPreferences.getBoolean(KEY_LAN_MAC_REPORTED);
    }

    // 清理数据的时候，不要清理pushID
    public static void clearData() {
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
//        saveDeviceStatus(-1);

        saveNodeCode("");
        saveNodeName("");

        saveDeviceIDSync("");
        clearPushIDSync();
    }


}
