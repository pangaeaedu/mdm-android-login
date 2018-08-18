package com.nd.android.mdm.wifi_sdk.business.bean;

import com.nd.android.mdm.wifi_sdk.business.utils.MdmWifiUtils;

import java.io.Serializable;

/**
 * wifi 信号
 * <p>
 * Created by HuangYK on 2018/3/14.
 */
public class MdmWifiSignal implements Serializable {

    /**
     * 名称
     */
    private String mSSID;
    /**
     * 频率
     */
    private int mFrequency;
    /**
     * 等级
     */
    private int mLevel;
//    private int mChannel;


    public MdmWifiSignal(String ssid, int frequency, int level) {
        this.mSSID = ssid;
        this.mFrequency = frequency;
        this.mLevel = level;
//        this.mChannel = MdmWifiUtils.getChannelByFrequency(mFrequency);
    }

    public String getSSID() {
        return mSSID;
    }

    public void setSSID(String mSSID) {
        this.mSSID = mSSID;
    }

    public int getFrequency() {
        return mFrequency;
    }

    public void setFrequency(int mFrequency) {
        this.mFrequency = mFrequency;
    }

    public int getLevel() {
        return mLevel;
    }

    public void setLevel(int mLevel) {
        this.mLevel = mLevel;
    }

    public int getChannel() {
        return MdmWifiUtils.getChannelByFrequency(mFrequency);
    }
}
