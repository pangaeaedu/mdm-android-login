package com.nd.android.mdm.basic.command.response;


import android.support.annotation.NonNull;


import com.nd.android.mdm.biz.env.MdmEvnFactory;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/4/26.
 */

public class ResponseBase extends Response {

    public ResponseBase(String cmd, String sessionId, int to, long pStartTime) {
        this(cmd, sessionId, to, "",pStartTime);
    }

    public ResponseBase(String pCmd, String pSessionId, int pTo, String pCmdBizName, long pStartTime) {
        super(pCmd, pSessionId, pTo, pCmdBizName,pStartTime);
    }


    @Override
    protected JSONObject build() throws JSONException {
        return getJsonData();
    }

    @NonNull
    @Override
    public String getPostUrl() {
        return MdmEvnFactory.getInstance().getCurEnvironment().getUrl() + "/v1/device/cmdresult/";
    }

}
