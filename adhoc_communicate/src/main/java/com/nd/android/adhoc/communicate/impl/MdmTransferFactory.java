package com.nd.android.adhoc.communicate.impl;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.communicate.connect.IAdhocConnectModule;
import com.nd.android.adhoc.communicate.push.IPushModule;
import com.nd.android.adhoc.communicate.request.constant.AdhocNetworkChannel;

/**
 * MDM 通信传输层 工厂类
 * <p>
 * Created by HuangYK on 2018/5/4.
 */
public class MdmTransferFactory {

    private volatile static IPushModule mPushModel;
    private volatile static IAdhocConnectModule mCommunicationModule;

    private static AdhocNetworkChannel mNetworkChannel = AdhocNetworkChannel.CHANNEL_PUSH;

    static {
        init();
    }

    private static void init() {
        if (mPushModel == null) {
            mPushModel = new PushModule();
        }

        if (mCommunicationModule == null) {
            mCommunicationModule = new AdhocConnectModule();
        }
    }

    public static void setNetworkChannel(@NonNull AdhocNetworkChannel pNetworkChannel) {
        mNetworkChannel = pNetworkChannel;
    }

    public static AdhocNetworkChannel getNetworkChannel() {
        return mNetworkChannel;
    }

    public static IPushModule getPushModel() {
        return mPushModel;
    }

    public static IAdhocConnectModule getCommunicationModule() {
        return mCommunicationModule;
    }

    public static void release() {
        mPushModel.release();
        mCommunicationModule.release();
    }


}
