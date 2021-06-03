package com.nd.android.aioe.device.activate.biz.api.exception;

import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.aioe.device.activate.biz.api.R;

import androidx.annotation.StringRes;

public class AdhocActivateErrorCode {

    public static final int ERROR_CHECK_RESULT_RETRY_TIMES_REACHED = 40001;
    public static final int ERROR_CHECK_RESULT_FAILED_DEFAULT = 40002;

    public static final int ERROR_CHECK_RESULT_ACTIVATINT = 40003;
    public static final int ERROR_CHECK_RESULT_MDM_EXCEPTION = 40004;
    public static final int ERROR_CHECK_RESULT_AUTO_FAILED = 40005;
    public static final int ERROR_CHECK_RESULT_DEVICE_ACTIVATED = 40006;
    public static final int ERROR_CHECK_RESULT_USER_ACTIVATED = 40007;
    public static final int ERROR_CHECK_RESULT_GROUP_NOT_FOUND = 40008;


    @StringRes
    public static int transformCheckResultMsg(String pMsgCode) {
        if (TextUtils.isEmpty(pMsgCode)) {
            return R.string.check_activate_result_error;
        }

        /*
         * 010=激活进行中
         * 020=mdm异常
         * 030=自动激活失效（如果失效表示不再自动登录，输入用户名和密码登录）
         * 040=超过设备运行被激活的次数（目前一台设备只运行同时被激活一次，所以表示该设备已经被用）
         * 050=超过用户允许激活的设备数（目前一个用户只运行同时被注册一次，所以表示该用户已经被用）
         * 060=组不存在
         */
        switch (pMsgCode) {
            case "010":
                return R.string.exception_activate_user_010;
            case "020":
                return R.string.exception_activate_user_020;
            case "030":
                return R.string.exception_activate_user_030;
            case "040":
                return R.string.exception_activate_user_040;
            case "050":
                return R.string.exception_activate_user_050;
            case "060":
                return R.string.exception_activate_user_060;
            default:
                return R.string.check_activate_result_error;
        }

    }

    public static int transformCheckResultCode(String pMsgCode) {
        if (TextUtils.isEmpty(pMsgCode)) {
            return ERROR_CHECK_RESULT_FAILED_DEFAULT;
        }

        /*
         * 010=激活进行中
         * 020=mdm异常
         * 030=自动激活失效（如果失效表示不再自动登录，输入用户名和密码登录）
         * 040=超过设备运行被激活的次数（目前一台设备只运行同时被激活一次，所以表示该设备已经被用）
         * 050=超过用户允许激活的设备数（目前一个用户只运行同时被注册一次，所以表示该用户已经被用）
         * 060=组不存在
         */
        switch (pMsgCode) {
            case "010":
                return ERROR_CHECK_RESULT_ACTIVATINT;
            case "020":
                return ERROR_CHECK_RESULT_MDM_EXCEPTION;
            case "030":
                return ERROR_CHECK_RESULT_AUTO_FAILED;
            case "040":
                return ERROR_CHECK_RESULT_DEVICE_ACTIVATED;
            case "050":
                return ERROR_CHECK_RESULT_USER_ACTIVATED;
            case "060":
                return ERROR_CHECK_RESULT_GROUP_NOT_FOUND;
            default:
                return ERROR_CHECK_RESULT_FAILED_DEFAULT;
        }

    }
}
