package com.nd.android.adhoc.communicate.push;

import com.nd.android.adhoc.communicate.push.listener.IPushConnectListener;

/**
 * Created by HuangYK on 2018/2/28.
 */

public interface IPushModule {

    boolean isConnected();

    void start();

    void stop();

    String getDeviceId();

//    String getUid();

    void fireConnectatusEvent();

    void addConnectListener(IPushConnectListener pListener);

    void removeConnectListener(IPushConnectListener pListener);

    void release();

}
