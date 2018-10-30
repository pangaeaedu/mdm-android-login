package com.nd.android.mdm.wifi_sdk.business;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.util.net.AdhocNetworkIpUtil;
import com.nd.android.adhoc.basic.util.system.AdhocDeviceUtil;
import com.nd.android.adhoc.basic.util.thread.AdhocRxJavaUtil;
import com.nd.android.mdm.wifi_sdk.business.basic.broadcast.IMdmWifiStatusReceiverCallback;
import com.nd.android.mdm.wifi_sdk.business.basic.broadcast.MdmWifiStatusReceiver;
import com.nd.android.mdm.wifi_sdk.business.basic.constant.MdmWifiStatus;
import com.nd.android.mdm.wifi_sdk.business.basic.listener.MdmWifiListenerManager;
import com.nd.android.mdm.wifi_sdk.business.bean.MdmWifiInfo;
import com.nd.android.mdm.wifi_sdk.business.bean.MdmWifiVendor;
import com.nd.android.mdm.wifi_sdk.business.service.MdmWifiBizServiceFactory;
import com.nd.android.mdm.wifi_sdk.business.utils.MdmWifiUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.subjects.PublishSubject;


/**
 * MDM wifi 信息管理类
 * <p>
 * Created by HuangYK on 2018/3/14.
 */
public final class MdmWifiInfoManager {

    private static final String TAG = "MdmWifiInfoManager";

    private static final long sNotifyStateChangeDuration = 1;
    private static final long sUpdateWifiInfoDuration = 3;


    private static final int LEVEL_NUMBER = 4;

    private volatile static MdmWifiInfoManager sInstance = null;

    private final MdmWifiInfo mWifiInfo = new MdmWifiInfo();
    private AtomicBoolean mIsWiFiConnected = new AtomicBoolean(false);
    private AtomicBoolean mIsKeepRun = new AtomicBoolean(false);


    private WifiManager mWifiManager;
    private MdmWifiListenerManager mWifiListenerManager;


    private AtomicBoolean mIsKeepTimer = new AtomicBoolean(true);
    private Subscription mStateTimerSub;

    private Subscription mGetVendorInfo;

    private MdmWifiStatusReceiver mWifiStatusReceiver;


    private Subscription mInitWifiInfoSub;
    private Subscription mScanResultsSub;
    private Subscription mNetworkStateSub;
    private Subscription mSupplicantStateSub;

    private PublishSubject<Void> mScanResultsPublish = PublishSubject.create();
    private PublishSubject<NetworkInfo.DetailedState> mNetworkStatePublish = PublishSubject.create();
    private PublishSubject<SupplicantState> mSupplicantStatePublish = PublishSubject.create();


    @SuppressWarnings("FieldCanBeLocal")
    private IMdmWifiStatusReceiverCallback mReceiverCallback = new IMdmWifiStatusReceiverCallback() {
        @Override
        public void onScanResultsAvailable() {
            mScanResultsPublish.onNext(null);
        }

        @Override
        public void onNetworkStateChanged(@NonNull NetworkInfo.DetailedState pState) {
            mNetworkStatePublish.onNext(pState);
        }

        @Override
        public void onSupplicantStateChange(@NonNull SupplicantState pState, int pErrorCode) {
            mSupplicantStatePublish.onNext(pState);
            if (pErrorCode == WifiManager.ERROR_AUTHENTICATING) {
                mNetworkStatePublish.onNext(NetworkInfo.DetailedState.FAILED);
            }
        }
    };

    public static MdmWifiInfoManager getInstance() {
        if (sInstance == null) {
            synchronized (MdmWifiInfoManager.class) {
                if (sInstance == null) {
                    sInstance = new MdmWifiInfoManager();
                }
            }
        }
        return sInstance;
    }

    private MdmWifiInfoManager() {
        mWifiListenerManager = new MdmWifiListenerManager();
        mIsWiFiConnected.set(isWifiConnected());

        initWifiManager();

        initNetworkStateSub();
        initSupplicantStateSub();
        mWifiStatusReceiver = new MdmWifiStatusReceiver(mReceiverCallback);
        mWifiStatusReceiver.registerReceiver();

        initScanResultsSub();
    }

