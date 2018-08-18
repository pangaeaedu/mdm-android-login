package com.nd.android.mdm.wifi_sdk.business.service;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.common.util.AdhocDataCheckUtils;
import com.nd.android.mdm.wifi_sdk.business.MdmWifiInfoManager;
import com.nd.android.mdm.wifi_sdk.business.bean.MdmWifiItemInfo;
import com.nd.android.mdm.wifi_sdk.business.bean.MdmWifiPwd;
import com.nd.android.mdm.wifi_sdk.business.service.intfc.IMdmWifiOperateBizService;
import com.nd.android.mdm.wifi_sdk.sdk.dataService.MdmWifiDataServiceFactory;
import com.nd.android.mdm.wifi_sdk.sdk.dataService.db.entity.intfc.IMdmWifiPwdEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by HuangYK on 2018/3/19.
 */

public class MdmWifiOperateBizServiceImpl implements IMdmWifiOperateBizService {

    //    private static final String PSK = "PSK";
    private static final String WEP = "WEP";
    //    private static final String EAP = "EAP";
    private static final String WPA = "WPA";
    private static final String OPEN = "Open";

    @Override
    public Observable<List<MdmWifiItemInfo>> getWifiItemList() {


        return Observable.create(new Observable.OnSubscribe<List<MdmWifiItemInfo>>() {
            @Override
            public void call(Subscriber<? super List<MdmWifiItemInfo>> subscriber) {

                WifiManager wifiManager = MdmWifiInfoManager.getInstance().getWifiManager();
                if (!wifiManager.startScan()) {
                    subscriber.onError(new AdhocException("WifiManager startScan failed"));
                    return;
                }

                List<ScanResult> resultList = wifiManager.getScanResults();

                if (AdhocDataCheckUtils.isCollectionEmpty(resultList)) {
                    subscriber.onNext(null);
                } else {
                    List<MdmWifiItemInfo> itemInfoList = new ArrayList<>();
                    for (ScanResult result : resultList) {
                        if (result == null) {
                            continue;
                        }
                        MdmWifiItemInfo itemInfo = new MdmWifiItemInfo();
                        itemInfo.setScanResult(result);

                        IMdmWifiPwdEntity pwdEntity =
                                MdmWifiDataServiceFactory.getInstance().getMdmWifiPwdDbService().getWifiPwEntity(result.SSID);
                        if (pwdEntity != null) {
                            itemInfo.setWifiPwd(new MdmWifiPwd(pwdEntity));
                        }
                        itemInfoList.add(itemInfo);
                    }
                    subscriber.onNext(itemInfoList);
                }


                subscriber.onCompleted();
            }
        }).map(new Func1<List<MdmWifiItemInfo>, List<MdmWifiItemInfo>>() {
            @Override
            public List<MdmWifiItemInfo> call(List<MdmWifiItemInfo> mdmWifiItemInfos) {
                // 去重过滤，如果遇到有重复的情况，则保留信号更好的那个
                List<MdmWifiItemInfo> results = new ArrayList<>();
                Map<String, MdmWifiItemInfo> map = new HashMap<>();
                if(mdmWifiItemInfos.size()>0) {
                    for (MdmWifiItemInfo itemInfo : mdmWifiItemInfos) {
                        final String SSID = itemInfo.getScanResult().SSID;
                        if (!map.containsKey(SSID)) {
                            map.put(SSID, itemInfo);
                        } else {
                            MdmWifiItemInfo origin = map.get(SSID);
                            if (itemInfo.getScanResult().level > origin.getScanResult().level) {
                                map.put(SSID, itemInfo);
                            }
                        }
                    }

                    for (Map.Entry<String, MdmWifiItemInfo> entry : map.entrySet()) {
                        results.add(entry.getValue());
                    }
                }
                return results;
            }
        });
    }

    @Override
    public Observable<Boolean> connectWifi(final MdmWifiItemInfo pItemInfo) {
        if (pItemInfo == null || pItemInfo.getWifiPwd() == null) {
            return Observable.just(false);
        }

        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {

                MdmWifiPwd wifiPwd = pItemInfo.getWifiPwd();

                WifiManager wifiManager = MdmWifiInfoManager.getInstance().getWifiManager();

                WifiConfiguration configuration=isWifiConfigureList( wifiPwd.getSsid(),wifiManager);

                boolean result = false;

                if(configuration==null){
                    configuration = new WifiConfiguration();
                    String securityType = getScanResultSecurity(pItemInfo.getScanResult().capabilities);
                    configure(configuration, wifiPwd.getSsid(), wifiPwd.getPwd(), securityType);
                    int networkId = wifiManager.addNetwork(configuration);
                    if (networkId != -1) {
                        result = wifiManager.enableNetwork(networkId, true);
                    }
                }else{
                    result = wifiManager.enableNetwork(configuration.networkId, true);
                }
                subscriber.onNext(result);
                subscriber.onCompleted();
            }
        });
    }

    /**
     * 验证是否在已有的WIFI列表中 针对6.0处理
     * @param SSID
     * @param wifiManager
     * @return
     */
    private WifiConfiguration isWifiConfigureList(String SSID,WifiManager wifiManager) {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    private String getScanResultSecurity(String pCapabilities) {
        if (pCapabilities.contains(WPA)) {
            return WPA;
        }
        if (pCapabilities.contains(WEP)) {
            return WEP;
        }
        return OPEN;
    }


    private void configure(WifiConfiguration wfc, String ssid, String password, String securityType) {

        wfc.SSID = "\"".concat(ssid).concat("\"");
        wfc.status = WifiConfiguration.Status.DISABLED;
        wfc.priority = 40;

        if (securityType.equals(WPA)) {
            wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

            wfc.preSharedKey = "\"".concat(password).concat("\"");
        } else if (securityType.equals(WEP)) {
            wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

            wfc.wepKeys[0] = "\"".concat(password).concat("\"");
            wfc.wepTxKeyIndex = 0;

        } else if (securityType.equals(OPEN)) {
            wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wfc.allowedAuthAlgorithms.clear();
            wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        }
    }

}











