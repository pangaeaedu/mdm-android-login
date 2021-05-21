package com.nd.android.adhoc.communicate.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.os.IBinder;
import android.os.RemoteException;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.common.toast.AdhocToastModule;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.util.app.AdhocPackageUtil;
import com.nd.android.adhoc.basic.util.storage.AdhocStorageAdapter;
import com.nd.android.adhoc.basic.util.storage.ZipCompressorUtil;
import com.nd.android.adhoc.basic.util.system.AdhocDeviceUtil;
import com.nd.android.adhoc.basic.util.thread.AdhocRxJavaUtil;
import com.nd.android.adhoc.communicate.connect.IAdhocConnectModule;
import com.nd.android.adhoc.communicate.connect.callback.AdhocCallbackImpl;
import com.nd.eci.sdk.IAdhoc;
import com.nd.eci.sdk.service.AdhocService;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

class AdhocConnectModule implements IAdhocConnectModule {

    private static final String TAG = "AdhocConnectModule";

    private Context mContext;
    private IAdhoc mAdhoc;
    private String mTurnId;
//    private IDeviceInfoEvent mDeviceInfoEvent;
    private AdhocCallbackImpl mAdhocCallback;

    private final byte[] mTurnIdLock = new byte[]{};


    AdhocConnectModule() {
        mContext = AdhocBasicConfig.getInstance().getAppContext();
        mAdhocCallback = new AdhocCallbackImpl();
    }

    public void startAdhoc() {

        Context context = AdhocBasicConfig.getInstance().getAppContext();
        Logger.i(TAG, "AdhocConnectModule ready");
        Intent intent = new Intent(context, AdhocService.class);
        context.bindService(intent, mConn, Context.BIND_AUTO_CREATE);
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//        context.registerReceiver(mNetworkBroadcastReceiver, intentFilter);
    }

    // 这些原先是通过回调给 MonitorModule 去做，其实也就只是监听网络连上以后就去更新一次 deviceInfo，
    // 没有必要放在这里去通知，还要外部再设置一个监听进来，多此一举
//    private BroadcastReceiver mNetworkBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
//                ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//                NetworkInfo info = manager.getActiveNetworkInfo();
//
//                if (info != null) {
//                    mSessionId = UUID.randomUUID().toString();
//                }
//
//                if (mDeviceInfoEvent != null) {
////                    new PostDeviceInfoEvent(UUID.randomUUID().toString(), MdmCmdFromTo.MDM_CMD_DRM.getValue()).post();
//                    mDeviceInfoEvent.notifyDeviceInfo(mSessionId, AdhocCmdFromTo.MDM_CMD_DRM.getValue());
//                }
//            }
//        }
//    };


    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mAdhoc = IAdhoc.Stub.asInterface(binder);
            try {
                String logPath = AdhocStorageAdapter.getFilesDir("log/adhoclog/");
                String receivePath = AdhocStorageAdapter.getFilesDir("adhocrecv/");
                mAdhoc.setLogPathAndName(logPath, "adhoc");
                mAdhoc.setRecvFilePath(receivePath);
                mAdhoc.setMasterName("teacher");
                mAdhoc.startAndJoin(null, 0, 12580, logPath, "adhoc", receivePath, mAdhocCallback);
                synchronized (mTurnIdLock) {
                    if (!TextUtils.isEmpty(mTurnId)) {
                        Logger.i(TAG, "set turnid " + mTurnId);
                        mAdhoc.setTurnId(mTurnId);
                    }
                }
            } catch (RemoteException e) {
                Logger.e(TAG, "remote exception:" + e.toString());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Intent intent = new Intent(mContext, AdhocService.class);
            mContext.bindService(intent, mConn, Context.BIND_AUTO_CREATE);
        }
    };


//    @Override
//    public void setDeviceInfoEvent(IDeviceInfoEvent pDeviceInfoEvent) {
//        mDeviceInfoEvent = pDeviceInfoEvent;
//    }

//    @Override
//    public void setConnectListener(IAdhocConnectListener pListener) {
//        mAdhocCallback.setConnectListener(pListener);
//    }

    @Override
    public void sendLoginInfo(String pDevToken, JSONObject pDeviceInfo) {
        if (!mAdhocCallback.isAdhocConnect()) {
            return;
        }
        try {
            JSONObject json = new JSONObject();
            json.put("cmd", "login");
            JSONObject data = new JSONObject();
            json.put("data", data);
            PackageInfo info = AdhocPackageUtil.getPackageInfo(mContext);
            data.put("versioncode", info == null ? 0 : info.versionCode);
            data.put("versionname", info == null ? "" : info.versionName);
            data.put("type", 1);
            data.put("deviceid", pDevToken);

            if (pDeviceInfo != null) {
                data.put("level", pDeviceInfo.getInt("battery"));
                data.put("status", pDeviceInfo.getBoolean("charge"));
            }

            // 备忘：此处这么修改是因为出现和登录时回报的 WifiMac 不一致的情况，所以 mac 统一改为传 wifimac  -- by hyk 2018-08-29
//            String mac = AdhocNetworkIpUtil.getLocalMacAddressFromIp(mContext, AdhocNetworkIpUtil.getCurrentIp(mContext));
            String mac = AdhocDeviceUtil.getWifiMac(mContext);
            if(TextUtils.isEmpty(mac)){
                data.put("mac", pDevToken);
            }else {
                data.put("mac", mac.replace(":",""));
            }

//            new MessageEvent(json.toString()).post();
            sendMessage(json.toString());
        } catch (JSONException e) {
            Logger.e(TAG, "sendLoginInfo error: " + ExceptionUtils.getStackTrace(e));
        }

    }


    private String formatMac(@NonNull String mac) {
        return String.format("%12s", mac.replace(":", "").toLowerCase()).replace(" ", "0");
    }

