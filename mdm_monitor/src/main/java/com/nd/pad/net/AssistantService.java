package com.nd.pad.net;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.nd.android.adhoc.basic.log.Logger;
import com.nd.pad.eci.demo.IAssistant;

import java.util.concurrent.atomic.AtomicBoolean;

public class AssistantService extends Service implements IAssistant {
//    private static final int MESSAGE_BIND_SYS_SERVICE = 1;
//    private static final int MESSAGE_INIT_MODULES = 2;
//    private static final int DELAY_TIME = 3000;

    private static final String TAG = "AssistantService";

    private static final AtomicBoolean serviceStart = new AtomicBoolean(false);
    IAssistant.Stub mStub = new IAssistant.Stub() {

    };
//    private PackageManager pm;
//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                // HYK 一下这段绑定系统服务的操作要移到 control 模块
//                case MESSAGE_BIND_SYS_SERVICE:
//                    try {
//                        PackageInfo info = pm.getPackageInfo("com.nd.sdp.demo", 0);
//                    } catch (PackageManager.NameNotFoundException e) {
//                        break;
//                    }
//                    Intent intentSysCtrl = new Intent();
//                    intentSysCtrl.setAction("com.nd.pad.systemapp.service.SysCtrlService");
//                    bindService(intentSysCtrl, mSystemServiceConnection, Context.BIND_AUTO_CREATE);
//                    break;
//            }
//        }
//    };
    //    private ISystemControl mSystemControl;
//    private ServiceConnection mSystemServiceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            SystemControFactory.getInstance().setSystemControl(ISystemControl.Stub.asInterface(service));
//            SystemControFactory.getInstance().setSystemApp(true);
//            new SystemCtrlConnectEvent(true).post();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            SystemControFactory.getInstance().setSystemControl(null);
//            SystemControFactory.getInstance().setSystemApp(false);
//            mHandler.sendEmptyMessageDelayed(MESSAGE_BIND_SYS_SERVICE, DELAY_TIME);
//            new SystemCtrlConnectEvent(false).post();
//        }
//    };

    public static boolean isServiceStart() {
        return serviceStart.get();
    }

//    public void startSystemApp(long time) {
//        mHandler.sendEmptyMessageDelayed(MESSAGE_BIND_SYS_SERVICE, time);
//    }

//    public ISystemControl getSystemControl() {
//        if (mSystemControl != null) {
//            return mSystemControl;
//        } else {
//            return mRootControl;
//        }
//    }

    @Override
    public void onCreate() {
        Logger.v(TAG, "create assistant service");

        serviceStart.set(true);

        // 这里 目前需要等到 Dex 装载完成再开始一系列初始化操作
//        AdhocApplication.getDexInstallObservable()
//                .first()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<Void>() {
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                        Logger.e(TAG, "assistant service create error");
//                    }
//
//                    @Override
//                    public void onNext(Void aVoid) {
//                        init();
//                        onReady();
//                        unsubscribe();
//                    }
//                });

    }


    @Override
    public IBinder onBind(Intent intent) {
        Logger.v(TAG, "onBind");
        return mStub;
    }

