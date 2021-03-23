package com.nd.android.mdm.wifi_sdk.business.basic.listener;

import com.nd.android.adhoc.basic.common.util.AdhocDataCheckUtils;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.mdm.wifi_sdk.business.MdmWifiInfoManager;
import com.nd.android.mdm.wifi_sdk.business.basic.constant.MdmWifiStatus;
import com.nd.android.mdm.wifi_sdk.business.bean.MdmWifiInfo;

import java.util.HashSet;
import java.util.Set;

/**
 * 监听器管理者
 * <p>
 * Created by HuangYK on 2018/3/15.
 */
public class MdmWifiListenerManager {

    private static final String TAG = "MdmWifiListenerManager";

    //    private Set<IMdmWifiConnectListener> mConnectListeners = new HashSet<>();
//    private Set<IMdmWifiStateChangeListener> mStateChangeListeners = new HashSet<>();
    private Set<IMdmWifiInfoUpdateListener> mInfoUpdateListeners = new HashSet<>();


    private Set<IMdmWifiStatusChangeListener> mStatusChangeListeners = new HashSet<>();


//    public void addConnectListener(IMdmWifiConnectListener pListener) {
//        if (pListener == null) {
//            return;
//        }
//        mConnectListeners.add(pListener);
//    }
//
//    public void removeConnectListener(IMdmWifiConnectListener pListener) {
//        mConnectListeners.remove(pListener);
//    }
//
//
//    public void addStateChangeListener(IMdmWifiStateChangeListener pListener) {
//        if (pListener == null) {
//            return;
//        }
//        mStateChangeListeners.add(pListener);
//    }
//
//    public void removeStateChangeListener(IMdmWifiStateChangeListener pListener) {
//        mStateChangeListeners.remove(pListener);
//    }

    public boolean isInfoListenerEmpty(){
        return mInfoUpdateListeners.isEmpty();
    }

    public void addInfoUpdateListener(IMdmWifiInfoUpdateListener pListener) {
        if (pListener == null) {
            return;
        }

        mInfoUpdateListeners.add(pListener);
        MdmWifiInfoManager.getInstance().starStateTimer();
    }

    public void removeInfoUpdateListener(IMdmWifiInfoUpdateListener pListener) {
        mInfoUpdateListeners.remove(pListener);

        if(mInfoUpdateListeners.isEmpty()){
            MdmWifiInfoManager.getInstance().stopStateTimer();
        }
    }

    public void addStatusChangeListener(IMdmWifiStatusChangeListener pListener) {
        if (pListener == null) {
            return;
        }

        mStatusChangeListeners.add(pListener);
    }

    public void removeStatusChangeListener(IMdmWifiStatusChangeListener pListener) {
        mStatusChangeListeners.remove(pListener);
    }

    public void noticeWifiStatusChange(MdmWifiStatus pStatus) {
        if (mStatusChangeListeners.isEmpty()) {
            return;
        }

        for (IMdmWifiStatusChangeListener listener : mStatusChangeListeners) {
            if (listener == null) {
                return;
            }
            try {
                listener.onWifiStatusChange(pStatus);
            } catch (Exception e) {
                Logger.e(TAG, "noticeWifiStatusChange error on [" + listener.getClass().getCanonicalName() + "]: " + e);
            }
        }
    }

    public void noticeInfoUpdated(MdmWifiInfo pWifiInfo) {
        if (mInfoUpdateListeners.isEmpty()) {
            return;
        }
        for (IMdmWifiInfoUpdateListener listener : mInfoUpdateListeners) {
            if (listener == null) {
                continue;
            }
            try {
                listener.onInfoUpdated(pWifiInfo);
            } catch (Exception e) {
                Logger.e(TAG, "noticeInfoUpdated error on [" + listener.getClass().getCanonicalName() + "]: " + e);
            }
        }
    }


//    public void noticeConnectStateChange(boolean isConnected) {
//        if (mConnectListeners.isEmpty()) {
//            return;
//        }
//
//        for (IMdmWifiConnectListener listener : mConnectListeners) {
//            if (listener == null) {
//                return;
//            }
//            listener.onConnectStateChange(isConnected);
//        }
//    }
//
//    public void noticeSupplicantStateChange(SupplicantState pState, WifiInfo pWifiInfo) {
//        if (mStateChangeListeners.isEmpty()) {
//            return;
//        }
//        for (IMdmWifiStateChangeListener listener : mStateChangeListeners) {
//            if (listener == null) {
//                continue;
//            }
//            listener.onSupplicantStateChange(pState, pWifiInfo);
//        }
//    }
//
//    public void noticeNetworkStateChange(NetworkInfo.DetailedState pState, WifiInfo pWifiInfo) {
//        if (mStateChangeListeners.isEmpty()) {
//            return;
//        }
//        for (IMdmWifiStateChangeListener listener : mStateChangeListeners) {
//            if (listener == null) {
//                continue;
//            }
//            listener.onNetworkStateChange(pState, pWifiInfo);
//        }
//    }

    public void release() {
//        if (!ParamUtils.isSetEmpty(mConnectListeners)) {
//            mConnectListeners.clear();
//        }
//        if (!ParamUtils.isSetEmpty(mStateChangeListeners)) {
//            mStateChangeListeners.clear();
//        }
        if (!AdhocDataCheckUtils.isCollectionEmpty(mStatusChangeListeners)) {
            mStatusChangeListeners.clear();
        }
        if (!AdhocDataCheckUtils.isCollectionEmpty(mInfoUpdateListeners)) {
            mInfoUpdateListeners.clear();
        }
    }


}
