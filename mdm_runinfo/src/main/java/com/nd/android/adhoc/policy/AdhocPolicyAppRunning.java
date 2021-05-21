package com.nd.android.adhoc.policy;

import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nd.android.adhoc.RunningAppWatchManager;
import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.util.app.AdhocPackageUtil;
import com.nd.android.adhoc.policy.api.AdhocPolicyTaskAbs;
import com.nd.android.adhoc.policy.api.IAdhocPolicyEntity;
import com.nd.android.adhoc.policy.api.constant.AdhocPolicyErrorCode;
import com.nd.android.adhoc.policy.api.constant.AdhocPolicyException;
import com.nd.android.adhoc.policy.api.constant.AdhocPolicyMsgCode;
import com.nd.android.mdm.appusage.AdhocAppUsageFactory;
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
            JSONObject jsonObject = new JSONObject(pPolicyEntity.getRealData());

            // 1 = 开启，0 = 关闭，默认 1
            int enable = jsonObject.optInt("enable", 1);

            // 装了 SystemService，走旧的逻辑
            // 如果 SDK < 23，也走旧的逻辑
            if (isCountSelf()) {
                if (1 == enable) {
                    RunningAppWatchManager.getInstance().init();
                } else {
                    RunningAppWatchManager.getInstance().stopWatching();
                }
                return super.executeTask(pPolicyEntity);
            }

            // 走新的逻辑
            if (1 == enable) {
                AdhocAppUsageFactory.start();
            } else {
                AdhocAppUsageFactory.cancel();
            }

            return super.executeTask(pPolicyEntity);
        } catch (Exception e) {
            Logger.w(TAG, "executeTask error: " + e);
            throw new AdhocPolicyException("executeTask error: " + e, AdhocPolicyMsgCode.ERROR_UNKNOW);
        }
    }

    @Override
    public AdhocPolicyErrorCode stop(@Nullable IAdhocPolicyEntity pNewPolicyEntity) throws AdhocException {
        try{
            if(isCountSelf()){
                RunningAppWatchManager.getInstance().stopWatching();
            }else {
                AdhocAppUsageFactory.cancel();
            }
        }catch (Exception e){
            throw new AdhocPolicyException("stop error: " + e, AdhocPolicyMsgCode.ERROR_UNKNOW);
        }
        return super.stop(pNewPolicyEntity);
    }

    /**
     * MDM自己统计
     * @return
     */
    private boolean isCountSelf(){
        return  AdhocPackageUtil.checkPackageInstalled("com.nd.adhoc.systemservice")
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
    }
}
