package com.nd.android.mdm.basic.command.response;


/**
 * response 辅助类
 * <p>
 * Created by HuangYK on 2018/5/3.
 */
public class MdmResponseHelper {

    public static IResponse_MDM createResponseBase(String pCmd, String pCmdBizName, String pSessionId, int pTo, long pStartTime) {
        return new ResponseBase(pCmd, pSessionId, pTo, pCmdBizName,pStartTime);
    }
}
