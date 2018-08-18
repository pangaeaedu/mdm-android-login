package com.nd.android.mdm.basic.command.receiver;

import android.support.annotation.NonNull;
import android.support.v4.util.ArraySet;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.common.util.AdhocDataCheckUtils;
import com.nd.android.adhoc.basic.util.thread.AdhocRxJavaUtil;
import com.nd.android.adhoc.communicate.constant.AdhocCmdFromTo;
import com.nd.android.adhoc.communicate.constant.AdhocCmdType;
import com.nd.android.mdm.basic.command.operator.ICmdOperator;
import com.nd.android.mdm.basic.command.response.MdmResponseHelper;
import com.nd.android.mdm.basic.command.utils.MdmCmdUtil;
import com.nd.android.mdm.biz.common.ErrorCode;
import com.nd.android.mdm.biz.common.MsgCode;
import com.nd.android.mdm.basic.command.cmd.ICmdContent_MDM;
import com.nd.android.mdm.basic.command.cmd.impl.MdmCmdHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by HuangYK on 2018/8/15.
 */
public final class MdmCmdReceiveFactory {

    private static Set<ICmdOperator> sCmdMsgOperatorSet = new ArraySet<>();

    public static void addCmdOperator(@NonNull ICmdOperator pCmdOperator) {
        sCmdMsgOperatorSet.add(pCmdOperator);
    }

    static void doCmdReceived(@NonNull final String pCmdMsg, @NonNull final AdhocCmdFromTo pFrom, @NonNull final AdhocCmdFromTo pTo) {
        if (AdhocDataCheckUtils.isCollectionEmpty(sCmdMsgOperatorSet)) {
            return;
        }

        if (AdhocCmdFromTo.MDM_CMD_UNKNOW == pFrom) {
            return;
        }

        AdhocRxJavaUtil.safeSubscribe(
                Observable.create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        ICmdContent_MDM cmdContent = null;
                        try {
                            cmdContent = MdmCmdHelper.commandParsing(pCmdMsg, pFrom, pTo, AdhocCmdType.CMD_TYPE_STATUS);

                            List<Boolean> operateResults = new ArrayList<>();
                            for (ICmdOperator operator : sCmdMsgOperatorSet) {
                                operateResults.add(operator.operate(cmdContent));
                            }

                            if (!operateResults.contains(true)) {
                                MdmResponseHelper.createResponseBase(cmdContent.getCmdName(), cmdContent.getCmdName(), cmdContent.getSessionId(), AdhocCmdFromTo.MDM_CMD_ADHOC.getValue(), System.currentTimeMillis())
                                        .setErrorCode(ErrorCode.UNUSABLE)
                                        .setMsgCode(MsgCode.ERROR_COMMAND_UNSUPPORT)
                                        .setMsg(MdmCmdUtil.formatFailMsg(AdhocBasicConfig.getInstance().getAppContext(), cmdContent.getCmdName(), MsgCode.ERROR_COMMAND_UNSUPPORT, " not support ."))
                                        .setCmdName(cmdContent.getCmdName())
                                        .post();
                            }
                        } catch (AdhocException e) {
                            if (cmdContent != null && !TextUtils.isEmpty(cmdContent.getCmdName()) && !TextUtils.isEmpty(cmdContent.getSessionId())) {
                                MdmResponseHelper.createResponseBase(cmdContent.getCmdName(), "", cmdContent.getSessionId(), pTo.getValue(), System.currentTimeMillis())
                                        .setErrorCode(e.getErrorCode())
                                        .setMsgCode(e.getMsgCode())
                                        .setMsg(e.getMessage())
                                        .post();
                            }
                        }

                    }
                }).subscribeOn(Schedulers.io()));
    }
}
