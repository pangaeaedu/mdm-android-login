package com.nd.android.adhoc.communicate.push.listener;


public interface IAdhocPushConnectListener extends IPushConnectListener {
    void onPushDeviceToken(String deviceToken);
}
