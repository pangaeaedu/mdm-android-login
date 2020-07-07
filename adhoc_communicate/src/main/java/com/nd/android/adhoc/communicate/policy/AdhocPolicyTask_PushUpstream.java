package com.nd.android.adhoc.communicate.policy;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.communicate.impl.MdmTransferConfig;
import com.nd.android.adhoc.communicate.request.constant.AdhocNetworkChannel;
import com.nd.android.adhoc.policy.api.AdhocPolicyTaskAbs;
import com.nd.android.adhoc.policy.api.IAdhocPolicyEntity;
import com.nd.android.adhoc.policy.api.constant.AdhocPolicyErrorCode;
import com.nd.android.adhoc.policy.api.constant.AdhocPolicyException;
import com.nd.android.adhoc.policy.api.constant.AdhocPolicyMsgCode;
import com.nd.sdp.android.serviceloader.annotation.Service;

import org.json.JSONException;
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
    public AdhocPolicyErrorCode executeTask(@NonNull IAdhocPolicyEntity pPolicyEntity) throws AdhocException {
        if (TextUtils.isEmpty(pPolicyEntity.getData())) {
            throw new AdhocPolicyException("updateTask failed: pPolicyData is empty", AdhocPolicyMsgCode.ERROR_POLICY_DATA_IS_EMPTY);
        }

        try {
            JSONObject jsonObject = new JSONObject(pPolicyEntity.getData());

            // 1 = push上行，0 = http/https，默认 1
            int enable = jsonObject.optInt("enable", 1);
            long timeout = jsonObject.optLong("timeout", MdmTransferConfig.getRequestTimeout());

            MdmTransferConfig.setNetworkChannel(AdhocNetworkChannel.getTypeByValue(enable));
            MdmTransferConfig.setRequestTimeout(timeout);

        } catch (JSONException e) {
            throw new AdhocPolicyException("updateTask error: " + e, AdhocPolicyMsgCode.ERROR_JSON_PARSING_FAILED);
        } catch (Exception e) {
            throw new AdhocPolicyException("updateTask error: " + e, AdhocPolicyMsgCode.ERROR_UNKNOW);
        }

        return super.executeTask(pPolicyEntity);
    }

    @Override
    public AdhocPolicyErrorCode stop(@Nullable IAdhocPolicyEntity pNewPolicyEntity) throws AdhocException {
        try {
            MdmTransferConfig.setNetworkChannel(AdhocNetworkChannel.CHANNEL_PUSH);
            MdmTransferConfig.setRequestTimeout(0);
        } catch (Exception e) {
            throw new AdhocPolicyException("stop error: " + e, AdhocPolicyMsgCode.ERROR_UNKNOW);
        }
        return super.stop(pNewPolicyEntity);
    }
}
