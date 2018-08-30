package com.nd.android.mdm.basic.command.response;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceHelper;
import com.nd.android.adhoc.cmd_log.business.operator.impl.CmdLogBizOperatorFactory;
import com.nd.android.adhoc.cmd_log.business.option.CmdLogOptions;
import com.nd.android.adhoc.communicate.impl.MdmTransferFactory;
import com.nd.android.mdm.basic.command.response.strategy.IMdmResponsePost;
import com.nd.android.mdm.basic.command.response.strategy.impl.MdmResponsePost_ToAdhoc;
import com.nd.android.mdm.basic.command.response.strategy.impl.MdmResponsePost_ToDatabase;
import com.nd.android.mdm.basic.command.response.strategy.impl.MdmResponsePost_ToDrms;
import com.nd.android.mdm.biz.common.ErrorCode;
import com.nd.android.mdm.biz.common.MsgCode;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/14.
 */

public abstract class Response implements IResponse_MDM {

    protected int mTo;
    //    protected String cmd;
    protected String mSessionId;
    protected long mTimeStamp;
    protected JSONObject mData;
    protected String other;       // 没有地方使用
    protected String deviceId;    // 没有地方使用
    protected int mCmdType = 1; // 执行情况返回0 指标返回1
    protected int mErrcode;
    protected int mMsgcode;
    protected String mMsg;
    protected String mCmdName;

    protected String mCmdBizName;
    protected long mStartTime;


    private static List<IMdmResponsePost> sPostStrategyList = new ArrayList<IMdmResponsePost>() {
        {
            add(new MdmResponsePost_ToAdhoc());
            add(new MdmResponsePost_ToDatabase());
            add(new MdmResponsePost_ToDrms());
        }
    };

    public Response(@NonNull String cmd, @NonNull String sessionId, int to, long pStartTime) {
        mCmdName = cmd;
        mSessionId = sessionId;
        mTimeStamp = SystemClock.currentThreadTimeMillis() / 1000;
        deviceId = MdmTransferFactory.getPushModel().getDeviceId();
        mTo = to;

        mStartTime = pStartTime;
    }

    public Response(@NonNull String cmd, @NonNull String sessionId, int to, @Nullable String pCmdBizName, long pStartTime) {
        mCmdName = cmd;
        mSessionId = sessionId;
        mTimeStamp = SystemClock.currentThreadTimeMillis() / 1000;
        deviceId = MdmTransferFactory.getPushModel().getDeviceId();
        mTo = to;

        mCmdBizName = pCmdBizName;

        mStartTime = pStartTime;
    }

    @Override
    public IResponse_MDM setErrorCode(int pErrorCode) {
        mErrcode = pErrorCode;
        return this;
    }

    @Override
    public IResponse_MDM setMsgCode(int pMsgCode) {
        mMsgcode = pMsgCode;
        return this;
    }

    @Override
    public IResponse_MDM setCmdType(int pCmdType) {
        mCmdType = pCmdType;
        return this;
    }

    @Override
    public IResponse_MDM setCmdName(String pCmdName) {
        mCmdName = pCmdName;
        return this;
    }

    @Override
    public IResponse_MDM setMsg(String pMsg) {
        mMsg = pMsg;
        return this;
    }

    @Override
    public IResponse_MDM setJsonData(JSONObject pJsonData) {
        mData = pJsonData;
        return this;
    }

    @Override
    public IResponse_MDM setCmdBizName(String pCmdBizeName) {
        mCmdBizName = pCmdBizeName;
        return this;
    }

    @Override
    public IResponse_MDM setStartTime(long startTime) {
        mStartTime = startTime;
        return this;
    }

    @Override
    public int getErrorCode() {
        return mErrcode;
    }

    @Override
    public int getMsgCode() {
        return mMsgcode;
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
    public String getMsg() {
        return mMsg;
    }

    @Override
    public String getCmdBizName() {
        return !TextUtils.isEmpty(mCmdBizName) ? mCmdBizName : mCmdName;
    }

    @Override
    public JSONObject getJsonData() {
        return mData;
    }

    @Override
    public int getTo() {
        return mTo;
    }

    @Override
    public long getStartTime() {
        return mStartTime;
    }

    @Override
    public void post() {
        postAsync();
        saveCmdLog();
    }

    protected void saveCmdLog(){
        CmdLogOptions options =
                new CmdLogOptions.Builder(mSessionId,
                        !TextUtils.isEmpty(mCmdBizName) ? mCmdBizName : mCmdName)
                        .errorCode(mErrcode)
                        .msgCode(mMsgcode)
                        .message(mMsg)
                        .startTime(mStartTime)
                        .build();
        CmdLogBizOperatorFactory.getWriteOperator().saveCmdLog(options);
    }

    public void postAsync() {
        for (IMdmResponsePost responsePost : sPostStrategyList) {
            if (responsePost.getPostTo().getValue() == mTo) {
                responsePost.post(this);
                return;
            }
        }
    }

//    private void postToAdhco() {
//        new MessageEvent(this.toString()).post();
//    }

//    private void postToDrms() {
////        new HttpPostEvent(getPostUrl(), this.toString()).post();
//        MdmTransferFactory.getCommunicationModule().doHttpPost(getPostUrl(), this.toString());
//    }

//    private void postToDatabase() {
//        new ResponseCacheEvent(this).post();
//    }

    protected abstract JSONObject build() throws JSONException;

//    public abstract String getPostUrl();

    @Override
    public String toString() {
        try {
            mData = build();
        } catch (JSONException e) {
            mErrcode = ErrorCode.FAILED;
            mMsgcode = MsgCode.ERROR_JSON_INVALID;
            mMsg = ExceptionUtils.getStackTrace(e);
        }
        try {
            JSONObject json = new JSONObject();
            json.put("cmd", mCmdName);
            json.put("data", mData);
            json.put("timestamp", mTimeStamp);
            json.put("other", other);
            json.put("type", mCmdType);
            // TODO:  getDevToken getUserToken 待替换为最新的登录 API 接口
            json.put("device_token", DeviceHelper.getDeviceToken());
            json.put("user_token", DeviceHelper.getDeviceToken());
            json.put("sessionid", mSessionId);
            json.put("errcode", mErrcode);
            json.put("msgcode", mMsgcode);
            json.put("msg", mMsg);
            return json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
