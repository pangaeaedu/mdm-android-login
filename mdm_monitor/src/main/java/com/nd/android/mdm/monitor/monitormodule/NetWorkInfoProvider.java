package com.nd.android.mdm.monitor.monitormodule;

import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.util.net.AdhocNetworkUtil;
import com.nd.android.mdm.monitor.info.AdhocNetworkInfo;
import com.nd.android.mdm.wifi_sdk.business.MdmWifiInfoManager;
import com.nd.android.mdm.wifi_sdk.business.bean.MdmWifiInfo;
import com.nd.android.mdm.wifi_sdk.business.bean.MdmWifiVendor;
import com.nd.eci.sdk.utils.MonitorUtil;

/**Administrator
 * @author
 * @name adhoc-101-assistant-app
 * @class name：com.nd.android.mdm.monitor.monitormodule
 * @class describe
 * @time 2021/4/27 19:47
 * @change
 * @chang time
 * @class describe
 */
public class NetWorkInfoProvider {
    private static final String TAG = "NetWorkInfoProvider";
    private static final long CACHE_INFO_VALID_PERIOD = 60 * 1000;
    private long mlLastUpdateTime;
    private AdhocNetworkInfo mNetworkInfo;
    private boolean mbNeedUpdate;

    private String mNetWorkType;
    private boolean mbGetNetWorkTypeBefore = false;

    public String getNetWorkType() {
        if(!mbGetNetWorkTypeBefore || mbNeedUpdate){
            mNetWorkType = AdhocNetworkUtil.getNetworkOperatorName();
            mbGetNetWorkTypeBefore = true;
        }
        return mNetWorkType;
    }

    public AdhocNetworkInfo getNetworkInfo(boolean bForceUpdate){
        if(bForceUpdate || null == mNetworkInfo || mbNeedUpdate
                || System.currentTimeMillis() - mlLastUpdateTime > CACHE_INFO_VALID_PERIOD){
            UpdateNetWorkInfo();
            return mNetworkInfo;
        }else {
            return mNetworkInfo;
        }
    }

    public void setNeedUpdateInfo(){
        mbNeedUpdate = true;
    }
    private void UpdateNetWorkInfo(){
        Logger.i(TAG, "update network info");
        mbNeedUpdate = false;
        MdmWifiInfo wifiInfo = MdmWifiInfoManager.getInstance().getWifiInfo();
        AdhocNetworkInfo networkInfo = new AdhocNetworkInfo();
        networkInfo.ip = wifiInfo.getIp();
        networkInfo.ssid = wifiInfo.getSsid();
        networkInfo.rssi = wifiInfo.getRssi();
        networkInfo.linkSpeed = wifiInfo.getSpeed();
        networkInfo.BSSID = wifiInfo.getApMac();
        networkInfo.apMac = wifiInfo.getApMac();
        networkInfo.mac = wifiInfo.getMac().replace(":", "");
//            networkInfo.apFactory = WifiUtils.getVendorNameByMac(mContext, networkInfo.BSSID);

        // HYK Modified on 2018-04-12
        MdmWifiVendor vendor = wifiInfo.getVendor();
        networkInfo.apFactory = vendor == null ? "" : vendor.getVendorName();

        long[] traficBytes = MonitorUtil.getTraficByte();
        networkInfo.downloadSpeed = traficBytes[3];
        networkInfo.uploadSpeed = traficBytes[2];
        mNetworkInfo = networkInfo;
        mlLastUpdateTime = System.currentTimeMillis();
    }
}
