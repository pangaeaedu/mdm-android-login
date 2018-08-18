package com.nd.android.mdm.wifi_sdk.business.service.intfc;

import com.nd.android.mdm.wifi_sdk.business.basic.constant.MdmWifiBand;
import com.nd.android.mdm.wifi_sdk.business.basic.constant.MdmWifiChannel;
import com.nd.android.mdm.wifi_sdk.business.bean.MdmWifiSignalResult;

import rx.Observable;

/**
 * wifi 信号相关 业务服务接口
 * <p>
 * Created by HuangYK on 2018/3/14.
 */
public interface IMdmWifiChannelBizService {

    Observable<MdmWifiSignalResult> getMdmWifiSignalList(MdmWifiBand pWifiBand, MdmWifiChannel pWifiChannel);

}
