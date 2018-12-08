package com.nd.android.mdm.monitor.info;


import com.nd.eci.sdk.utils.MonitorUtil;

/**
 * Created by Administrator on 2017/4/20.
 */

public class AdhocCpuInfo {
    public int cpuRate;
    public AdhocCpuInfo(long[] info){
        cpuRate = (int) MonitorUtil.getCpuInfo()[8];
    }
}
