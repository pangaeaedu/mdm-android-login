package com.nd.android.mdm.basic.command.response.strategy;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.communicate.constant.AdhocCmdFromTo;
import com.nd.android.mdm.basic.command.response.IResponse_MDM;


/**
 * 信息回报执行策略
 * <p>
 * Created by HuangYK on 2018/5/1.
 */
public interface IMdmResponsePost {

    @NonNull
    AdhocCmdFromTo getPostTo();

    void post(@NonNull IResponse_MDM pResponse);
}
