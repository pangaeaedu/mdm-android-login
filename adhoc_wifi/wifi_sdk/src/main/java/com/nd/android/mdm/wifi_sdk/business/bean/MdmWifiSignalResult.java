package com.nd.android.mdm.wifi_sdk.business.bean;


import com.nd.android.adhoc.basic.common.util.AdhocDataCheckUtils;

import java.util.List;

/**
 * Created by HuangYK on 2018/5/23.
 */

public class MdmWifiSignalResult {

    private List<MdmWifiSignal> mWifiSignalList;

    private int[] mXLabels = null;

    private int[] mInterferences = null;

    public MdmWifiSignalResult(List<MdmWifiSignal> wifiSignalList, int[] XLabels, int[] interferences) {
        mWifiSignalList = wifiSignalList;
        mXLabels = XLabels;
        mInterferences = interferences;
    }

    public List<MdmWifiSignal> getWifiSignalList() {
        return mWifiSignalList;
    }

    public int[] getXLabels() {
        return mXLabels;
    }

    public int[] getInterferences() {
        return mInterferences;
    }

    public boolean isValid(){
        return !AdhocDataCheckUtils.isCollectionEmpty(mWifiSignalList)
                && mXLabels != null && mXLabels.length > 0
                && mInterferences != null && mInterferences.length > 0;
    }
}