    @Override
    public void onRebind(Intent intent) {
        Logger.i(TAG,"onRebind");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Logger.i(TAG, "onUnbind");
        return true;
    }

//    private void init() {
//        initModules();
//        initUI();
//    }

//    private void initModules() {
//        if (serviceStart.get()) {
//            return;
//        }
//        DataBaseModule.getInstance().init(this);  // 已删除
//        DaemonModule.getInstance().init(this);    // 移到 AssistantMainInitAsync 中进行
//        DataModule.getInstance().init(this);

//        MdmWifiInfoManager.getInstance().start();     // 由 WIFI SDK 模块自行初始化


//        boolean isWifiConnected = MdmWifiInfoManager.getInstance().getIsWiFiConnected();
//        MdmGpsModule.getInstance().setIsWiFiConnected(isWifiConnected);
//        MdmGpsModule.getInstance().init(this);  // 已抽出            // 放在 adhoc_location 模块中自行初始化

//        SystemControFactory.getInstance().init(this, this);
//        MdmControlFactory.getInstance().initControl();  // 在 device_control_root 模块中初始化

//        new MdmControlInit(this).init();      // 在 device_control_nd3 模块中初始化

//        MonitorModule.getInstance().init(this);       // 放在 mdm_monitor 模块中自行初始化
//        PolicySetModule.getInstance().init(this);     // 放在 policy 模块中 自行初始化

//        MdmTransferFactory.getPushModel().addConnectListener(InitialManager.getInstance().getPushConnectListener());  // 改用 ServiceLoader 注入
//        MdmTransferFactory.getCommunicationModule().setAdocFileTransferListener(new MdmAdocFileTransferListener());   // 改用 ServiceLoader 注入
//        MdmTransferFactory.getCommunicationModule().startAdhoc();         // 改为传输层执行初始化


//        ScriptModule.getInstance().init(this);  // 已删除
//        MockModule.getInstance().init(this);
//        SettingModule.getInstance().init(this);       // 在 device_control_nd3 模块中初始化
//        CacheModule.getInstance().init(this);  // 已删除

        //注册监听
//        ImsiModule.getInstance().init(this);
//        AutoUpdateModule.getInstance().init(this);    // 已抽出
//        AutoUpdateModule.getInstance().trigger();
//        LocationModule.getInstance().init(this);  // 已抽出


//        MdmTransferFactory.getPushModel().start();      // 改为传输层执行初始化

////        DeliverProjectTaskFactory.getInstance().startAllProjectTasks();
//        IDeliverRestorePlay restorePlay =
//                (IDeliverRestorePlay) AdhocFrameFactory.getInstance().getAdhocRouter().build(IDeliverRestorePlay.ROUTE_PATH).navigation();
//        if (restorePlay != null) {
//            restorePlay.restorePlay();
//        }

//        MdmToastModule.getInstance().init(this);
//        MsgDlgModule.getInstance().init(this);
//        serviceStart.set(true);
//        new ServiceStartEvent().post();
//    }

    // 这两个 方法 放到了 AssistantMainInitAsync 中执行
//        private void initUI() {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (!Settings.canDrawOverlays(this)) {
//                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                    try {
//                        startActivity(intent);
//                        MdmToastModule.getInstance().showToast("allow draw over other apps to show log");
//                    } catch (RuntimeException e) {
//                        Logger.e(TAG, "Failed to open overlay permission activity: " + e);
//                    }
//
//                }
//            }
//        }
    //    private void onReady() {
    //        Intent intent = new Intent();
    //        intent.setAction("com.nd.pad.nett.SERVICE_READY");
    //        sendBroadcast(intent);
    //    }




//    public void onEvent(PushConnectStatusEvent event) {
//        if (event.isConnected()) {
//            try {
//                CertificateVerifier.getInstance().start(this);
//            } catch (InterruptedException e) {
//                Logger.i(TAG, "certificate : start certificate exception %s", e.toString());
//            }
//        }
//    }

    @Override
    public IBinder asBinder() {
        return mStub;
    }

    @Override
    public void onDestroy() {
        Logger.d(TAG, "destroy service");
//        mHandler.removeCallbacksAndMessages(null);
//        AdhocMsgDlgModule.getInstance().release();
//        MdmToastModule.getInstance().release();
//        DataBaseModule.getInstance().release();
//        DaemonModule.getInstance().release();
////        MdmWiFiModule.getInstance().release();
//        MdmWifiInfoManager.getInstance().release();
//
//        MdmGpsModule.getInstance().release();
//        //移除sim卡检测模块
//        ImsiModule.getInstance().release();
//        AutoUpdateModule.getInstance().release();
//
////        MdmTransferFactory.release();
//
////        SystemControFactory.getInstance().release();
//
////        ScreenCaptureModule.getImpl().release();
//        ScriptModule.getInstance().release();
//        MonitorModule.getInstance().release();
////        MockModule.getInstance().release();
//        SettingModule.getInstance().release();
//        EventBus.getDefault().unregister(this);
//        CacheModule.getInstance().release();
//        LocationModule.getInstance().release();
//        serviceStart.set(false);
        super.onDestroy();
    }
}