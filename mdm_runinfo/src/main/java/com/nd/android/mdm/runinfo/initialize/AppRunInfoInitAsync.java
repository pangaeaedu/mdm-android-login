package com.nd.android.mdm.runinfo.initialize;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.frame.api.initialization.AdhocAppInitAsyncAbs;
import com.nd.android.mdm.runinfo.business.task.AppRunInfoTaskModule;
import com.nd.sdp.android.serviceloader.annotation.Service;

/**
 * Created by HuangYK on 2018/12/8.
 */
@Service(AdhocAppInitAsyncAbs.class)
public class AppRunInfoInitAsync extends AdhocAppInitAsyncAbs {

    @Override
    public boolean doInitAsync() {
        AppRunInfoTaskModule.getInstance().init(AdhocBasicConfig.getInstance().getAppContext());
        return true;
    }
}
