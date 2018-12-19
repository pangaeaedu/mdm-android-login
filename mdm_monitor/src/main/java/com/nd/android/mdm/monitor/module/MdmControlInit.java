package com.nd.android.mdm.monitor.module;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.RemoteException;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.util.AdhocDataCheckUtils;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.sp.SharedPreferenceFactory;
import com.nd.android.adhoc.basic.util.storage.AdhocFileWriteUtil;
import com.nd.android.adhoc.basic.util.storage.AdhocStorageUtil;
import com.nd.android.adhoc.basic.util.string.AdhocTextUtil;
import com.nd.android.adhoc.basic.util.thread.AdhocRxJavaUtil;
import com.nd.android.adhoc.basic.util.time.AdhocTimeUtil;
import com.nd.android.adhoc.control.define.IControl_WifiConnect;
import com.nd.android.mdm.basic.ControlFactory;
import com.nd.android.mdm.monitor.SystemControFactory;
import com.nd.android.mdm.wifi_sdk.business.MdmWifiInfoManager;
import com.nd.android.mdm.wifi_sdk.business.utils.MdmWifiUtils;
import com.nd.eci.sdk.lib.libadhoc;
import com.nd.pad.systemapp.IDataCallback;
import com.nd.screen.event.MouseEvent;
import com.nd.screen.interfaces.ScreenCaptureCallback;
import com.nd.screen.interfaces.ScreenCaptureDataCallback;
import com.nd.screen.lib.librtmp;
import com.nd.screen.nativesdk.NativeScreenCapture;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * 原先的  ControlAdminImpl，剥离了 IControl 的实现，以及去除了部分无用的操作。时间关系，其余的功能实现暂时没有做过多的改动
 */
public class MdmControlInit {
    private static final String TAG = "MdmControlInit";
    private static final String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";
    private static final int MESSAGE_WIFI_RECONNECT = 1;
//    private static final int MESSAGE_GET_LINKSPEED = 3;

    private Context mContext;


//    private HandlerThread mHandlerThread;
//    private Handler mHandler;


    private Subscription mNetworkSub;
    private PublishSubject<NetworkInfo> mNetworkUpdatePublish = PublishSubject.create();


    public MdmControlInit(Context pContext) {
        mContext = pContext;
    }

    public void init() {

//        mHandlerThread = new HandlerThread("mdm_control");
//        mHandlerThread.start();
//        mHandler = new Handler(mHandlerThread.getLooper()) {
//            @Override
//            public void handleMessage(Message msg) {
//                switch (msg.what) {
//                    case MESSAGE_WIFI_RECONNECT:
//                        onNetworkChanged(true);
//                        break;
////                    case MESSAGE_GET_LINKSPEED:
////                        sendEmptyMessageDelayed(MESSAGE_GET_LINKSPEED, 5000);
////                        break;
//                }
//            }
//        };

        initNetworkUpdateSub();

        initReceiver();

        initAdhocScreen();

        Logger.d(TAG, "control module ready");
    }

