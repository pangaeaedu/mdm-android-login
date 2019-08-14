package com.nd.android.adhoc.policy;

import com.nd.android.adhoc.RunningAppWatchManager;
import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.policy.api.AdhocPolicyTaskAbs;
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
    public void updateTask(String pPolicyData) throws AdhocException{
        try {
            JSONObject jsonObject = new JSONObject(pPolicyData);

            // 1 = push上行，0 = http/https，默认 1
            int enable = jsonObject.optInt("enable", 1);

            if(1 == enable){
                RunningAppWatchManager.getInstance().init();
            }else {
                RunningAppWatchManager.getInstance().stopWatching();
            }
            super.updateTask(pPolicyData);
        } catch (Exception e) {
            Logger.w(TAG, "runTask error: " + e);
        }
    }

    @Override
    public void stop() throws AdhocException {
        try{
            RunningAppWatchManager.getInstance().stopWatching();
        }catch (Exception e){

        }
        super.stop();
    }
}
