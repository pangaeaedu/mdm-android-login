package com.nd.android.mdm.basic.command.cmd.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.communicate.constant.AdhocCmdFromTo;
import com.nd.android.mdm.basic.command.R;
import com.nd.android.mdm.basic.command.cmd.ICmdContent_MDM;
import com.nd.android.mdm.basic.command.cmd.ICmd_MDM;
import com.nd.android.mdm.biz.common.ErrorCode;
import com.nd.android.mdm.biz.common.MsgCode;
import com.nd.android.adhoc.basic.command.cmd.ICmdCreator;
import com.nd.android.mdm.basic.command.response.IResponse_MDM;
import com.nd.android.mdm.basic.command.response.MdmResponseHelper;
import com.nd.android.mdm.biz.common.util.SDKLogUtil;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Mdm 命令 构造器
 * <p>
 * Created by HuangYK on 2018/5/2.
 */

public abstract class MdmCmdCreatorBase implements ICmdCreator<ICmd_MDM, ICmdContent_MDM> {

    private String mFormat;


    @Override
    public ICmd_MDM createCmd(@NonNull ICmdContent_MDM pCmdContent) throws AdhocException {
        Context context = AdhocBasicConfig.getInstance().getAppContext();

        JSONObject json = pCmdContent.getCmdJson();
        try {
            String cmdName = pCmdContent.getCmdName();

            SDKLogUtil.d("factory create:" + cmdName);
            Class<? extends ICmd_MDM> cmdClass = getCmdClass(cmdName);

            if (cmdClass != null) {
                Constructor<?> constructor = cmdClass.getConstructor(Context.class, JSONObject.class);
                Cmd instance = (Cmd) constructor.newInstance(context, json);

                instance.setCmdName(cmdName)
                        .setTo(pCmdContent.getTo())
                        .setFrom(pCmdContent.getFrom())
                        .setCmdType(pCmdContent.getCmdType())
                        .setSessionId(pCmdContent.getSessionId());

                SDKLogUtil.d("create class:" + cmdClass.getCanonicalName() + "'s instance success");
                if (mFormat == null) {
                    mFormat = context.getString(R.string.cmd_log_create_style);
                }
//                new LogEvent(0, "", String.format(mFormat, instance.getCmdName()), 1, instance.getSessionId(), instance.getCmdBizName(), instance.getErrorCode(), instance.getCmdType()).post();
                return instance;

            } else {
                SDKLogUtil.e("make cmd failed,can not find class");
            }
        } catch (InstantiationException e) {
            doException(pCmdContent, e);
        } catch (NoSuchMethodException e) {
            doException(pCmdContent, e);
        } catch (IllegalAccessException e) {
            doException(pCmdContent, e);
        } catch (InvocationTargetException e) {
            doException(pCmdContent, e);
        }
        return null;
    }

    private void doException(ICmdContent_MDM pCmdContent, Throwable e) throws AdhocException {
        IResponse_MDM response = MdmResponseHelper.createResponseBase(
                pCmdContent.getCmdName(),
                "",
                pCmdContent.getSessionId(),
                AdhocCmdFromTo.MDM_CMD_ADHOC.getValue(),
                System.currentTimeMillis()
        );
        response.setErrorCode(ErrorCode.UNUSABLE)
                .setMsgCode(MsgCode.ERROR_JSON_INVALID)
                .setMsg(ExceptionUtils.getStackTrace(e));
        response.post();

        throw new AdhocException("Create Cmd Error: " + ExceptionUtils.getStackTrace(e), ErrorCode.FAILED, MsgCode.ERROR_UNKNOWN);

    }


    protected abstract Class<? extends ICmd_MDM> getCmdClass(@NonNull String pCmdName);
}