    private void initReceiver(){
        IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ACTION_SIM_STATE_CHANGED);
        mContext.registerReceiver(mReceiver, filter);
    }

    private void initAdhocScreen() {
        librtmp.getInstance().init(mScreenCaptureCallback);
        NativeScreenCapture.startScreenCaptureActivityForICR(mContext, mScreenCaptureCallback);
        libadhoc.setContext(mContext);
    }

    private void initNetworkUpdateSub() {
        mNetworkSub = mNetworkUpdatePublish.asObservable()
                .throttleLast(1, TimeUnit.SECONDS)
                .filter(new Func1<NetworkInfo, Boolean>() {
                    @Override
                    public Boolean call(NetworkInfo networkInfo) {
                        return networkInfo != null;
                    }
                }).compose(AdhocRxJavaUtil.<NetworkInfo>applyDefaultSchedulers()).subscribe(new Subscriber<NetworkInfo>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Logger.e(TAG, "mNetworkUpdatePublish onError: " + throwable);
                        AdhocRxJavaUtil.doUnsubscribe(mNetworkSub);
                        initNetworkUpdateSub();
                    }

                    @Override
                    public void onNext(NetworkInfo networkInfo) {
                        onNetworkUpdate(networkInfo);
                    }
                });
    }

    private void onNetworkUpdate(NetworkInfo info) {
        if (info == null) {
            return;
        }
        String fileName = String.format("%s/%s/wifilog-%d.txt", AdhocStorageUtil.getSdCardPath(), mContext.getPackageName(), AdhocTimeUtil.getDataStamp());
        switch (info.getState()) {
            case DISCONNECTED:
                AdhocFileWriteUtil.writeFile(fileName, AdhocTimeUtil.getTimeStamp() + "\twifi disconnected\n", true);
                onNetworkChanged(false);
                break;
            case CONNECTED:
                AdhocFileWriteUtil.writeFile(fileName, AdhocTimeUtil.getTimeStamp() + "\twifi connected:" + MdmWifiInfoManager.getInstance().getWifiInfo().getSsid() + "\n", true);
                //new PostDeviceInfoEvent("", Cmd.FROM_DRMS).post();  MDM: 放到PUSH连接成功后再发
                onNetworkChanged(true);
                break;
            case DISCONNECTING:
                AdhocFileWriteUtil.writeFile(fileName, AdhocTimeUtil.getTimeStamp() + "\twifi disconnecting\n", true);
                break;
            case CONNECTING:
                AdhocFileWriteUtil.writeFile(fileName, AdhocTimeUtil.getTimeStamp() + "\twifi connecting\n", true);
                break;
            default:
                AdhocFileWriteUtil.writeFile(fileName, AdhocTimeUtil.getTimeStamp() + "\tunkonwn wifi state\n", true);
                break;
        }
    }


    private Subscription mReconnectSub;

    private void onNetworkChanged(final boolean connected) {
        AdhocRxJavaUtil.doUnsubscribe(mReconnectSub);

        mReconnectSub = Observable.timer(20000, TimeUnit.MILLISECONDS)
                .map(new Func1<Long, String>() {
                    @Override
                    public String call(Long aLong) {
                        return SharedPreferenceFactory.getInstance().getModel(mContext).getString("ssid", "");
                    }
                })
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String ssid) {
                        return !AdhocTextUtil.isBlank(ssid);
                    }
                })
                .map(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String ssid) {

                        WifiManager wifiManager = MdmWifiInfoManager.getInstance().getWifiManager();
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                        // 通过系统方式获取出来的 WIFI 信息，可能会带有 双引号，例如  ""NetDragon-FZ""，所以必须先去除
                        String curSsid = MdmWifiUtils.removeDoubleQuotes(wifiInfo.getSSID());

                        if (!TextUtils.isEmpty(ssid) && !ssid.equals(curSsid)) {
                            List<ScanResult> result = MdmWifiInfoManager.getInstance().getWifiManager().getScanResults();
                            if (!AdhocDataCheckUtils.isCollectionEmpty(result)) {
                                for (ScanResult res : result) {
                                    if (ssid.equals(res.SSID)) {
                                        IControl_WifiConnect control_wifi = ControlFactory.getInstance().getControl(IControl_WifiConnect.class);
                                        if (control_wifi != null) {
                                            if (connected &&
                                                    (WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState()) == NetworkInfo.DetailedState.CONNECTED )) {
                                                control_wifi.disconnectWifi();
                                            } else {
                                                control_wifi.connectConfigarationSSID(ssid);
                                            }
                                        }
                                        return false;
                                    }
                                }
                            }
                            // TODO: Toast不應該加在這裡. 應該回報服務端, 此指令執行錯誤原因
                            Logger.d(TAG, "ssid not found:" + ssid);
//                            mHandler.sendEmptyMessageDelayed(MESSAGE_WIFI_RECONNECT, 20000);
                        }
                        return true;
                    }
                })
                .compose(AdhocRxJavaUtil.<Boolean>applyDefaultSchedulers())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(TAG, "onNetworkChanged error: " + e);
                        onNetworkChanged(true);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if(aBoolean){
                            onNetworkChanged(true);
                        }
                    }
                });
    }

