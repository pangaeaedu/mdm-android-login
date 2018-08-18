package com.nd.android.mdm.basic.command.cmd;


import com.nd.android.adhoc.basic.command.cmd.ICmd;
import com.nd.android.mdm.basic.command.response.IResponse_MDM;

import org.json.JSONObject;

/**
 * 默认的业务接口
 * <p>
 * Created by HuangYK on 2018/4/30.
 */
public interface ICmd_MDM extends ICmd<IResponse_MDM> {

//    ICmd_Build setFrom(int pFrom);
//
//    ICmd_Build setTo(int pTo);
//
//    ICmd_Build setCmdType(int pCmdType);
//
//    ICmd_Build setSessionId(String pSessionId);
//
//    ICmd_Build setCmdName(String pCmdName);

    String getCmdName();

    String getCmdBizName();

    int getFrom();

    int getTo();

    int getCmdType();

    String getSessionId();

    JSONObject getCmdJson();



    ICmd_MDM setErrorCode(int pErrorCode);

    ICmd_MDM setMsgCode(int pMsgCode);

    ICmd_MDM setMsg(String pMsg);

    int getErrorCode();

    int getMsgCode();

    String getMsg();

    long getStartTime();



}
