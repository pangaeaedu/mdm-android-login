package com.nd.android.mdm.wifi_sdk.business.basic.constant;

/**
 * Created by cbs on 2016/12/31 0031.
 */

public enum MdmWifiChannel {
    /**
     * 当选择5Ghz频段时，
     * 分为两个信道区，1区对应36-64，
     * 2区对应149-165
     */
    CHANNEL_ONE(36,64),
    CHANNEL_TWO(149,165);

    /**
     * 区间对应的开始和结束信道号
     */
    int mStartChannel;
    int mEndChannel;

    MdmWifiChannel(int start, int end) {
        this.mStartChannel = start;
        this.mEndChannel = end;
    }

    public int getStartChannel() {
        return this.mStartChannel;
    }

    public int getEndChannel() {
        return this.mEndChannel;
    }
}
