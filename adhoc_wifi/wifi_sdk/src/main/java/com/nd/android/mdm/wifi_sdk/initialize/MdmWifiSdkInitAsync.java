package com.nd.android.mdm.wifi_sdk.initialize;

import com.nd.android.adhoc.basic.frame.api.initialization.AdhocAppInitAsyncAbs;
import com.nd.android.mdm.wifi_sdk.business.MdmWifiInfoManager;
import com.nd.sdp.android.serviceloader.annotation.Service;

/**
 * Created by HuangYK on 2018/12/8.
 */
@Service(AdhocAppInitAsyncAbs.class)
public class MdmWifiSdkInitAsync extends AdhocAppInitAsyncAbs {

    @Override
    public boolean doInitAsync() {
        MdmWifiInfoManager.getInstance().start();
        return true;
    }
}
