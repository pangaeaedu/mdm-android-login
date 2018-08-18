package com.nd.android.mdm.basic.command.cmd.impl;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.communicate.constant.AdhocCmdFromTo;
import com.nd.android.adhoc.communicate.constant.AdhocCmdType;
import com.nd.android.mdm.basic.command.cmd.ICmdContent_MDM;
import com.nd.android.mdm.biz.common.ErrorCode;
import com.nd.android.mdm.biz.common.MsgCode;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 命令解析器
 * <p>
 * Created by HuangYK on 2018/5/3.
 */
public class MdmCmdHelper {

    private static final String KEY_CMD_NAME = "cmd";
    private static final String KEY_SESSIONID = "sessionid";

    /**
     * 命令解析
     *
     * @param pCmdMsg  命令内容
     * @param pFrom    命令发送方
     * @param pTo      命令结果接收方
     * @param pCmdType 命令类型
     * @return ICmdContent
     */
    @NonNull
    public static ICmdContent_MDM commandParsing(@NonNull String pCmdMsg,
                                                 @NonNull AdhocCmdFromTo pFrom,
                                                 @NonNull AdhocCmdFromTo pTo,
                                                 @NonNull AdhocCmdType pCmdType) throws AdhocException {

        final JSONObject jsonObject;
        final String sessionId;
        try {
            jsonObject = new JSONObject(pCmdMsg);
            sessionId = jsonObject.optString(KEY_SESSIONID, "");
        } catch (JSONException e) {
            throw new AdhocException(ExceptionUtils.getStackTrace(e), ErrorCode.FAILED, MsgCode.ERROR_JSON_INVALID);
        }

        if (TextUtils.isEmpty(sessionId)) {
            throw new AdhocException("sessionId is empty", ErrorCode.FAILED, MsgCode.ERROR_PARAMETER);
        }

        return commandParsing(pCmdMsg, pFrom, pTo, pCmdType, sessionId);
    }

    /**
     * 命令解析
     *
     * @param pCmdMsg    命令内容
     * @param pFrom      命令发送方
     * @param pTo        命令结果接收方
     * @param pCmdType   命令类型
     * @param pSessionId 命令 唯一标识
     * @return ICmdContent
     */
    @NonNull
    public static ICmdContent_MDM commandParsing(@NonNull String pCmdMsg,
                                                 @NonNull AdhocCmdFromTo pFrom,
                                                 @NonNull AdhocCmdFromTo pTo,
                                                 @NonNull AdhocCmdType pCmdType,
                                                 @NonNull String pSessionId) throws AdhocException {
        // from 不允许为未知
        if (AdhocCmdFromTo.MDM_CMD_UNKNOW == pFrom) {
            throw new AdhocException("Command's From is UNKNOW", ErrorCode.FAILED, MsgCode.ERROR_PARAMETER);
        }

        // to 不允许为未知，如果 to 是未知，默认设置成 from
        if (AdhocCmdFromTo.MDM_CMD_UNKNOW == pTo) {
            pTo = pFrom;
        }

        final JSONObject jsonObject;
        final String cmdName;
        final String sessionId;
        try {
            jsonObject = new JSONObject(pCmdMsg);
            cmdName = jsonObject.optString(KEY_CMD_NAME, "").toLowerCase();

            sessionId = !TextUtils.isEmpty(pSessionId) ? pSessionId : jsonObject.optString(KEY_SESSIONID, "");

        } catch (JSONException e) {
            throw new AdhocException(ExceptionUtils.getStackTrace(e), ErrorCode.FAILED, MsgCode.ERROR_JSON_INVALID);
        }

        if (TextUtils.isEmpty(cmdName)) {
            throw new AdhocException("cmdName is empty", ErrorCode.FAILED, MsgCode.ERROR_PARAMETER);
        }

        return new MdmCmdContent(cmdName, jsonObject, sessionId, pFrom.getValue(), pTo.getValue(), pCmdType.getValue());
    }


}
