package com.nd.android.mdm.monitor;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.util.storage.AdhocFileWriteUtil;
import com.nd.android.adhoc.basic.util.storage.AdhocStorageUtil;
import com.nd.android.adhoc.basic.util.string.AdhocTextUtil;
import com.nd.android.adhoc.basic.util.system.AdhocPackageUtil;
import com.nd.android.adhoc.basic.util.thread.AdhocRxJavaUtil;
import com.nd.android.adhoc.control.MdmControlFactory;
import com.nd.android.adhoc.control.define.IControl_Apk;
import com.nd.android.mdm.biz.common.ErrorCode;
import com.nd.android.mdm.monitor.mock.MockModule;
import com.nd.android.mdm.monitor.module.MdmControlInit;
import com.nd.pad.systemapp.ISystemControl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by HuangYK on 2018/3/1.
 */

public final class SystemControFactory {

    private static final String TAG = "SystemControFactory";

    private volatile static SystemControFactory sInstance = null;

    private ISystemControl mSystemControl;
    private boolean mSystemApp;


    private Subscription mDelayToStartSysApp;

    public static SystemControFactory getInstance() {
        if (sInstance == null) {
            synchronized (SystemControFactory.class) {
                if (sInstance == null) {
                    sInstance = new SystemControFactory();
                }
            }
        }
        return sInstance;
    }

    public void init() {
        startSystemAppSync();
        new MdmControlInit(AdhocBasicConfig.getInstance().getAppContext()).init();

        MockModule.getInstance().init(AdhocBasicConfig.getInstance().getAppContext());
    }


    public ISystemControl getSystemControl() {
        return mSystemControl;
    }

    public boolean isSystemApp() {
        return mSystemApp;
    }

    private void startSystemAppSync() {
        if (!checkSystemAppVersion()) {
            return;
        }
        Intent intentSysCtrl = new Intent();
        intentSysCtrl.setAction("com.nd.pad.systemapp.service.SysCtrlService");
        try {
            AdhocBasicConfig.getInstance().getAppContext().bindService(intentSysCtrl, mSystemServiceConnection, Context.BIND_AUTO_CREATE);
        } catch (RuntimeException e) {
            Logger.e(TAG, "startSystemAppSync error: " + e.getMessage());
        }
    }

    public void startSystemAppDelay() {
        AdhocRxJavaUtil.doUnsubscribe(mDelayToStartSysApp);

        mDelayToStartSysApp = Observable.timer(3000, TimeUnit.MILLISECONDS)
                .compose(AdhocRxJavaUtil.<Long>applyDefaultSchedulers())
                .subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Long aLong) {
                startSystemAppSync();
            }
        });
    }

    private ServiceConnection mSystemServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mSystemControl = ISystemControl.Stub.asInterface(service);
            mSystemApp = true;

            MockModule.getInstance().updateConnectStatus(true);
            checkSystemAppVersion();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mSystemControl = null;
            mSystemApp = false;
            // 这里不知道原来为什么是要延迟 3000 毫秒？ 不延迟会有什么问题吗？  -- Comment by HYK  2018-05-08
//            mHandler.sendEmptyMessageDelayed(MESSAGE_BIND_SYS_SERVICE, DELAY_TIME);
            startSystemAppDelay();

            MockModule.getInstance().updateConnectStatus(false);
        }
    };

    // 每次更新systemapp要更新这个
    private final static int CURRENT_SYSTEM_APP_VERSION = 29;

    private boolean registedListener = false;

    private void waiteForInstall(final String fileName) {
        registedListener = true;
        final Context context = AdhocBasicConfig.getInstance().getAppContext();
        ((Application) context.getApplicationContext()).registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (checkSystemAppVersion()) {
                    ((Application) context.getApplicationContext()).unregisterActivityLifecycleCallbacks(this);
                    registedListener = false;
                } else {
                    runInstall(context, fileName);
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    private int getSystemVersion() {
        Context context = AdhocBasicConfig.getInstance().getAppContext();
        PackageInfo info = AdhocPackageUtil.getPackageInfo(context, "com.nd.sdp.demo");
        return info == null ? 0 : info.versionCode;
    }

    private boolean checkSystemAppVersion() {
        final Context context = AdhocBasicConfig.getInstance().getAppContext();
        if (getSystemVersion() < CURRENT_SYSTEM_APP_VERSION) {
            String model = Build.MODEL;
            final String fileName = model.contains("ND3") ? "systemappnd3.apk" : model.contains("101同学派") ? "systemappnd2.apk" : "";
            if (!AdhocTextUtil.isBlank(fileName)) {
                runInstall(context, fileName);
                // 需要安装 但还没装
                return false;
            } else {
                // 不需要安装或者无法安装
                return true;
            }

        } else {
            return true;
        }
    }

    private void runInstall(final Context context, final String appName){
        new Thread(new Runnable() {
            @Override
            public void run() {
                installSystemApp(context, appName);
                if (!registedListener) {
                    waiteForInstall(appName);
                }
            }
        }).start();
    }

    private void installSystemApp(Context context, String appName) {
        InputStream inputStream = null;
        try {
            AssetManager assetManager = context.getAssets();
            inputStream = assetManager.open(appName);
            String filePath = AdhocStorageUtil.getSdCardPath() + "/" + context.getPackageName() + File.separator + appName;
            AdhocFileWriteUtil.createFileFromInputStream(inputStream, filePath);
            IControl_Apk control_apk = MdmControlFactory.getInstance().getControl(IControl_Apk.class);
            long lastInstallTimestamp = System.currentTimeMillis();
            int ret = control_apk == null ? ErrorCode.FAILED : control_apk.install(filePath);
            Logger.d(TAG, String.format("systemapp %s install result %d", appName, ret));
            if (ret == ErrorCode.SUCCESS) {
                SystemControFactory.getInstance().startSystemAppDelay();
            } else if (ret == ErrorCode.UNKNOWN || ret == ErrorCode.EXECUTING) {
                int trycount = 90;
                while (trycount-- > 0) {
                    Thread.currentThread().sleep(2000);

                    PackageInfo info = AdhocPackageUtil.getPackageInfo(context, "com.nd.sdp.demo");
                    if (info != null) {
                        break;
                    }
                    if (System.currentTimeMillis() - lastInstallTimestamp > 20000) {
                        lastInstallTimestamp = System.currentTimeMillis();
                        control_apk.install(filePath);
                    }
                }
                startSystemAppDelay();
            }
        } catch (Exception e) {
            Logger.d(TAG, String.format("installsystemapp %s exception %s", appName, e.toString()));
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void release() {
        MockModule.getInstance().release();
    }


}
