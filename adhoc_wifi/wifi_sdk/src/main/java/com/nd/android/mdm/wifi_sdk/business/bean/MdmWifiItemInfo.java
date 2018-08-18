package com.nd.android.mdm.wifi_sdk.business.bean;

import android.net.wifi.ScanResult;

import com.nd.android.mdm.wifi_sdk.business.basic.constant.MdmWifiStatus;

/**
 * Created by HuangYK on 2018/3/19.
 */

public class MdmWifiItemInfo {
    private ScanResult mScanResult;

    private MdmWifiPwd mWifiPwd;

    private MdmWifiStatus state;

    public MdmWifiItemInfo() {
    }

    public ScanResult getScanResult() {
        return mScanResult;
    }

    public void setScanResult(ScanResult scanResult) {
        mScanResult = scanResult;
    }

    public MdmWifiPwd getWifiPwd() {
        return mWifiPwd;
    }

    public void setWifiPwd(MdmWifiPwd wifiPwd) {
        mWifiPwd = wifiPwd;
    }

    public MdmWifiStatus getState() {
        return state;
    }

    public void setState(MdmWifiStatus state) {
        this.state = state;
    }
}
