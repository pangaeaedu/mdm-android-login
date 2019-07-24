package com.nd.android.adhoc.communicate.policy;

import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.communicate.impl.MdmTransferConfig;
import com.nd.android.adhoc.communicate.request.constant.AdhocNetworkChannel;
import com.nd.android.adhoc.policy.api.AdhocPolicyTaskAbs;
import com.nd.sdp.android.serviceloader.annotation.Service;

import org.json.JSONObject;

/**
 * Created by HuangYK on 2019/7/2.
 */
@Service(AdhocPolicyTaskAbs.class)
public class AdhocPolicyTask_PushUpstream extends AdhocPolicyTaskAbs {

    private static final String TAG = "AdhocPolicyTask_PushUpstream";

    @Override
    public int getPolicyType() {
        return 2;
    }

    @Override
    public String getPolicyName() {
        return "push_upstream";
    }


    @Override
    public void updateTask(String pPolicyData) {
        try {
            JSONObject jsonObject = new JSONObject(pPolicyData);

            // 1 = push上行，0 = http/https，默认 1
            int enable = jsonObject.optInt("enable", 1);
            long timeout = jsonObject.optLong("timeout", MdmTransferConfig.getRequestTimeout());

            MdmTransferConfig.setNetworkChannel(AdhocNetworkChannel.getTypeByValue(enable));
            MdmTransferConfig.setRequestTimeout(timeout);

        } catch (Exception e) {
            Logger.w(TAG, "runTask error: " + e);
            return;
        }

        super.updateTask(pPolicyData);
    }

    @Override
    public void stop() {
        MdmTransferConfig.setNetworkChannel(AdhocNetworkChannel.CHANNEL_PUSH);
        MdmTransferConfig.setRequestTimeout(0);
        super.stop();
    }
}
