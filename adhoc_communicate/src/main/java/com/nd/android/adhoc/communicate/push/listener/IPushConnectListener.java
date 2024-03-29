package com.nd.android.adhoc.communicate.push.listener;

import androidx.annotation.WorkerThread;

/**
 * Created by HuangYK on 2018/8/7.
 */

public interface IPushConnectListener {

    @WorkerThread
    void onConnected();

    @WorkerThread
    void onDisconnected();
}
