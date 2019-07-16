package com.nd.android.adhoc.communicate.impl;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.communicate.request.constant.AdhocNetworkChannel;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by HuangYK on 2019/6/24.
 */

public final class MdmTransferConfig {

    private static AdhocNetworkChannel sNetworkChannel = AdhocNetworkChannel.CHANNEL_PUSH;

    private static AtomicLong sRequestTimeout = new AtomicLong(30);

    public static void setNetworkChannel(@NonNull AdhocNetworkChannel pNetworkChannel) {
        sNetworkChannel = pNetworkChannel;
    }

    public static AdhocNetworkChannel getNetworkChannel() {
        return sNetworkChannel;
    }

    public static void setRequestTimeout(long pRequestTimeout){
        sRequestTimeout.set(pRequestTimeout);
    }

    public static long getRequestTimeout(){
        return sRequestTimeout.get();
    }
}
