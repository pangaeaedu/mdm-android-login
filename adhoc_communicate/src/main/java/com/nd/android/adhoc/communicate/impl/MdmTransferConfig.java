package com.nd.android.adhoc.communicate.impl;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.communicate.request.constant.AdhocNetworkChannel;

/**
 * Created by HuangYK on 2019/6/24.
 */

public final class MdmTransferConfig {

    private static AdhocNetworkChannel mNetworkChannel = AdhocNetworkChannel.CHANNEL_PUSH;

    public static void setNetworkChannel(@NonNull AdhocNetworkChannel pNetworkChannel) {
        mNetworkChannel = pNetworkChannel;
    }

    public static AdhocNetworkChannel getNetworkChannel() {
        return mNetworkChannel;
    }
}
