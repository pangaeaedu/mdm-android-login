package com.nd.android.mdm.monitor.monitormodule;

import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.mdm.monitor.info.AdhocMemoryInfo;
import com.nd.eci.sdk.utils.MonitorUtil;

/**Administrator
 * @author
 * @name adhoc-101-assistant-app
 * @class name：com.nd.android.mdm.monitor.monitormodule
 * @class describe
 * @time 2021/4/27 19:47
 * @change
 * @chang time
 * @class describe
 */
public class MemoryInfoProvider {
    private static final String TAG = "MemoryInfoProvider";
    private static final long CACHE_INFO_VALID_PERIOD = 10 * 1000;
    private long mlLastUpdateTime;
    private AdhocMemoryInfo mMemoryInfo;

    public AdhocMemoryInfo getMemoryInfo(boolean bForceUpdate){
        if(bForceUpdate || null == mMemoryInfo
                || System.currentTimeMillis() - mlLastUpdateTime > CACHE_INFO_VALID_PERIOD){
            UpdateMemoryInfo();
            return mMemoryInfo;
        }else {
            return mMemoryInfo;
        }
    }

    private void UpdateMemoryInfo(){
        Logger.i(TAG, "update memory info");
        mMemoryInfo = new AdhocMemoryInfo(MonitorUtil.getMemoryInfo());
        mlLastUpdateTime = System.currentTimeMillis();
    }
}
