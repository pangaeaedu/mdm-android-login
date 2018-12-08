package com.nd.android.mdm.monitor.info;

/**
 * Created by Administrator on 2017/4/20.
 */

public class AdhocSDCardInfo {
    public long totalSystemSpace;
    public long freeSystemSpace;
    public long usedSystemSpace;
    public long totalExternalSpace;
    public long freeExternalSpace;
    public long usedExternalSpace;

    public AdhocSDCardInfo(long[] info) {
        totalSystemSpace = info[0];
        freeSystemSpace = info[1];
        usedSystemSpace = totalSystemSpace - freeSystemSpace;
        totalExternalSpace = info[2];
        freeExternalSpace = info[3];
        usedExternalSpace = totalExternalSpace - freeExternalSpace;
    }
}