    @SuppressLint("WifiManagerLeak")
    private void initWifiManager(){
        if (mWifiManager == null) {
            mWifiManager = (WifiManager) AdhocBasicConfig.getInstance().getAppContext().getSystemService(Context.WIFI_SERVICE);
        }
    }

    public WifiManager getWifiManager() {
        initWifiManager();
        return mWifiManager;
    }

    /**
     * 获取 wifi 监听管理器
     *
     * @return MdmWifiListenerManager
     */
    public MdmWifiListenerManager getWifiListenerManager() {
        return mWifiListenerManager;
    }


    public void start() {
        mIsKeepRun.set(true);
        if (mIsWiFiConnected.get()) {
            updateCurWifiInfo();
            updateVendorInfo();
        }
    }

    public void stop() {
        mIsKeepRun.set(true);
        stopStateTimer();
    }

    public boolean getIsWiFiConnected() {
        return mIsWiFiConnected.get();
    }

    @NonNull
    public MdmWifiInfo getWifiInfo() {
        return mWifiInfo;
    }

    /**
     * 回收
     */
    public void release() {
        if (mWifiStatusReceiver != null) {
            mWifiStatusReceiver.unregisterReceiver();
        }
        if (mWifiListenerManager != null) {
            mWifiListenerManager.release();
        }

        stopStateTimer();
        AdhocRxJavaUtil.doUnsubscribe(mGetVendorInfo);

        AdhocRxJavaUtil.doUnsubscribe(mInitWifiInfoSub);
        AdhocRxJavaUtil.doUnsubscribe(mNetworkStateSub);
        AdhocRxJavaUtil.doUnsubscribe(mScanResultsSub);
        AdhocRxJavaUtil.doUnsubscribe(mSupplicantStateSub);
    }

