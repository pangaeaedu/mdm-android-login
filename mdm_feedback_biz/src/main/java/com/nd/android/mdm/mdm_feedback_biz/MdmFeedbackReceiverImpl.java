package com.nd.android.mdm.mdm_feedback_biz;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.communicate.receiver.IFeedbackMsgReceiver;
import com.nd.sdp.android.serviceloader.annotation.Service;

@Service(IFeedbackMsgReceiver.class)
public class MdmFeedbackReceiverImpl implements IFeedbackMsgReceiver {
    @Override
    public void onCmdReceived(@NonNull String pCmdMsg) {
        MdmFeedbackReceiveFactory.doFeedbackReceived(pCmdMsg);
    }
}
