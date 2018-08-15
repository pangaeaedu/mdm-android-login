package com.nd.android.adhoc.communicate.receiver;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.communicate.constant.AdhocCmdFromTo;


/**
 * 指令接收者，由 各业务实现后，向 通信层 注入
 * <p>
 * Created by HuangYK on 2018/4/29.
 */
public interface ICmdMsgReceiver {

    /**
     * Cmd 指令接收之后的处理
     * @param pCmdMsg cmd 指令信息
     */
    void onCmdReceived(@NonNull String pCmdMsg, @NonNull AdhocCmdFromTo pFrom, @NonNull AdhocCmdFromTo pTo);
}
