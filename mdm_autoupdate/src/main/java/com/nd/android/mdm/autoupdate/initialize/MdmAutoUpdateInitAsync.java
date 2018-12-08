package com.nd.android.mdm.autoupdate.initialize;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.frame.api.initialization.AdhocAppInitAsyncAbs;
import com.nd.android.mdm.autoupdate.AutoUpdateModule;
import com.nd.sdp.android.serviceloader.annotation.Service;

/**
 * Created by HuangYK on 2018/12/8.
 */
@Service(AdhocAppInitAsyncAbs.class)
public class MdmAutoUpdateInitAsync extends AdhocAppInitAsyncAbs {

    @Override
    public boolean doInitAsync() {
        AutoUpdateModule.getInstance().init(AdhocBasicConfig.getInstance().getAppContext());
        return false;
    }
}