//    public void onEvent(CmdEvent event) {
//        String cmd = event.getCmd();
//        if (mAdhoc != null) {
//            try {
//                mAdhoc.sendCmd(event.getCmd().getBytes(), 6);
//            } catch (RemoteException e) {
//                SDKLogUtil.e("send cmd failed:" + e.toString());
//            }
//        }
//    }

    @Override
    public void release() {
//        mContext.unregisterReceiver(mNetworkBroadcastReceiver);
    }

    @Override
    public boolean isAdhocConnect() {
        return mAdhocCallback.isAdhocConnect();
    }

//    public void onEvent(DeviceTokenChangeEvent event) {
//        if (mAdhocConnect) {
//            sendLoginInfo();
//        }
//    }

    @Override
    public void uploadFile(final String pLocalPath, final String pFileInfo, final int pTimeOut) {
        AdhocRxJavaUtil.safeSubscribe(Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                if (mAdhoc == null) {
                    subscriber.onCompleted();
                    return;
                }

                File file = new File(pLocalPath);
                if (!file.exists()) {
                    // TODO: Toast不應該加在這裡. 應該回報服務端, 此指令執行錯誤原因
                    Logger.d(TAG, "file not exist");
                    subscriber.onCompleted();
                    return;
                }
                if (file.isDirectory()) {
                    try {
                        String zipPath = String.format("%s/%s.zip", AdhocStorageAdapter.getFilesDir("adhocfile"), file.getName());
                        if (pLocalPath.equalsIgnoreCase(String.format("%s/%s", AdhocStorageAdapter.getFilesDir("adhocfile"), file.getName()))) {
//                        new DialogEvent("can not send this folder").post();
                            AdhocToastModule.getInstance().showToast("can not send this folder");
                            subscriber.onCompleted();
                            return;
                        }
                        ZipCompressorUtil.doZip(file.getAbsolutePath(), zipPath);
                        int timeout = pTimeOut != 0 ? pTimeOut : (int) new File(zipPath).length() / 200000 + 10;
                        mAdhoc.sendFile(zipPath, pFileInfo.getBytes(), timeout);  // default speed is 200kb
                    } catch (Exception e) {
                        Logger.e(TAG, "send zip file failed:" + e.toString());
                    }
                } else {
                    try {
                        int timeout = pTimeOut != 0 ? pTimeOut : (int) file.length() / 200000 + 10;
                        mAdhoc.sendFile(pLocalPath, pFileInfo.getBytes(), timeout);  // default speed is 200kb
                    } catch (RemoteException e) {
                        Logger.e(TAG, "send file exception:" + e.toString());
                    }
                }

                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()));

    }


    @Override
    public void modifyTurnId(@NonNull String pTurnId) {
        if (mAdhoc != null) {
            try {
                mAdhoc.setTurnId(pTurnId);
                Logger.d(TAG, "set turnid " + pTurnId);
            } catch (RemoteException e) {
                Logger.e(TAG, "set turnid " + pTurnId + "exception:" + e.toString());
            }
        } else {
            Logger.d(TAG, "ignore turnid event , turnid :" + pTurnId);
            synchronized (mTurnIdLock) {
                mTurnId = pTurnId;
            }
        }
    }

    @Override
    public void sendMessage(@NonNull final String pMessage) {
        if (mAdhoc != null && !TextUtils.isEmpty(pMessage)) {
            AdhocRxJavaUtil.safeSubscribe(Observable.create(new Observable.OnSubscribe<Void>() {
                @Override
                public void call(Subscriber<? super Void> subscriber) {
                    Logger.i(TAG, "adhoc send message");
                    Logger.d(TAG, "adhoc send message:" + pMessage);
                    try {
                        byte[] data = pMessage.getBytes();
                        if (data.length >= 1024 * 4) {
                            mAdhoc.sendData(data, "".getBytes(), 6);
                        } else {
                            mAdhoc.sendCmd(data, 6);
                        }
                    } catch (RemoteException e) {
                        Logger.d(TAG, "send message exception:" + e.toString());
                    }
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io()));


        }
    }

    // 改为用上面直接调用的方法
//    public void onEventAsync(MessageEvent event) {
//        sendMessage(event.toString());
//    }

//    @Override
//    public void doHttpPost(final String pUrl, final String pContent) {
//        AdhocRxJavaUtil.safeSubscribe(Observable.create(new Observable.OnSubscribe<Void>() {
//            @Override
//            public void call(Subscriber<? super Void> subscriber) {
//                HttpUtil.post(pUrl, pContent);
//                subscriber.onCompleted();
//            }
//        }).subscribeOn(Schedulers.io()));
//    }

//    @Override
//    public void setAdocFileTransferListener(IAdocFileTransferListener pListener) {
//        mAdhocCallback.setFileTransferListener(pListener);
//    }

//    public void onEventAsync(HttpPostEvent event) {
//        HttpUtil.post(event.url, event.toString());
//    }

    // HYK 没有地方发送，先注释
//    public void onEventBackgroundThread(DataEvent event) {
//        if (mAdhoc != null) {
//            try {
//                mAdhoc.sendData(event.getData(), event.getInfo(), 6);
//            } catch (RemoteException e) {
//                SDKLogUtil.d("send message exception:" + e.toString());
//            }
//        }
//    }
}