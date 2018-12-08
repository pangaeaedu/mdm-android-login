package com.nd.android.mdm.monitor.info;

/**
 * Created by Administrator on 2017/4/20.
 */

public class AdhocBatteryInfo {
    public int level;
    public boolean isCharging;

    public AdhocBatteryInfo(int level, boolean isCharging) {
        this.level = level;
        this.isCharging = isCharging;
    }
}
