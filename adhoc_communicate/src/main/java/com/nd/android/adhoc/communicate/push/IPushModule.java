package com.nd.android.adhoc.communicate.push;

import com.nd.android.adhoc.communicate.push.listener.IPushConnectListener;

/**
 * Created by HuangYK on 2018/2/28.
 */

public interface IPushModule {

    boolean isConnected();

    int getChannelType();

    void start();

    void stop();

    void setAutoStart(boolean pAutoStart);

    String getDeviceId();

//    String getUid();

    void fireConnectatusEvent();

    void addConnectListener(IPushConnectListener pListener);

    void removeConnectListener(IPushConnectListener pListener);

    void release();

    int sendUpStreamMsg(String msgid, long ttlSeconds, String contentType, String content);

}
