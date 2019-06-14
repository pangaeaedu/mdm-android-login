package com.nd.android.adhoc.communicate.impl;

import com.nd.android.adhoc.communicate.connect.IAdhocConnectModule;
import com.nd.android.adhoc.communicate.push.IPushModule;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * MDM 通信传输层 工厂类
 * <p>
 * Created by HuangYK on 2018/5/4.
 */
public class MdmTransferFactory {

    private volatile static IPushModule mPushModel;
    private volatile static IAdhocConnectModule mCommunicationModule;

    private static AtomicBoolean mPushChannel = new AtomicBoolean(true);

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

    public static void setPushChannel(boolean pPushChannel) {
        mPushChannel.set(pPushChannel);
    }

    public static boolean getPushChannel() {
        return mPushChannel.get();
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
