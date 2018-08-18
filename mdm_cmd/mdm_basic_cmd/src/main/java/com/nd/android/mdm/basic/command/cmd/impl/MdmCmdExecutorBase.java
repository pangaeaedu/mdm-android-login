package com.nd.android.mdm.basic.command.cmd.impl;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.communicate.constant.AdhocCmdFromTo;
import com.nd.android.mdm.basic.command.cmd.ICmd_MDM;
import com.nd.android.mdm.biz.common.ErrorCode;
import com.nd.android.mdm.biz.common.MsgCode;
import com.nd.android.adhoc.basic.command.cmd.ICmdExecutor;
import com.nd.android.mdm.basic.command.response.IResponse_MDM;
import com.nd.android.mdm.basic.command.response.MdmResponseHelper;
import com.nd.android.mdm.basic.command.utils.MdmCmdUtil;

/**
 * MDM 命令 执行器
 * <p>
 * Created by HuangYK on 2018/5/2.
 */
public abstract class MdmCmdExecutorBase implements ICmdExecutor<ICmd_MDM> {

    @Override
    public void executeCmd(@NonNull ICmd_MDM pCmd) throws AdhocException {
        if (!isCmdSupport(pCmd.getCmdName())) {
            MdmResponseHelper.createResponseBase(pCmd.getCmdName(), pCmd.getCmdBizName(), pCmd.getSessionId(), AdhocCmdFromTo.MDM_CMD_ADHOC.getValue(), pCmd.getStartTime())
                    .setErrorCode(ErrorCode.UNUSABLE)
                    .setMsgCode(MsgCode.ERROR_COMMAND_UNSUPPORT)
                    .setMsg(MdmCmdUtil.formatFailMsg(AdhocBasicConfig.getInstance().getAppContext(), pCmd.getCmdName(), MsgCode.ERROR_COMMAND_UNSUPPORT, " not support ."))
                    .setCmdName(pCmd.getCmdName())
                    .setCmdBizName(pCmd.getCmdBizName())
                    .post();
            return;
        }

        pCmd.execute();
        IResponse_MDM response = pCmd.response();
        if (response != null) {
            if (pCmd.getErrorCode() == ErrorCode.SUCCESS) {
                response.setMsg(MdmCmdUtil.formatSuccessMsg(AdhocBasicConfig.getInstance().getAppContext(), pCmd.getCmdName(), pCmd.getErrorCode()));
            } else {
                response.setMsg(MdmCmdUtil.formatFailMsg(AdhocBasicConfig.getInstance().getAppContext(), pCmd.getCmdName(), pCmd.getErrorCode(), pCmd.getMsg()));
            }
            response.setCmdName(pCmd.getCmdName()).setCmdBizName(pCmd.getCmdBizName());
            response.post();
        }
    }

    protected abstract boolean isCmdSupport(String pCmdName);
}
