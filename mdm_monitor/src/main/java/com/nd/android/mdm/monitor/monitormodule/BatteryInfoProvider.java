package com.nd.android.mdm.monitor.monitormodule;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.cloudatlas.utils.SystemInfoUtil;

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
public class BatteryInfoProvider {
    private static final String TAG = "BatteryInfoProvider";
    private static final long CACHE_INFO_VALID_PERIOD = 120 * 1000;

    private long mlLastUpdateTime;
    private int miBatteryLevel;
    private int miCharging = -1;
    private boolean mbNeedUpdate;

    public int getBatteryLevel(boolean bForceUpdate){
        if(bForceUpdate || mbNeedUpdate ||
                System.currentTimeMillis() - mlLastUpdateTime > CACHE_INFO_VALID_PERIOD){
            UpdateBatteryInfo();
            return miBatteryLevel;
        }else {
            return miBatteryLevel;
        }
    }

    public void setIsCharging(boolean bVal){
        miCharging = bVal ? 1 : 0;
    }

    public boolean getIsCharging(){
        if(-1 == miCharging || mbNeedUpdate){
            UpdateBatteryInfo();
            return 1 == miCharging;
        }else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //小于6.0的，无法获取充电广播，只能自己取
            if(System.currentTimeMillis() - mlLastUpdateTime > CACHE_INFO_VALID_PERIOD){
                UpdateBatteryInfo();
                return 1 == miCharging;
            }
        }

        return 1 == miCharging;
    }

    public void setNeedUpdateInfo(){
        mbNeedUpdate = true;
    }

    private void UpdateBatteryInfo(){
        Logger.i(TAG, "update battery info");
        mbNeedUpdate = false;
        String customPermission = SystemInfoUtil.getApplicationId(AdhocBasicConfig.getInstance().getAppContext()) + ".permission.ACTION_BATTERY_CHANGED";
        Intent batteryInfoIntent = AdhocBasicConfig.getInstance().getAppContext()
                .registerReceiver( null ,
                        new IntentFilter( Intent.ACTION_BATTERY_CHANGED ), customPermission, null ) ;
        miBatteryLevel = batteryInfoIntent.getIntExtra( "level" , 0 );
        int plugged = batteryInfoIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        miCharging = (plugged != -1 && plugged != 0) ? 1 : 0;
        mlLastUpdateTime = System.currentTimeMillis();
    }
}
