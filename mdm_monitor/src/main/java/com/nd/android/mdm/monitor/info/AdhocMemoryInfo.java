package com.nd.android.mdm.monitor.info;

/**
 * Created by Administrator on 2017/4/20.
 */

public class AdhocMemoryInfo {
    public long totalMemory;
    public long usedMemory;
    public long freeMemory;

    public AdhocMemoryInfo(long[] info) {
        totalMemory = info[0];
        usedMemory = info[4];
//        freeMemory = totalMemory - usedMemory;
        freeMemory = (totalMemory - usedMemory) / totalMemory;
    }
}
