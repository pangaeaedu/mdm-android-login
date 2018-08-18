package com.nd.android.mdm.basic.command.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nd.android.mdm.basic.command.R;
import com.nd.android.mdm.biz.common.ErrorCode;

/**
 * MDM 命令工具类
 * <p>
 * Created by HuangYK on 2018/5/4.
 */
public final class MdmCmdUtil {

    @NonNull
    public static String getResult(@NonNull Context pContext, int pErrorCode) {

        if (pErrorCode == ErrorCode.SUCCESS) {
            return pContext.getString(R.string.cmd_success);
        }

        if (pErrorCode == ErrorCode.FAILED) {
            return pContext.getString(R.string.cmd_failed);
        }

        if (pErrorCode == ErrorCode.EXECUTING) {
            return pContext.getString(R.string.cmd_executing);
        }

        if (pErrorCode == ErrorCode.UNUSABLE) {
            return pContext.getString(R.string.cmd_unusable);
        }

        return pContext.getString(R.string.cmd_unknown);
    }


    private static String sReplayFormatSuccess;
    private static String sReplyFormatFailed;

    public static String formatFailMsg(@NonNull Context pContext, String pCmdName, int pErrorCode, String pMsg) {
        if (sReplyFormatFailed == null) {
            sReplyFormatFailed = pContext.getString(R.string.cmd_log_reply_failed_style);
        }
        return String.format(sReplyFormatFailed, pCmdName, getResult(pContext, pErrorCode), pMsg);
    }

    public static String formatSuccessMsg(@NonNull Context pContext, String pCmdName, int pErrorCode) {
        if (sReplayFormatSuccess == null) {
            sReplayFormatSuccess = pContext.getString(R.string.cmd_log_reply_success_style);
        }
        return String.format(sReplayFormatSuccess, pCmdName, getResult(pContext, pErrorCode));
    }
}
