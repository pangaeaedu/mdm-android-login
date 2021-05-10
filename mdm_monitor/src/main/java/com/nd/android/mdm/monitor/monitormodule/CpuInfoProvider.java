package com.nd.android.mdm.monitor.monitormodule;

import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.control.define.IControl_CpuUsageRate;
import com.nd.android.mdm.basic.ControlFactory;
import com.nd.android.mdm.monitor.info.AdhocCpuInfo;

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
public class CpuInfoProvider {
    private static final String TAG = "CpuInfoProvider";
    private static final long CACHE_INFO_VALID_PERIOD = 10 * 1000;
    private long mlLastUpdateTime;
    private AdhocCpuInfo mCpuInfo;

    public AdhocCpuInfo getCpuInfo(boolean bForceUpdate){
        if(bForceUpdate || null == mCpuInfo
                || System.currentTimeMillis() - mlLastUpdateTime > CACHE_INFO_VALID_PERIOD){
            UpdateCpuInfo();
            return mCpuInfo;
        }else {
            return mCpuInfo;
        }
    }

    private void UpdateCpuInfo(){
        Logger.i(TAG, "update cpu info");
        IControl_CpuUsageRate control_cpuUsageRate = ControlFactory.getInstance().getControl(IControl_CpuUsageRate.class);
        if (control_cpuUsageRate != null) {
            mCpuInfo = new AdhocCpuInfo((int)control_cpuUsageRate.getUsageRate());
        }else {
            mCpuInfo = new AdhocCpuInfo(0);
        }

        mlLastUpdateTime = System.currentTimeMillis();
    }
}
