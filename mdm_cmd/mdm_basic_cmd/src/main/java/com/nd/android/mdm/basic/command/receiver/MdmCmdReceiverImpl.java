package com.nd.android.mdm.basic.command.receiver;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.communicate.constant.AdhocCmdFromTo;
import com.nd.android.adhoc.communicate.receiver.ICmdMsgReceiver;
import com.nd.sdp.android.serviceloader.annotation.Service;

/**
 * Created by HuangYK on 2018/8/15.
 */
@Service(ICmdMsgReceiver.class)
public class MdmCmdReceiverImpl implements ICmdMsgReceiver {

    @Override
    public void onCmdReceived(@NonNull String pCmdMsg, @NonNull AdhocCmdFromTo pFrom, @NonNull AdhocCmdFromTo pTo) {
        MdmCmdReceiveFactory.doCmdReceived(pCmdMsg, pFrom, pTo);
    }
}
