package com.nd.android.mdm.monitor.initialize;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.frame.api.initialization.AdhocAppInitSyncAbs;
import com.nd.android.mdm.monitor.MonitorModule;
import com.nd.sdp.android.serviceloader.annotation.Service;

/**
 * Created by HuangYK on 2018/12/6.
 */
@Service(AdhocAppInitSyncAbs.class)
public class MonitorInitSyncTask extends AdhocAppInitSyncAbs {

    @Override
    public boolean doInitSync() {
        MonitorModule.getInstance().init(AdhocBasicConfig.getInstance().getAppContext());
        return true;
    }
}
