package com.nd.android.adhoc.communicate.impl;

import com.nd.android.adhoc.communicate.connect.IAdhocConnectModule;
import com.nd.android.adhoc.communicate.push.IPushModule;

/**
 * MDM 通信传输层 工厂类
 * <p>
 * Created by HuangYK on 2018/5/4.
 */
public class MdmTransferFactory {

    private volatile static IPushModule mPushModel;
    private volatile static IAdhocConnectModule mCommunicationModule;

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
