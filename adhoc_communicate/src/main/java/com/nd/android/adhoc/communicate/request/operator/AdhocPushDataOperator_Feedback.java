package com.nd.android.adhoc.communicate.request.operator;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.communicate.receiver.IPushDataOperator;
import com.nd.sdp.android.serviceloader.annotation.Service;

/**
 * Created by HuangYK on 2019/6/17.
 */

@Service(IPushDataOperator.class)
public class AdhocPushDataOperator_Feedback implements IPushDataOperator {

    @Override
    public boolean isPushMsgTypeMatche(int pPushMsgType) {
        return pPushMsgType == 1;
    }

    @Override
    public void onPushDataArrived(@NonNull String pData) {
        AdhocPushRequestOperator.receiveFeedback(pData);
    }
}
