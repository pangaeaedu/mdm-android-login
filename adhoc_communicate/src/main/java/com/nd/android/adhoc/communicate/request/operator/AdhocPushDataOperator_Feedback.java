package com.nd.android.adhoc.communicate.request.operator;

import android.support.annotation.NonNull;
import android.util.Log;

import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.communicate.receiver.IPushDataOperator;
import com.nd.sdp.android.serviceloader.annotation.Service;

import org.json.JSONException;
import org.json.JSONObject;

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

        String message_id = "";
        try {
            JSONObject object = new JSONObject(pData);
            message_id = object.optString("message_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Logger.d("yhq_push", "PushDataOperator_Feedback onPushDataArrived, message_id = " + message_id);
        AdhocPushRequestOperator.receiveFeedback(pData);
    }
}
