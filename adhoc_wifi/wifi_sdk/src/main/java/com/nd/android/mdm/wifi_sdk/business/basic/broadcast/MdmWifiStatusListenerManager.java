package com.nd.android.mdm.wifi_sdk.business.basic.broadcast;

import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.util.thread.AdhocRxJavaUtil;
import com.nd.android.adhoc.basic.util.thread.AdhocThreadUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by HuangYK on 2019/3/2 0002.
 */

public class MdmWifiStatusListenerManager {

    private static final String TAG = "MdmWifiStatusListenerManager";

    private volatile static MdmWifiStatusListenerManager sInstance = null;


    private List<IMdmWifiStatusListener> mWifiStatusListener = new CopyOnWriteArrayList<>();

    public static MdmWifiStatusListenerManager getInstance() {
        if (sInstance == null) {
            synchronized (MdmWifiStatusListenerManager.class) {
                if (sInstance == null) {
                    sInstance = new MdmWifiStatusListenerManager();
                }
            }
        }
        return sInstance;
    }

    public void addListener(@NonNull IMdmWifiStatusListener pListener) {
        if (mWifiStatusListener.contains(pListener)) {
            return;
        }

        mWifiStatusListener.add(pListener);
    }

    public void removeListener(IMdmWifiStatusListener pListener) {
        mWifiStatusListener.remove(pListener);
    }

    private void doScanResultsAvailable(){
        for (IMdmWifiStatusListener listener : mWifiStatusListener) {
            try {
                listener.onScanResultsAvailable();
            } catch (Exception e) {
                Logger.w(TAG, "onScanResultsAvailable error: " + e);
            }
        }
    }

    public void onScanResultsAvailable() {
        if(AdhocThreadUtil.isOnMainThread()){
            AdhocRxJavaUtil.safeSubscribe(Observable.create(new Observable.OnSubscribe<Void>() {
                @Override
                public void call(Subscriber<? super Void> subscriber) {
                    doScanResultsAvailable();
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io()));
        }else {
            doScanResultsAvailable();
        }
    }

    private void doNetworkStateChanged(@NonNull NetworkInfo.DetailedState pState){
        for (IMdmWifiStatusListener listener : mWifiStatusListener) {
            try {
                listener.onNetworkStateChanged(pState);
            } catch (Exception e) {
                Logger.w(TAG, "onNetworkStateChanged error: " + e);
            }
        }
    }

    private void doSupplicantStateChange(@NonNull SupplicantState pState, int pErrorCode){
        for (IMdmWifiStatusListener listener : mWifiStatusListener) {
            try {
                listener.onSupplicantStateChange(pState, pErrorCode);
            } catch (Exception e) {
                Logger.w(TAG, "onSupplicantStateChange error: " + e);
            }
        }
    }

    public void onNetworkStateChanged(@NonNull final NetworkInfo.DetailedState pState) {
        if(AdhocThreadUtil.isOnMainThread()){
            AdhocRxJavaUtil.safeSubscribe(Observable.create(new Observable.OnSubscribe<Void>() {
                @Override
                public void call(Subscriber<? super Void> subscriber) {
                    doNetworkStateChanged(pState);
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io()));
        }else {
            doNetworkStateChanged(pState);
        }
    }

    public void onSupplicantStateChange(@NonNull final SupplicantState pState, final int pErrorCode) {
        if(AdhocThreadUtil.isOnMainThread()){
            AdhocRxJavaUtil.safeSubscribe(Observable.create(new Observable.OnSubscribe<Void>() {
                @Override
                public void call(Subscriber<? super Void> subscriber) {
                    doSupplicantStateChange(pState, pErrorCode);
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io()));
        }else {
            doSupplicantStateChange(pState, pErrorCode);
        }
    }
}
