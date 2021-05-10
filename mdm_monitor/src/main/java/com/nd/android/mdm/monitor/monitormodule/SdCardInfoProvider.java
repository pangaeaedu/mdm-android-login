package com.nd.android.mdm.monitor.monitormodule;

import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.mdm.monitor.info.AdhocSDCardInfo;
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
public class SdCardInfoProvider {
    private static final String TAG = "SdCardInfoProvider";
    private static final long CACHE_INFO_VALID_PERIOD = 60 * 1000;
    private long mlLastUpdateTime;
    private AdhocSDCardInfo mSDCardInfo;

    public AdhocSDCardInfo getSDCardInfo(boolean bForceUpdate){
        if(bForceUpdate || null == mSDCardInfo
                || System.currentTimeMillis() - mlLastUpdateTime > CACHE_INFO_VALID_PERIOD){
            UpdateSDCardInfo();
            return mSDCardInfo;
        }else {
            return mSDCardInfo;
        }
    }

    private void UpdateSDCardInfo(){
        Logger.i(TAG, "update sdcard info");
        mSDCardInfo = new AdhocSDCardInfo(MonitorUtil.getSdcardInfo());
        mlLastUpdateTime = System.currentTimeMillis();
    }
}
