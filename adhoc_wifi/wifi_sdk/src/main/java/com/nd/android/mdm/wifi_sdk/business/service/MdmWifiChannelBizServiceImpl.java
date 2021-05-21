package com.nd.android.mdm.wifi_sdk.business.service;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import androidx.annotation.NonNull;

import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.mdm.wifi_sdk.business.MdmWifiInfoManager;
import com.nd.android.mdm.wifi_sdk.business.basic.constant.MdmWifiBand;
import com.nd.android.mdm.wifi_sdk.business.basic.constant.MdmWifiChannel;
import com.nd.android.mdm.wifi_sdk.business.bean.MdmWifiSignal;
import com.nd.android.mdm.wifi_sdk.business.bean.MdmWifiSignalResult;
import com.nd.android.mdm.wifi_sdk.business.service.intfc.IMdmWifiChannelBizService;
import com.nd.android.mdm.wifi_sdk.business.utils.MdmWifiUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;

/**
 * wifi 信号相关 业务服务接口实现
 * <p>
 * Created by HuangYK on 2018/3/14.
 */
class MdmWifiChannelBizServiceImpl implements IMdmWifiChannelBizService {

    MdmWifiChannelBizServiceImpl() {
    }

    @Override
    public Observable<MdmWifiSignalResult> getMdmWifiSignalList(@NonNull final MdmWifiBand pWifiBand, @NonNull final MdmWifiChannel pWifiChannel) {

        return Observable.create(new Observable.OnSubscribe<MdmWifiSignalResult>() {
            @Override
            public void call(Subscriber<? super MdmWifiSignalResult> subscriber) {

                int[] xLabels = null;
                if (pWifiBand == MdmWifiBand.GHZ2_4) {
                    xLabels = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
                } else if (pWifiBand == MdmWifiBand.GHZ5) {
                    xLabels = new int[pWifiChannel.getEndChannel() - pWifiChannel.getStartChannel() + 1];
                    for (int i = pWifiChannel.getStartChannel(), j = 0; i <= pWifiChannel.getEndChannel(); ++i, ++j) {
                        xLabels[j] = i;
                    }
                }

                if (xLabels == null || xLabels.length <= 0) {
                    subscriber.onError(new AdhocException("xLabels is empty"));
                    return;
                }


                WifiManager wifiManager = MdmWifiInfoManager.getInstance().getWifiManager();

                if (!wifiManager.startScan()) {
                    subscriber.onError(new AdhocException("WifiManager startScan failed"));
                    return;
                }

                List<MdmWifiSignal> signals = new ArrayList<>();
                List<ScanResult> results = wifiManager.getScanResults();

                int[] interferences = new int[xLabels.length];

                Map<Integer, Integer> interfenceMap = new HashMap<>();

                for (int i = 0; i < interferences.length; ++i) {
                    interfenceMap.put(xLabels[i], 0);
                }

                for (ScanResult result : results) {
                    if (pWifiBand == MdmWifiBand.GHZ5
                            && !isInChannelRange(MdmWifiUtils.getChannelByFrequency(result.frequency), pWifiChannel)) {
                        continue;
                    }
                    signals.add(new MdmWifiSignal(result.SSID, result.frequency, result.level));
                    int channel = MdmWifiUtils.getChannelByFrequency(result.frequency);
                    if (interfenceMap.containsKey(channel)) {
                        interfenceMap.put(channel, interfenceMap.get(channel) + 1);
                    } else {
                        interfenceMap.put(channel, 1);
                    }
                }

                for (int i = 0; i < xLabels.length; ++i) {
                    interferences[i] = interfenceMap.get(xLabels[i]);
                }

                MdmWifiSignalResult result = new MdmWifiSignalResult(signals, xLabels, interferences);
                subscriber.onNext(result);
                subscriber.onCompleted();
            }
        });

    }

    private boolean isInChannelRange(int channel, MdmWifiChannel wifiChannel) {
        boolean result = false;
        if (channel >= wifiChannel.getStartChannel() && channel <= wifiChannel.getEndChannel()) {
            result = true;
        }
        return result;
    }

}