    private void initScanResultsSub() {
        if(AdhocRxJavaUtil.isSubscribed(mScanResultsSub)){
            return;
        }
        mScanResultsSub =
                mScanResultsPublish.asObservable().throttleLast(sNotifyStateChangeDuration, TimeUnit.SECONDS)
                        .map(new Func1<Void, ScanResult>() {
                            @Override
                            public ScanResult call(Void aVoid) {
                                List<ScanResult> results = mWifiManager.getScanResults();
                                for (ScanResult result : results) {
                                    if (result.BSSID.equals(mWifiInfo.getApMac())) {
                                        return result;
                                    }
                                }
                                return null;
                            }
                        }).compose(AdhocRxJavaUtil.<ScanResult>applyDefaultSchedulers())
                        .subscribe(new Subscriber<ScanResult>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable throwable) {
                                Logger.w(TAG, "ScanResultsSub onError: " + throwable.toString());
                                AdhocRxJavaUtil.doUnsubscribe(mScanResultsSub);
                                initScanResultsSub();
                            }

                            @Override
                            public void onNext(ScanResult scanResult) {
                                if (scanResult == null) {
                                    return;
                                }
                                mWifiInfo.setChannel(MdmWifiUtils.getChannelByFrequency(scanResult.frequency));
                                starStateTimer();
                            }
                        });
    }

    /**
     * 处理网络状态变更
     */
    private void initNetworkStateSub() {
        if(AdhocRxJavaUtil.isSubscribed(mNetworkStateSub)){
            return;
        }

        mNetworkStateSub = mNetworkStatePublish.asObservable().throttleLast(sNotifyStateChangeDuration, TimeUnit.SECONDS)
                .compose(AdhocRxJavaUtil.<NetworkInfo.DetailedState>applyDefaultSchedulers())
                .subscribe(new Subscriber<NetworkInfo.DetailedState>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.w(TAG, "NetworkStateSub onError: " + e.toString());
                        AdhocRxJavaUtil.doUnsubscribe(mNetworkStateSub);
                        initNetworkStateSub();
                    }

                    @Override
                    public void onNext(NetworkInfo.DetailedState detailedState) {
                        switch (detailedState) {
                            case CONNECTED:
                                mIsWiFiConnected.set(true);
                                updateCurWifiInfo();
                                updateVendorInfo();
                                mWifiListenerManager.noticeWifiStatusChange(MdmWifiStatus.CONNECTED);
                                break;
                            case DISCONNECTED:
                                mIsWiFiConnected.set(false);
                                mWifiListenerManager.noticeWifiStatusChange(MdmWifiStatus.DISCONNECT);
                                resetConnectedInfo();
                            case SCANNING:

                            case CONNECTING:
                                mWifiListenerManager.noticeWifiStatusChange(MdmWifiStatus.CONNECTING);
                                updateCurWifiInfo();
                            case AUTHENTICATING:
                                mWifiListenerManager.noticeWifiStatusChange(MdmWifiStatus.AUTHENTICATING);
                            case OBTAINING_IPADDR:
                                mWifiListenerManager.noticeWifiStatusChange(MdmWifiStatus.OBTAINING_IPADDR);
                            case FAILED:
                            case CAPTIVE_PORTAL_CHECK:
                                mWifiListenerManager.noticeWifiStatusChange(MdmWifiStatus.CAPTIVE_PORTAL_CHECK);
                                mIsWiFiConnected.set(false);
                                stopStateTimer();
                                resetConnectedInfo();
                                break;
                            default:
                                stopStateTimer();
                                break;
                        }
                    }
                });
    }

    private void updateCurWifiInfo() {
        AdhocRxJavaUtil.doUnsubscribe(mInitWifiInfoSub);
        mInitWifiInfoSub = Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
//                if (!mWifiInfo.isVaild()) {
                    subscriber.onNext(updateWifiInfo());
                    subscriber.onCompleted();
//                }
            }
        })
                .compose(AdhocRxJavaUtil.<Boolean>applyDefaultSchedulers())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.w(TAG, "updateCurWifiInfo onError: " + e.toString());
                    }

                    @Override
                    public void onNext(Boolean aVoid) {
                        updateConnectedInfo();
                    }
                });
    }

    private void updateVendorInfo() {
        // 获取厂商信息
        // 原来这里先异步调用 RX去获取 Vendor 的信息，然后直接就去通知界面更新了，可能会有问题
        AdhocRxJavaUtil.doUnsubscribe(mGetVendorInfo);

        mGetVendorInfo = MdmWifiBizServiceFactory.getInstance().getMdmWifiVendorBizService()
                .getWifiVendor(mWifiManager.getConnectionInfo().getBSSID())
                .filter(new Func1<MdmWifiVendor, Boolean>() {
                    @Override
                    public Boolean call(MdmWifiVendor mdmWifiVendor) {
                        // 过滤条件，用于判断是否需要更新 并且通知 厂商信息变更
                        if (mdmWifiVendor == null) {
                            return false;
                        }
                        MdmWifiVendor oldVender = mWifiInfo.getVendor();
                        return oldVender == null || !mdmWifiVendor.equals(oldVender);
                    }
                })
                .compose(AdhocRxJavaUtil.<MdmWifiVendor>applyDefaultSchedulers())
                .subscribe(new Subscriber<MdmWifiVendor>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(MdmWifiVendor vendor) {
                        starStateTimer();
                        mWifiInfo.setVendor(vendor);
                        notifyInfoupdated();
                    }
                });
    }


    private void updateConnectedInfo() {
        notifyInfoupdated();

        mIsKeepTimer.set(false);
        if (mIsKeepRun.get() && mIsWiFiConnected.get()) {
            if (mWifiInfo.getChannel() <= 0) {
                //  诊断当前wifi所在信道是否改變, 不需要持續的 scan WiFi, 當 Ap 改變信道時, Wifi 會走重新連接流程, 並重新诊断当前wifi所在信道
                //  初始WiFi 連接時強制調用一次, 取得信道訊息
                mWifiManager.startScan();
            }
            mIsKeepTimer.set(true);
        }
    }

    private boolean updateWifiInfo() {

        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        if (wifiInfo == null) {
            return false;
        }

        DhcpInfo dhcpInfo = mWifiManager.getDhcpInfo();

        final int signalLevel = WifiManager.calculateSignalLevel(mWifiInfo.getRssi(), LEVEL_NUMBER);

        final String ssid = MdmWifiUtils.removeDoubleQuotes(wifiInfo.getSSID());
        final int rssi = wifiInfo.getRssi();
        final String bssid = wifiInfo.getBSSID();
        final String mac = AdhocDeviceUtil.getWifiMac(AdhocBasicConfig.getInstance()
                .getAppContext()); //wifiInfo.getMacAddress();
        final int speed = wifiInfo.getLinkSpeed();
        final String ip = AdhocNetworkIpUtil.getCurrentIp(AdhocBasicConfig.getInstance().getAppContext());
        final String dns = dhcpInfo == null ? "" : AdhocNetworkIpUtil.formatIpAddress(dhcpInfo.dns1);
        final String gateway = dhcpInfo == null ? "" : AdhocNetworkIpUtil.formatIpAddress(dhcpInfo.gateway);

        synchronized (mWifiInfo) {

            if (mWifiInfo.getIp().equals(ip)
                    && mWifiInfo.getSsid().equals(ssid)
                    && mWifiInfo.getRssi() == rssi
                    && mWifiInfo.getApMac().equals(bssid)
                    && mWifiInfo.getMac().equals(mac)
                    && mWifiInfo.getSpeed() == speed
                    && mWifiInfo.getDns().equals(dns)
                    && mWifiInfo.getGateway().equals(gateway)
                    && mWifiInfo.getSignalLevel() == signalLevel
                    ) {
                return false;
            }

            mWifiInfo.setSsid(ssid);
            mWifiInfo.setRssi(rssi);
            mWifiInfo.setApMac(bssid);
            mWifiInfo.setMac(mac);
            mWifiInfo.setSpeed(speed);
            mWifiInfo.setIp(ip);
            mWifiInfo.setDns(dns);
            mWifiInfo.setGateway(gateway);
            mWifiInfo.setSignalLevel(signalLevel);

        }

        Logger.d(TAG, "after updateWifiInfo: "
                + "IP = " + mWifiInfo.getIp()
                + ", SSID = " + mWifiInfo.getSsid()
                + ", APMAC = " + mWifiInfo.getApMac()
        );
        return true;
    }

    private void notifyInfoupdated() {
        if (mIsKeepRun.get()) {
//            EventBus.getDefault().post(new WiFiInfoUpdateEvent());
            mWifiListenerManager.noticeInfoUpdated(mWifiInfo);
        }
    }

    /**
     * 处理客户端网络连接状态变更
     */
    private void initSupplicantStateSub() {
        AdhocRxJavaUtil.doUnsubscribe(mSupplicantStateSub);
        mSupplicantStateSub = mSupplicantStatePublish.asObservable().throttleLast(sNotifyStateChangeDuration, TimeUnit.SECONDS)
                .compose(AdhocRxJavaUtil.<SupplicantState>applyDefaultSchedulers())
                .subscribe(new Subscriber<SupplicantState>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.w(TAG, "SupplicantStateSub onError: " + e.toString());
                        initSupplicantStateSub();
                    }

                    @Override
                    public void onNext(SupplicantState supplicantState) {
                        switch (supplicantState) {
                            case SCANNING:
                            case ASSOCIATING:
                            case ASSOCIATED:
                                // WifiListFragment WifiAssistantActivity 中接收這個事件
                                mWifiListenerManager.noticeWifiStatusChange(MdmWifiStatus.CONNECTING);
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private void resetConnectedInfo() {
        if (!mIsWiFiConnected.get()) {
            mWifiInfo.reset();
            notifyInfoupdated();
        }
    }

    private void starStateTimer() {
        if (AdhocRxJavaUtil.isSubscribed(mStateTimerSub)) {
            return;
        }
        mStateTimerSub = Observable.interval(sUpdateWifiInfoDuration, TimeUnit.SECONDS)
                .filter(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        return mIsKeepTimer.get();
                    }
                }).filter(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        return updateWifiInfo();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(TAG, "Run StateTimer error: " + e);
                        AdhocRxJavaUtil.doUnsubscribe(mStateTimerSub);
                        starStateTimer();
                    }

                    @Override
                    public void onNext(Long aLong) {
                        updateConnectedInfo();
                    }
                });
    }

    private void stopStateTimer() {
        AdhocRxJavaUtil.doUnsubscribe(mStateTimerSub);
    }


    private boolean isWifiConnected() {
        ConnectivityManager connManager =
                (ConnectivityManager) AdhocBasicConfig.getInstance().getAppContext()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager == null) {
            return false;
        }
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

}
