package com.nd.android.adhoc.communicate.impl;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.nd.adhoc.push.PushSdk;
import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.common.util.AdhocDataCheckUtils;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.communicate.constant.AdhocCmdFromTo;
import com.nd.android.adhoc.communicate.constant.AdhocPushMsgType;
import com.nd.android.adhoc.communicate.push.IPushModule;
import com.nd.android.adhoc.communicate.push.listener.IPushConnectListener;
import com.nd.android.adhoc.communicate.receiver.ICmdMsgReceiver;
import com.nd.android.adhoc.communicate.utils.HttpUtil;
import com.nd.android.mdm.biz.common.ErrorCode;
import com.nd.android.mdm.biz.common.MsgCode;
import com.nd.android.mdm.biz.common.util.SDKLogUtil;
import com.nd.android.mdm.biz.env.MdmEvnFactory;
import com.nd.android.mdm.util.cmd.CmdUtil;
import com.nd.sdp.adhoc.push.IPushSdkCallback;
import com.nd.sdp.android.serviceloader.AnnotationServiceLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Created by HuangYK on 2018/3/1.
 */

class PushModule implements IPushModule {
    private static final String TAG = "PushModule";

    private Context mContext;

    private ICmdMsgReceiver mCmdReceiver;

    private List<IPushConnectListener> mConnectListeners;


    private String mUid;

    PushModule() {
        mContext = AdhocBasicConfig.getInstance().getAppContext();
        mConnectListeners = new CopyOnWriteArrayList<>();
    }

    @Override
    public boolean isConnected() {
        return PushSdk.getInstance().isConnected();
    }

    @Override
    public void start() {
        String pushSrvIp = MdmEvnFactory.getInstance().getCurEnvironment().getPushIp();
        int pushSrvPort = MdmEvnFactory.getInstance().getCurEnvironment().getPushPort();
//        CmdFactory.enableFactory(true);
        PushSdk.getInstance().stop();
        PushSdk.getInstance().startPushSdk(mContext, "mdm", null, pushSrvIp, pushSrvPort, mPushSdkCallback);

        Iterator<ICmdMsgReceiver> receiverIterator =  AnnotationServiceLoader.load(ICmdMsgReceiver.class).iterator();
        mCmdReceiver = receiverIterator.next();

    }

    @Override
    public void stop() {
//        PushSdk.getInstance().stop();
    }

    @Override
    public String getDeviceId() {
        return PushSdk.getInstance().getDeviceid();
    }

    @Override
    public void fireConnectatusEvent() {
//        if (PushSdk.getInstance().isConnected()) {
//            EventBus.getDefault().post(new PushConnectStatusEvent(true));
//        }
        notifyConnectStatus(PushSdk.getInstance().isConnected());
    }

    private IPushSdkCallback.Stub mPushSdkCallback = new IPushSdkCallback.Stub() {
        @Override
        public void onPushDeviceToken(String deviceToken) {
//            deviceId = ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
            //initCallback = null;
        }


        @Override
        public byte[] onPushMessage(String appId, int msgtype, byte[] contenttype, long msgid, long msgTime, byte[] content, String[] extraKeys, String[] extraValues) {
            try {
                if(msgtype == AdhocPushMsgType.Feedback.getValue()){
                    Log.e(TAG, "feedback:"+new String(content));
                }
//                new PushMsgEvent(content).post();
                doCmdReceived(content);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e(TAG, "get error:" + e.toString() + "\n with messege:" + new String(content));
//                new DialogEvent("get error:" + e.toString() + "\nwith messege:" + new String(content.toString()));
            }
            return UUID.randomUUID().toString().getBytes();
        }

        @Override
        public void onPushStatus(final boolean isConnected) {
            Logger.d("HYK", "onPushStatus: isConnected = " + isConnected);
//            EventBus.getDefault().post(new PushConnectStatusEvent(isConnected));
            notifyConnectStatus(isConnected);

            //  郭文要求
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (isConnected) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("stock_id", getUid());
                            String content = jsonObject.toString();
                            String env = MdmEvnFactory.getInstance().getCurEnvironment().getUrl();
                            HttpUtil.post(env, content);
                        } catch (JSONException e) {
                            Logger.e(TAG, "assistant service send stock failed:" + e.getMessage());
                        } catch (Exception e) {
                            Logger.e(TAG, "assistant service send stock failed:" + e.getMessage());
                        }
                    }
                }
            }).start();
        }

        @Override
        public void onPushShutdown() throws RemoteException {
            start();
        }
    };

    public String getUid() {
        if (mUid == null) {
            String resLine = CmdUtil.runCmd("cat /proc/cpuinfo");
            String hardware = null;
            String serial = null;
            String[] lines = resLine.split("\n");
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].indexOf("Hardware") != -1) {
                    hardware = lines[i].substring(lines[i].indexOf(":") + 1, lines[i].length());
                    break;
                }
            }
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].indexOf("Serial") != -1) {
                    serial = lines[i].substring(lines[i].indexOf(":") + 1, lines[i].length());
                    break;
                }
            }
            hardware = hardware != null ? hardware.trim() : null;
            serial = serial != null ? serial.trim() : null;
            if (hardware != null && serial != null && hardware.length() + serial.length() > 63) {
                hardware = hardware.substring(0, 63 - serial.length());
            }
            mUid = String.valueOf(hardware) + "-" + String.valueOf(serial);
        }
        return mUid;
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
    public void release() {
        mCmdReceiver = null;

        if(!AdhocDataCheckUtils.isCollectionEmpty(mConnectListeners)){
            mConnectListeners.clear();
        }
    }

    private void doCmdReceived(byte[] pCmdMsgBytes) throws AdhocException {
        if (pCmdMsgBytes == null || pCmdMsgBytes.length <= 0) {
            throw new AdhocException("PushModule doCmdReceived: Cmd message bytes is null", ErrorCode.FAILED, MsgCode.ERROR_PARAMETER);
        }

        if(mCmdReceiver == null){
            Logger.w(TAG, "mCmdReceiver is null");
            return;
        }

        String pCmdMsg = new String(pCmdMsgBytes);
        SDKLogUtil.v("【PushModule】doCmdReceived: on cmd arrive %s", pCmdMsg);
        mCmdReceiver.onCmdReceived(new String(pCmdMsgBytes), AdhocCmdFromTo.MDM_CMD_DRM, AdhocCmdFromTo.MDM_CMD_DRM);

    }

    private synchronized void notifyConnectStatus(boolean pIsConnected) {
        for (IPushConnectListener listener : mConnectListeners) {
            if (pIsConnected) {
                listener.onConnected();
            } else {
                listener.onDisconnected();
            }
        }
    }

}
