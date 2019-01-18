package com.nd.android.mdm.monitor;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.util.storage.AdhocStorageUtil;
import com.nd.android.adhoc.basic.util.app.AdhocPackageUtil;
import com.nd.android.adhoc.basic.util.thread.AdhocRxJavaUtil;
import com.nd.android.adhoc.control.MdmControlFactory;
import com.nd.android.adhoc.control.define.IControl_Apk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by XWQ on 2018/3/8 0008.
 * 為了隨時隨地一安裝 Daemon, 就把 Daemon 跑起來, 雖然是一個怪怪的需求
 */

public class DaemonModule {
    public static final int DAEMON_VERSION = 13;

    private static final String TAG = DaemonModule.class.getSimpleName();
    private static final int STATE_NONE = 0;
    private static final int STATE_COPYING = 1;
    private static final int STATE_COPIED = 2;
    private static final int STATE_INSTALLING = 3;
    private static final int STATE_INSTALLED = 4;
    private static final int STATE_RUNNING = 5;


    private static final int MESSAGE_DAEMON_CHECKING = 1;
    private static final int MONITOR_INTERVAL = 10000;
    private static final String PACKAGE_NAME = "com.nd.pad.daemon";
    private static DaemonModule instance = new DaemonModule();
    private ActivityManager mActivityManager;
    private String packagePath;
    private Context mContext;
    private AtomicInteger mMonitorState;

//    private HandlerThread handlerThread;
//    private Handler mHandler;

    private DaemonModule() {
//        handlerThread = new HandlerThread("daemonthread");
//        handlerThread.start();
//        mHandler = new Handler(handlerThread.getLooper()) {
//            @Override
//            public void handleMessage(Message msg) {
//                switch (msg.what) {
//                    case MESSAGE_DAEMON_CHECKING:
//                        mHandler.sendEmptyMessageDelayed(MESSAGE_DAEMON_CHECKING, MONITOR_INTERVAL);
//                        monitorDaemon();
//                        break;
//                }
//            }
//        };
    }

    public static DaemonModule getInstance() {
        return instance;
    }

    public void init(Context context) {
        Logger.i(TAG, "handle Start Monitor");
        mContext = context;
        packagePath = AdhocStorageUtil.getSdCardPath() + "/" + mContext.getPackageName() + "/" + "daemonapp.apk";
        mMonitorState = new AtomicInteger(STATE_NONE);
        mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
//        mHandler.sendEmptyMessageDelayed(MESSAGE_DAEMON_CHECKING, MONITOR_INTERVAL);
        checkDaemon();
    }

    public void release() {
        Logger.d(TAG, "Stop DaemonModule");
//        mHandler.removeMessages(MESSAGE_DAEMON_CHECKING);
        AdhocRxJavaUtil.doUnsubscribe(mCheckDaemonSub);
    }

    private void checkRunning() {
        Logger.d(TAG, "daemon module check daemon running");
        List<ActivityManager.RunningAppProcessInfo> listOfProcesses = mActivityManager.getRunningAppProcesses();
        if (listOfProcesses != null && listOfProcesses.size() > 0) {
            for (ActivityManager.RunningAppProcessInfo process : listOfProcesses) {
                if (process.processName.contains(PACKAGE_NAME)) {
                    mMonitorState.set(STATE_RUNNING);
                    return;
                }
            }
        }
        mMonitorState.set(STATE_NONE);
    }

    private Subscription mCheckDaemonSub;

    private void checkDaemon(){
        AdhocRxJavaUtil.doUnsubscribe(mCheckDaemonSub);
        mCheckDaemonSub = Observable.timer(MONITOR_INTERVAL, TimeUnit.MILLISECONDS)
                .map(new Func1<Long, Void>() {
                    @Override
                    public Void call(Long aLong) {
                        monitorDaemon();
                        return null;
                    }
                })
                .compose(AdhocRxJavaUtil.<Void>applyDefaultSchedulers())
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        checkDaemon();
                    }

