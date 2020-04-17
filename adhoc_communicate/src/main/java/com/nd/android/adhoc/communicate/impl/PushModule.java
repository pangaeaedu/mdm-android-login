package com.nd.android.adhoc.communicate.impl;

import android.content.Context;
import android.util.Log;

import com.nd.adhoc.push.adhoc.IAdhocPushChannelConnectListener;
import com.nd.adhoc.push.adhoc.sdk.PushSdkModule;
import com.nd.adhoc.push.core.IPushChannel;
import com.nd.adhoc.push.core.IPushChannelConnectListener;
import com.nd.adhoc.push.core.IPushChannelDataListener;
import com.nd.adhoc.push.core.IPushRecvData;
import com.nd.adhoc.push.core.enumConst.PushConnectStatus;
import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.common.util.AdhocDataCheckUtils;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.communicate.push.IPushModule;
import com.nd.android.adhoc.communicate.push.UpStreamData;
import com.nd.android.adhoc.communicate.push.listener.IAdhocPushConnectListener;
import com.nd.android.adhoc.communicate.push.listener.IPushConnectListener;
import com.nd.android.adhoc.communicate.receiver.IPushDataOperator;
import com.nd.sdp.android.serviceloader.AnnotationServiceLoader;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rx.Observer;
import rx.schedulers.Schedulers;


/**
 * Created by HuangYK on 2018/3/1.
 */

class PushModule implements IPushModule {
    private static final String TAG = "PushModule";

    private Context mContext;

    private List<IPushDataOperator> mPushDataOperators = new CopyOnWriteArrayList<>();
    private final ExecutorService mExecutorService = Executors.newFixedThreadPool(5);
    //    private ICmdMsgReceiver mCmdReceiver;
//    private IFeedbackMsgReceiver mFeedbackReceiver;
    private CopyOnWriteArrayList<UpStreamData> mUpStreamMsgCache = new CopyOnWriteArrayList<>();

    private List<IPushConnectListener> mConnectListeners;

    private IPushChannel mPushChannel = null;

    private IPushChannelConnectListener mChannelConnectListener = new IAdhocPushChannelConnectListener() {
        @Override
        public void onPushDeviceToken(String deviceToken) {
            for (IPushConnectListener listener : mConnectListeners) {
                if (listener instanceof IAdhocPushConnectListener) {
                    ((IAdhocPushConnectListener) listener).onPushDeviceToken(deviceToken);
                }
            }
        }

        @Override
        public void onConnectStatusChanged(IPushChannel pChannel, PushConnectStatus pStatus) {
//            Logger.e("yhq", "onConnectStatusChanged:"+pStatus);
//            for (IPushConnectListener listener : mConnectListeners) {
//                if (pStatus == PushConnectStatus.Connected) {
//                    listener.onConnected();
//                } else {
//                    listener.onDisconnected();
//                }
//            }

            notifyConnectStatus();
        }
    };

    private IPushChannelDataListener mChannelDataListener = new IPushChannelDataListener() {
        @Override
        public void onPushDataArrived(IPushChannel pChannel, IPushRecvData pData) {
            try {
                String data = new String(pData.getContent());
                Logger.e(TAG, "onPushMessage:" + data);
                JSONObject object = new JSONObject(data);
                int type = object.optInt("msgtype");

                for (IPushDataOperator pushDataOperator : mPushDataOperators) {
                    if (pushDataOperator == null) {
                        continue;
                    }

                    if (pushDataOperator.isPushMsgTypeMatche(type)) {
                        pushDataOperator.onPushDataArrived(data);
                        break;
                    }

                }

//                if (type == AdhocPushMsgType.Feedback.getValue()) {
//                    doFeedbackCmdReceived(content);
//                } else {
//                    doCmdReceived(content);
//                }
                Logger.d("yhq_push", "after  onPushMessage:" + data);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e(TAG, "onPushDataArrived error:" + e.toString() +
                        "\n with messege:" + new String(pData.getContent()));
            }
        }

        @Override
        public void onMessageSendResult(String pMsgID, int pErrorCode) {
        }
    };

    PushModule() {
        Logger.d(TAG, "init push module");
        mContext = AdhocBasicConfig.getInstance().getAppContext();
        mConnectListeners = new CopyOnWriteArrayList<>();

        initMessageReceiver();
        initPushChannel();
    }

    private void initMessageReceiver() {
        Iterator<IPushDataOperator> operatorIterator = AnnotationServiceLoader.load(IPushDataOperator.class).iterator();

        while (operatorIterator.hasNext()) {
            mPushDataOperators.add(operatorIterator.next());
        }
    }

