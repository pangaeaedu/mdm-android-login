package com.nd.android.mdm.wifi_sdk.business.basic.listener;

import com.nd.android.mdm.wifi_sdk.business.basic.constant.MdmWifiStatus;

/**
 * wifi 状态变更监听
 * <p>
 * Created by HuangYK on 2018/3/19.
 */
public interface IMdmWifiStatusChangeListener {

    /**
     * wifi 状态变更
     *
     * @param pStatus 当前 wifi 状态
     */
    void onWifiStatusChange(MdmWifiStatus pStatus);
}
