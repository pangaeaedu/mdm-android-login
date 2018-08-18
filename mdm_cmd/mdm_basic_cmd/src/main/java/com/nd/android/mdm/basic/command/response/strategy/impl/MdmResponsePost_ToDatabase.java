package com.nd.android.mdm.basic.command.response.strategy.impl;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.nd.android.adhoc.cmd_log.business.operator.impl.CmdLogBizOperatorFactory;
import com.nd.android.adhoc.cmd_log.business.option.CmdLogOptions;
import com.nd.android.adhoc.communicate.constant.AdhocCmdFromTo;
import com.nd.android.mdm.basic.command.response.IResponse_MDM;
import com.nd.android.mdm.basic.command.response.strategy.IMdmResponsePost;


/**
 * Created by HuangYK on 2018/5/1.
 */

public class MdmResponsePost_ToDatabase implements IMdmResponsePost {


    @NonNull
    @Override
    public AdhocCmdFromTo getPostTo() {
        return AdhocCmdFromTo.MDM_CMD_DATABASE;
    }

    @Override
    public void post(@NonNull IResponse_MDM pResponse) {
        CmdLogOptions options =
                new CmdLogOptions.Builder(pResponse.getSessionId(),
                        !TextUtils.isEmpty(pResponse.getCmdBizName()) ? pResponse.getCmdBizName() : pResponse.getCmdName())
                        .errorCode(pResponse.getErrorCode())
                        .msgCode(pResponse.getMsgCode())
                        .message(pResponse.getMsg())
                        .startTime(pResponse.getStartTime())
                        .build();
        CmdLogBizOperatorFactory.getWriteOperator().saveCmdLog(options);
//        new ResponseCacheEvent(pResponse).post();
    }
}