    private void initPushChannel() {
        Iterator<IPushChannel> iterator = AnnotationServiceLoader.load(IPushChannel.class)
                .iterator();
        List<IPushChannel> channels = new ArrayList<>();
        while (iterator.hasNext()) {
            IPushChannel channel = iterator.next();
            channels.add(channel);
        }

        if (channels.isEmpty()) {
            throw new RuntimeException("could not load any push channel");
        }

        mPushChannel = channels.get(0);
        Logger.d(TAG, "initPushChannel :" + mPushChannel.getClass().getCanonicalName());
        mPushChannel.addConnectListener(mChannelConnectListener);
        mPushChannel.addDataListener(mChannelDataListener);
        mPushChannel.init(mContext)
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Boolean pBoolean) {
                        Logger.d(TAG, "init push channel result:" + pBoolean);
                    }
                });
    }

    @Override
    public boolean isConnected() {
        PushConnectStatus status = mPushChannel.getCurrentStatus();
        if (status == PushConnectStatus.Connected) {
            return true;
        }

        return false;
    }

    @Override
    public int getChannelType() {
        return mPushChannel.getChannelType();
    }

    @Override
    public void start() {

        Logger.d(TAG, "do start");
        mPushChannel.start()
                .observeOn(Schedulers.from(mExecutorService))
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean pBoolean) {

                    }
                });
    }


    @Override
    public void stop() {
        mPushChannel.stop();
    }

    @Override
    public void setAutoStart(boolean pAutoStart) {
        mPushChannel.setAutoStart(pAutoStart);
    }

    @Override
    public String getDeviceId() {
        return mPushChannel.getPushID();
    }


    @Override
    public void fireConnectatusEvent() {
        notifyConnectStatus();
    }

    @Override
    public void addConnectListener(IPushConnectListener pListener) {
        if (pListener == null || mConnectListeners.contains(pListener)) {
            return;
        }
        mConnectListeners.add(pListener);
    }

    @Override
    public void removeConnectListener(IPushConnectListener pListener) {
        mConnectListeners.remove(pListener);
    }

    @Override
    public void addDataListener(IPushChannelDataListener pListener) {
        mPushChannel.addDataListener(pListener);
    }

    @Override
    public void removeDataListener(IPushChannelDataListener pListener) {
        mPushChannel.removeDataListener(pListener);
    }

    @Override
    public void release() {
//        mCmdReceiver = null;
//        mFeedbackReceiver = null;
        if (!AdhocDataCheckUtils.isCollectionEmpty(mPushDataOperators)) {
            mPushDataOperators.clear();
            mPushDataOperators = null;
        }

        if (!AdhocDataCheckUtils.isCollectionEmpty(mConnectListeners)) {
            mConnectListeners.clear();
        }
    }

    @Override
    public int sendUpStreamMsg(String msgid, long ttlSeconds, String contentType, String content) {
        PushConnectStatus status = mPushChannel.getCurrentStatus();
        if (status != PushConnectStatus.Connected) {
            cacheUpStreamMsg(msgid, ttlSeconds, contentType, content);
            start();
            return 0;
        }

        return PushSdkModule.getInstance().sendUpStreamMsg(msgid, ttlSeconds, contentType, content);
    }

    private void cacheUpStreamMsg(String msgid, long ttlSeconds, String contentType,
                                  String content) {
        Logger.d(TAG, "cacheUpStreamMsg: msgid =  " + msgid);
        UpStreamData data = new UpStreamData(System.currentTimeMillis(), msgid, ttlSeconds,
                contentType, content);
        mUpStreamMsgCache.add(data);
    }

    private synchronized void notifyConnectStatus() {
        Logger.d(TAG, "notifyConnectStatus");

        discardTimeoutMsg();

        PushConnectStatus status = mPushChannel.getCurrentStatus();
        for (IPushConnectListener listener : mConnectListeners) {
            if (status == PushConnectStatus.Connected) {
                listener.onConnected();
                resendMsgThenClearCache();
            } else {
                listener.onDisconnected();
            }
        }
    }

    private void discardTimeoutMsg() {
        //CopyOnWriteArrayList不能通过　iterator删除，直接边循环边删除
        Logger.d(TAG, "discardTimeoutMsg: mUpStreamMsgCache.size = " + mUpStreamMsgCache.size());
        for (UpStreamData data : mUpStreamMsgCache) {
            if (data == null) {
                continue;
            }

            long expireTime = MdmTransferConfig.getRequestTimeout();
            if (System.currentTimeMillis() - data.getSendTime() > expireTime) {
                Log.e(TAG, "discardTimeoutMsg id:" + data.getMsgID());
                mUpStreamMsgCache.remove(data);
            } else {
                Log.e(TAG, "do not need discardTimeoutMsg: msg id:" + data.getMsgID());
            }
        }
    }

    private void resendMsgThenClearCache() {
        Logger.d(TAG, "resendMsgThenClearCache: mUpStreamMsgCache.size = " + mUpStreamMsgCache.size());
        for (UpStreamData data : mUpStreamMsgCache) {
            if (data == null) {
                continue;
            }

            long expireTime = MdmTransferConfig.getRequestTimeout();
            if (System.currentTimeMillis() - data.getSendTime() < expireTime) {
                Log.e(TAG, "resendMsgThenClearCache: msg id:" + data.getMsgID());
                sendUpStreamMsg(data.getMsgID(), data.getTTLSeconds(), data.getContentType(),
                        data.getContent());
            } else {
                Log.e(TAG, "do not need resendMsgThenClearCache: msg id:" + data.getMsgID());
            }
        }

        mUpStreamMsgCache.clear();
    }
}
