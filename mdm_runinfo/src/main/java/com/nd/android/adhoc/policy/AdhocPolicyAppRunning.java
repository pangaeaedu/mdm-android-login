package com.nd.android.adhoc.policy;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.RunningAppWatchManager;
import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.policy.api.AdhocPolicyTaskAbs;
import com.nd.android.adhoc.policy.api.IAdhocPolicyEntity;
import com.nd.android.adhoc.policy.api.constant.AdhocPolicyErrorCode;
import com.nd.android.adhoc.policy.api.constant.AdhocPolicyException;
import com.nd.android.adhoc.policy.api.constant.AdhocPolicyMsgCode;
import com.nd.sdp.android.serviceloader.annotation.Service;

import org.json.JSONObject;

/**
 * Created by linsj on 2019/7/16.
 */
@Service(AdhocPolicyTaskAbs.class)
public class AdhocPolicyAppRunning extends AdhocPolicyTaskAbs {

    private static final String TAG = "AdhocPolicyAppRunning";

    @Override
    public int getPolicyType() {
        return 2;
    }

    @Override
    public String getPolicyName() {
        return "appusage";
    }

    @Override
    public AdhocPolicyErrorCode executeTask(@NonNull IAdhocPolicyEntity pPolicyEntity)  throws AdhocException{
        try {
            Logger.i(TAG, "executeTask");
            JSONObject jsonObject = new JSONObject(pPolicyEntity.getData());

            // 1 = push上行，0 = http/https，默认 1
            int enable = jsonObject.optInt("enable", 1);

            if(1 == enable){
                RunningAppWatchManager.getInstance().init();
            }else {
                RunningAppWatchManager.getInstance().stopWatching();
            }
            return super.executeTask(pPolicyEntity);
        } catch (Exception e) {
            Logger.w(TAG, "executeTask error: " + e);
            throw new AdhocPolicyException("executeTask error: " + e, AdhocPolicyMsgCode.ERROR_UNKNOW);
        }
    }

    @Override
    public AdhocPolicyErrorCode stop() throws AdhocException {
        try{
            RunningAppWatchManager.getInstance().stopWatching();
        }catch (Exception e){
            throw new AdhocPolicyException("stop error: " + e, AdhocPolicyMsgCode.ERROR_UNKNOW);
        }
        return super.stop();
    }
}
