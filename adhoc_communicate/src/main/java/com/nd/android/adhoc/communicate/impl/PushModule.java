package com.nd.android.adhoc.communicate.impl;

import android.content.Context;

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
import com.nd.android.adhoc.communicate.push.listener.IPushConnectListener;
import com.nd.android.adhoc.communicate.receiver.IPushDataOperator;
import com.nd.sdp.android.serviceloader.AnnotationServiceLoader;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import rx.Observer;
import rx.schedulers.Schedulers;


/**
 * Created by HuangYK on 2018/3/1.
 */

class PushModule implements IPushModule {
    private static final String TAG = "PushModule";

    private Context mContext;

    private List<IPushDataOperator> mPushDataOperators = new CopyOnWriteArrayList<>();

//    private ICmdMsgReceiver mCmdReceiver;
//    private IFeedbackMsgReceiver mFeedbackReceiver;

    private List<IPushConnectListener> mConnectListeners;

    private IPushChannel mPushChannel = null;

    private IPushChannelConnectListener mChannelConnectListener = new IPushChannelConnectListener() {
        @Override
        public void onConnectStatusChanged(IPushChannel pChannel, PushConnectStatus pStatus) {
            Logger.e("yhq", "onConnectStatusChanged:"+pStatus);
            for (IPushConnectListener listener : mConnectListeners) {
                if (pStatus == PushConnectStatus.Connected) {
                    listener.onConnected();
                } else {
                    listener.onDisconnected();
                }
            }
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
                    if(pushDataOperator == null){
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
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e(TAG, "onPushDataArrived error:" + e.toString() +
                        "\n with messege:" + new String(pData.getContent()));
            }
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

        while (operatorIterator.hasNext()){
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
                        Logger.d(TAG, "init push channel result:"+pBoolean);
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
        mPushChannel.start()
                .observeOn(Schedulers.io())
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
    public String getDeviceId() {
        return mPushChannel.getPushID();
    }


    @Override
    public void fireConnectatusEvent() {
        notifyConnectStatus();
    }

//    private IPushSdkCallback.Stub mPushSdkCallback = new IPushSdkCallback.Stub() {
//        @Override
//        public void onPushDeviceToken(String deviceToken) {
////            deviceId = ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
//            //initCallback = null;
//        }
//
//        @Override
//        public byte[] onPushMessage(String appId, int msgtype, byte[] contenttype, long msgid, long msgTime, byte[] content, String[] extraKeys, String[] extraValues) {
//            try {
//                String data = new String(content);
//                Logger.e(TAG, "onPushMessage:" + data);
//                JSONObject object = new JSONObject(data);
//                int type = object.optInt("msgtype");
//                if (type == AdhocPushMsgType.Feedback.getValue()) {
//                    doFeedbackCmdReceived(content);
//                } else {
//                    doCmdReceived(content);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                Logger.e(TAG, "get error:" + e.toString() + "\n with messege:" + new String(content));
//            }
//            return UUID.randomUUID().toString().getBytes();
//        }
//
//        @Override
//        public void onPushStatus(final boolean isConnected) {
//            Logger.d("HYK", "onPushStatus: isConnected = " + isConnected);
////            EventBus.getDefault().post(new PushConnectStatusEvent(isConnected));
//            notifyConnectStatus(isConnected);
//
//            //  郭文要求
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    if (isConnected) {
//                        try {
//                            JSONObject jsonObject = new JSONObject();
//                            jsonObject.put("stock_id", getUid());
//                            String content = jsonObject.toString();
//                            String env = MdmEvnFactory.getInstance().getCurEnvironment().getUrl();
//                            HttpUtil.post(env, content);
//                        } catch (JSONException e) {
//                            Logger.e(TAG, "assistant service send stock failed:" + e.getMessage());
//                        } catch (Exception e) {
//                            Logger.e(TAG, "assistant service send stock failed:" + e.getMessage());
//                        }
//                    }
//                }
//            }).start();
//        }
//
//        @Override
//        public void onPushShutdown() throws RemoteException {
//            start();
//        }
//    };

//    public String getUid() {
//        if (mUid == null) {
//            String resLine = CmdUtil.runCmd("cat /proc/cpuinfo");
//            String hardware = null;
//            String serial = null;
//            String[] lines = resLine.split("\n");
//            for (int i = 0; i < lines.length; i++) {
//                if (lines[i].indexOf("Hardware") != -1) {
//                    hardware = lines[i].substring(lines[i].indexOf(":") + 1, lines[i].length());
//                    break;
//                }
//            }
//            for (int i = 0; i < lines.length; i++) {
//                if (lines[i].indexOf("Serial") != -1) {
//                    serial = lines[i].substring(lines[i].indexOf(":") + 1, lines[i].length());
//                    break;
//                }
//            }
//            hardware = hardware != null ? hardware.trim() : null;
//            serial = serial != null ? serial.trim() : null;
//            if (hardware != null && serial != null && hardware.length() + serial.length() > 63) {
//                hardware = hardware.substring(0, 63 - serial.length());
//            }
//            mUid = String.valueOf(hardware) + "-" + String.valueOf(serial);
//        }
//        return mUid;
//    }

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
        return PushSdkModule.getInstance().sendUpStreamMsg(msgid, ttlSeconds, contentType, content);
    }

//    private void doCmdReceived(byte[] pCmdMsgBytes) throws AdhocException {
//        if (pCmdMsgBytes == null || pCmdMsgBytes.length <= 0) {
//            throw new AdhocException("PushModule doCmdReceived: Cmd message bytes is null", ErrorCode.FAILED, MsgCode.ERROR_PARAMETER);
//        }
//
//        if (mCmdReceiver == null) {
//            Logger.w(TAG, "mCmdReceiver is null");
//            return;
//        }
//
//        String pCmdMsg = new String(pCmdMsgBytes);
//        Logger.v(TAG, "doCmdReceived: on cmd arrive " + pCmdMsg);
//        mCmdReceiver.onCmdReceived(new String(pCmdMsgBytes), AdhocCmdFromTo.MDM_CMD_DRM, AdhocCmdFromTo.MDM_CMD_DRM);
//
//    }

    private synchronized void notifyConnectStatus() {
        PushConnectStatus status = mPushChannel.getCurrentStatus();
        for (IPushConnectListener listener : mConnectListeners) {
            if (status == PushConnectStatus.Connected) {
                listener.onConnected();
            } else {
                listener.onDisconnected();
            }
        }
    }

}
