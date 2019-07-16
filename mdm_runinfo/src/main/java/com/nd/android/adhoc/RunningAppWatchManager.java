package com.nd.android.adhoc;

import android.content.pm.PackageInfo;
import android.os.RemoteException;

import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.util.app.AdhocPackageUtil;
import com.nd.android.adhoc.basic.util.app.receiver.AdhocAppListenerManager;
import com.nd.android.adhoc.basic.util.app.receiver.IAdhocAppListener;
import com.nd.android.adhoc.basic.util.thread.AdhocRxJavaUtil;
import com.nd.android.adhoc.control.normal.api.ISystemServiceApi;
import com.nd.android.adhoc.reportAppRunning.AdhocReportAppRunning;
import com.nd.android.mdm.basic.system.SystemServiceFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * APP运行统计类入口类
 */
public class RunningAppWatchManager {
    private static final String TAG = "RunningAppWatchManager";

    private final static String SYSTEM_SERVICE_PACKAGENAME = "com.nd.adhoc.systemservice";

    private Subscription mSubscription;

    /**10秒检查一次*/
    private static final long CHECK_TIME_INTERVAL = 10 * 1000;

    private static RunningAppWatchManager instance;

    private final List<IAppListListenner> mListeners = new ArrayList<>();

    public synchronized static RunningAppWatchManager getInstance() {
        if (instance == null) {
            instance = new RunningAppWatchManager();
        }
        return instance;
    }

    public void addListeners(IAppListListenner listenner){
        synchronized (mListeners) {
            mListeners.add(listenner);
        }
    }

    public void removeListeners(IAppListListenner listenner){
        synchronized (mListeners) {
            mListeners.remove(listenner);
        }
    }

    private RunningAppWatchManager() {

    }

    public void init(){
        Logger.i(TAG, "init");
        // 如果没有安装，那么就注册一个应用安装监听
        if (!AdhocPackageUtil.checkPackageInstalled(SYSTEM_SERVICE_PACKAGENAME)) {
            Logger.i(TAG, "system service not installed, start listen");
            AdhocAppListenerManager.getInstance().addPackageListener(mAdhocAppListener);
        }else {
            deal();
        }
    }

    public void stopWatching(){
        Logger.i(TAG, "stop watching");
        AdhocAppListenerManager.getInstance().removePakcageListener(mAdhocAppListener);
        AdhocRxJavaUtil.doUnsubscribe(mSubscription);
        AdhocReportAppRunning.getInstance().stopWatching();
    }

    private void deal(){
        Logger.i(TAG, "system service installed, deal with it");
        AdhocReportAppRunning.getInstance().deal();
    }

    private IAdhocAppListener mAdhocAppListener = new IAdhocAppListener() {
        @Override
        public void onPackageAdded(String pPackageName) {
            if (!SYSTEM_SERVICE_PACKAGENAME.equals(pPackageName)) {
                return;
            }

            // 装上了就先移除
            AdhocAppListenerManager.getInstance().removePakcageListener(this);

            //开始监听APP运行状况
            Logger.i(TAG, "listener calls back");
            deal();
        }

        @Override
        public void onPackageRemoved(String pPackageName) {
        }
    };

    public void watch(){
        Logger.i(TAG, "call watch");
        if(!AdhocRxJavaUtil.isSubscribed(mSubscription)){
            //这里延迟10S执行是为了避免异常时，狂开定时器
            mSubscription = Observable.interval(CHECK_TIME_INTERVAL, CHECK_TIME_INTERVAL, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<Long>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            Logger.e(TAG, "定时器异常：" + e.getMessage());
                            AdhocRxJavaUtil.doUnsubscribe(mSubscription);
                            mSubscription = null;
                            watch();
                        }

                        @Override
                        public void onNext(Long l) {
                            Logger.i(TAG, "begin to get apps ");
                            ISystemServiceApi systemServiceApi = SystemServiceFactory.getInstance().getSystemServiceApi();
                            if (systemServiceApi != null) {
                                try {
                                    Logger.i(TAG, "systemServiceApi not null");
                                    PackageInfo[] runningArray = systemServiceApi.getRunningAppList();
                                    PackageInfo [] installedArray = systemServiceApi.getInstalledAppList();
                                    synchronized (mListeners){
                                        for(IAppListListenner listenner : mListeners){
                                            listenner.onRetrievedAppList(installedArray, runningArray);
                                        }
                                    }
                                } catch (RemoteException e) {
                                    Logger.e(TAG, "统计APP异常：" + e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }
    }
}