//    private void onNetworkChange(boolean connected) {
//        String ssid = SharedPreferenceFactory.getInstance().getModel(mContext).getString("ssid", "");
//        if (AdhocTextUtil.isBlank(ssid)) {
//            return;
//        }
//        mHandler.removeMessages(MESSAGE_WIFI_RECONNECT);
//        WifiManager wifiManager = MdmWifiInfoManager.getInstance().getWifiManager();
//        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//
//        // 通过系统方式获取出来的 WIFI 信息，可能会带有 双引号，例如  ""NetDragon-FZ""，所以必须先去除
//        String curSsid = MdmWifiUtils.removeDoubleQuotes(wifiInfo.getSSID());
//
//        if (!TextUtils.isEmpty(ssid) && !ssid.equals(curSsid)) {
//            List<ScanResult> result = MdmWifiInfoManager.getInstance().getWifiManager().getScanResults();
//            if (!AdhocDataCheckUtils.isCollectionEmpty(result)) {
//                for (ScanResult res : result) {
//                    if (ssid.equals(res.SSID)) {
//                        IControl_WifiConnect control_wifi = ControlFactory.getInstance().getControl(IControl_WifiConnect.class);
//                        if (control_wifi != null) {
//                            if (connected &&
//                                    (WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState()) == NetworkInfo.DetailedState.CONNECTED )) {
//                                control_wifi.disconnectWifi();
//                            } else {
//                                control_wifi.connectConfigarationSSID(ssid);
//                            }
//                        }
//                        return;
//                    }
//                }
//            }
//            // TODO: Toast不應該加在這裡. 應該回報服務端, 此指令執行錯誤原因
//            Logger.d(TAG, "ssid not found:" + ssid);
//            mHandler.sendEmptyMessageDelayed(MESSAGE_WIFI_RECONNECT, 20000);
//        }
//    }

    public void release() {
        Logger.d(TAG, "control module stop");
//        mHandler.removeCallbacksAndMessages(null);
//        mHandlerThread.quit();
        AdhocRxJavaUtil.doUnsubscribe(mReconnectSub);

        mContext.unregisterReceiver(mReceiver);

//        RxJavaUtils.doUnsubscribe(mStopAppInBlacklistSub);
        AdhocRxJavaUtil.doUnsubscribe(mNetworkSub);

        mContext = null;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            switch (action) {
//                case Intent.ACTION_SCREEN_OFF:
//                    keyguardLock.reenableKeyguard();
//                    break;
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                    // HYK Modified on 2018-07-30
                    mNetworkUpdatePublish.onNext((NetworkInfo)intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO));
//                    RxJavaUtils.safeSubscribe(Observable.create(new Observable.OnSubscribe<Void>() {
//                        @Override
//                        public void call(Subscriber<? super Void> subscriber) {
//                            onNetworkUpdate((NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO));
//                            subscriber.onCompleted();
//                        }
//                    }).doOnError(new Action1<Throwable>() {
//                        @Override
//                        public void call(Throwable throwable) {
//                            SDKLogUtil.e( "onNetworkUpdate error");
//                        }
//                    }).compose(RxJavaUtils.<Void>applyDefaultSchedulers()));
                    break;
                case ACTION_SIM_STATE_CHANGED:
                    Logger.d(TAG, "sim state changed");
                    break;
            }
        }
    };

    private ScreenCaptureCallback mScreenCaptureCallback = new ScreenCaptureCallback() {
        private boolean mStarted = false;

        @Override
        public synchronized int start(int width, int height, int frameRate, int bitRate, final ScreenCaptureDataCallback callback) {
            Logger.d(TAG, "screencapture: start screen capture");
            if (mStarted) {
                doStop();
            }
            mStarted = true;
            if (SystemControFactory.getInstance().isSystemApp()) {
                final IDataCallback.Stub mScreenDataCallback = new IDataCallback.Stub() {
                    @Override
                    public void onCallback(byte[] data, int pos) throws RemoteException {
                        callback.sendVideoStream(data, pos);
                    }
                };
                try {
                    SystemControFactory.getInstance().getSystemControl().setDataCallback(mScreenDataCallback);
                    Intent intent = new Intent();
                    intent.putExtra("width", width);
                    intent.putExtra("height", height);
                    intent.putExtra("frameRate", frameRate);
                    intent.putExtra("bitRate", bitRate);
                    SystemControFactory.getInstance().getSystemControl().invoke("startScreenCast", intent);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return 1;
            } else {
                return 0;
            }
        }

        @Override
        public synchronized int stop() {
            Logger.d(TAG, "screencapture: stop screen capture");
            doStop();
            return 0;
        }

        private void doStop() {
            mStarted = false;
            if (SystemControFactory.getInstance().isSystemApp()) {
                try {
                    SystemControFactory.getInstance().getSystemControl().invoke("stopScreenCast", null);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public synchronized void onMouseEvent(MouseEvent[] mouseEvents) {
//            if (SystemControFactory.getInstance().isSystemApp() && mStarted) {
//                new MockEvent(mouseEvents).post();    // 没有地方接收这个事件了，暂时注释
//            }
        }
    };
}
