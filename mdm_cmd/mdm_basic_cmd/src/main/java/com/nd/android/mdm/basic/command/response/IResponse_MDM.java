package com.nd.android.mdm.basic.command.response;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.command.response.IResponse;

import org.json.JSONObject;

/**
 * Created by HuangYK on 2018/5/2.
 */

public interface IResponse_MDM extends IResponse {

    IResponse_MDM setErrorCode(int pErrorCode);

    IResponse_MDM setMsgCode(int pMsgCode);

    IResponse_MDM setCmdType(int pCmdType);

    IResponse_MDM setCmdName(String pCmdName);

    IResponse_MDM setMsg(String pMsg);

    IResponse_MDM setJsonData(JSONObject pJsonData);

    IResponse_MDM setCmdBizName(String pCmdBizeName);

    IResponse_MDM setStartTime(long pStartTime);


    int getErrorCode();

    int getMsgCode();

    int getCmdType();

    String getCmdName();

    String getSessionId();

    String getMsg();

    @NonNull
    String getPostUrl();

    String getCmdBizName();

    JSONObject getJsonData();

    int getTo();

    long getStartTime();

}
