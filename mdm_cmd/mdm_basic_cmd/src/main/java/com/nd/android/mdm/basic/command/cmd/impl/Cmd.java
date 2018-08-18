package com.nd.android.mdm.basic.command.cmd.impl;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.nd.android.mdm.basic.command.R;
import com.nd.android.mdm.basic.command.cmd.ICmd_MDM;
import com.nd.android.mdm.basic.command.response.IResponse_MDM;
import com.nd.android.mdm.basic.command.response.MdmResponseHelper;
import com.nd.android.mdm.biz.common.ErrorCode;
import com.nd.android.mdm.biz.common.MsgCode;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Cmd implements ICmd_MDM {

    protected Context mContext;

    private JSONObject mCmdJson;

    // 指令的名称
    private String mCmdName = "CMD_NOT_EXIST";

    // 指令业务展示的 名称
    private String mCmdBizName;


    private int mFrom;
    private int mTo;
    private int mCmdType;
    private String mSessionId = "SESSION_ID_NOT_EXIST";

    private int mErrorCode = ErrorCode.UNKNOWN;
    private int mMsgCode = MsgCode.ERROR_NONE;
    private long mStartTime;
    private String mMsg;

    public Cmd(Context context, JSONObject pCmdJson) throws JSONException {
        mContext = context;
        mCmdJson = pCmdJson;
        mStartTime = System.currentTimeMillis();
    }

    Cmd setFrom(int pFrom) {
        mFrom = pFrom;
        return this;
    }

    Cmd setTo(int pTo) {
        mTo = pTo;
        return this;
    }

    Cmd setCmdType(int pCmdType) {
        mCmdType = pCmdType;
        return this;
    }

    Cmd setSessionId(@Nullable String sessionId) {
        this.mSessionId = TextUtils.isEmpty(sessionId) ? mSessionId : sessionId;
        return this;
    }

    Cmd setCmdName(String pCmdName) {
        mCmdName = pCmdName;
        return this;
    }

    @Override
    public JSONObject getCmdJson() {
        return mCmdJson;
    }

    @Override
    public int getTo() {
        return mTo;
    }

    @Override
    public int getFrom() {
        return mFrom;
    }

    @Override
    public int getCmdType() {
        return mCmdType;
    }

    @Override
    public String getSessionId() {
        return mSessionId;
    }

    @Override
    public String getCmdName() {
        return mCmdName;
    }


    @Override
    public ICmd_MDM setErrorCode(int errorCode) {
        this.mErrorCode = errorCode;
        return this;
    }
    @Override
    public ICmd_MDM setMsgCode(int pMsgCode) {
        mMsgCode = pMsgCode;
        return this;
    }

    @Override
    public ICmd_MDM setMsg(String pMsg) {
        mMsg = pMsg;
        return this;
    }

    @Override
    public int getErrorCode() {
        return mErrorCode;
    }

    @Override
    public String getMsg() {
        return mMsg == null || mMsg.isEmpty() ? mContext.getString(R.string.cmd_response_null) : mMsg;
    }

    @Override
    public int getMsgCode() {
        return mMsgCode;
    }

    @Override
    public String getCmdBizName() {
        return TextUtils.isEmpty(mCmdBizName) ? mCmdName : mCmdBizName;
    }

    @Override
    public long getStartTime() {
        return mStartTime;
    }

    protected ICmd_MDM setCmdBizName(@StringRes int pCmdBizNameRes) {
        mCmdBizName = mContext.getString(pCmdBizNameRes);
        return this;
    }

    //    public abstract void execute() throws Exception;
//
//    public abstract Response response();

//    protected Response getDefaultResponse() {
//        Response response = new ResponseBase(mCmdName, mSessionId, mFrom);
//        response.setErrorCode(ErrorCode.EXECUTING);
//        return response;
//    }

    protected IResponse_MDM getExecutingResponse() {
        return MdmResponseHelper.createResponseBase(mCmdName, mCmdBizName, mSessionId, mFrom, mStartTime)
                .setErrorCode(ErrorCode.EXECUTING)
                .setMsgCode(MsgCode.EXECUTING);
    }

    @Override
    public IResponse_MDM response() {
        return MdmResponseHelper.createResponseBase(mCmdName, mCmdBizName, mSessionId, mFrom, mStartTime)
                .setErrorCode(mErrorCode)
                .setMsgCode(mMsgCode)
                .setMsg(mMsg);
    }

//    public String getName() {
//        return mCmdBizName;
//    }
//
//    public void setName(String name) {
//        this.mCmdBizName = name;
//    }

    @Override
    public void release() {

    }
}
