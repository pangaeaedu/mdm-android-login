package com.nd.android.adhoc.communicate.connect.event;

/**
 * Created by HuangYK on 2018/5/2.
 */

public interface IDeviceInfoEvent {

    void notifyDeviceInfo(String pSessionId, int pFrom);
}