                    @Override
                    public void onNext(Void aVoid) {
                        checkDaemon();
                    }
                });
    }





    @SuppressWarnings("ConstantConditions")
    private void startDaemon() {
        Logger.d(TAG, "daemon module is starting daemon service");
        PackageManager packageManager = mContext.getPackageManager();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(PACKAGE_NAME, PACKAGE_NAME + "" + ".DaemonActivity"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            try {
                mContext.startActivity(intent);
//                mHandler.removeMessages(MESSAGE_DAEMON_CHECKING);
                AdhocRxJavaUtil.doUnsubscribe(mCheckDaemonSub);
            } catch (RuntimeException e) {
                Logger.e(TAG, "start DaemonActivity error:" + e);
                return;
            }
            // 5.0以上并不能检索应用,无法获取是否能运行成功,所以无论如何都要保证调用到一次,后续就不再检索了
        } else {
            Log.i(TAG, "start Daemon");
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(PACKAGE_NAME, PACKAGE_NAME + ".KitkatDaemonService"));
            try {
                JobIntentService.enqueueWork(mContext, intent.getComponent(), 1000, intent);
            } catch (RuntimeException e) {
                Logger.e(TAG, "start KitkatDaemonService error: " + e);
                return;
            }
        }
        mMonitorState.set(STATE_RUNNING);
    }

    private void checkVersion() {
        PackageInfo info = AdhocPackageUtil.getPackageInfo(mContext, PACKAGE_NAME);
        if (info == null || info.versionCode < DAEMON_VERSION) {
            File file = new File(packagePath);
            if (file.exists()) {
                file.delete();
            }
        }
        mMonitorState.set(STATE_COPYING);
        copyFile();
    }

    private void copyFile() {
        Logger.d(TAG, "daemon module copy file");
        PackageManager packageManager = mContext.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(packagePath, 0);
        if (packageInfo == null) {
            mMonitorState.set(STATE_COPYING);
        } else {
            mMonitorState.set(STATE_COPIED);
            Logger.d(TAG, "daemon module file exist,skip copy");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                AssetManager assetManager = mContext.getAssets();
                InputStream inputStream = null;
                OutputStream outputStream = null;
                try {
                    inputStream = assetManager.open("daemonapp.apk");
                    outputStream = new FileOutputStream(packagePath);
                    byte[] buffer = new byte[1024];
                    int size;
                    while ((size = inputStream.read(buffer, 0, 1024)) != 0) {
                        if (size >= 0) {
                            outputStream.write(buffer, 0, size);
                        } else {
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mMonitorState.set(STATE_COPIED);
            }
        }).start();
    }

    private boolean isServiceExist() {
        PackageInfo info = AdhocPackageUtil.getPackageInfo(mContext, PACKAGE_NAME);
        return info != null && info.versionCode >= DAEMON_VERSION;
    }

    private void install() {
        Logger.d(TAG, "daemon module install package");
        File file = new File(packagePath);
        if (!file.exists()) {
            return;
        }
        if (isServiceExist()) {
            mMonitorState.set(STATE_INSTALLED);
            Logger.d(TAG, "daemon module package exist skip install");
            return;
        }
        IControl_Apk control_apk = MdmControlFactory.getInstance().getControl(IControl_Apk.class);
        if (control_apk != null) {
            mMonitorState.set(STATE_INSTALLING);
            int res = control_apk.install(packagePath);
            mMonitorState.set(STATE_INSTALLED);
            if (res != 0) {
                Logger.d(TAG, "daemon module install failed");
            } else {
                Logger.d(TAG, "daemon module install success");
            }
//        } else {
//            new DialogEvent(DialogEvent.HIDE_DIALOG).show();
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            MdmBasicConfig.getInstance().getAppContext().startActivity(intent);
//            // 无法确认用户什么时候安装,只能当作已安装好
//            mMonitorState.set(STATE_INSTALLED);
        }
    }

    private void monitorDaemon() {
        switch (mMonitorState.get()) {
            case STATE_NONE:
                checkVersion();
                break;
            case STATE_COPYING:
                // waiting for copy
                Logger.d(TAG, "daemon module is waiting for copy file");
                break;
            case STATE_COPIED:
                install();
                break;
            case STATE_INSTALLING:
                // waiting for install
                Logger.d(TAG, "daemon module is waiting for install package");
                break;
            case STATE_INSTALLED:
                startDaemon();
                break;
            case STATE_RUNNING:
                checkRunning();
                break;
        }
    }

}
