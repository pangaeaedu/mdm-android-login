package com.nd.android.adhoc.communicate.initialize;

import com.nd.android.adhoc.basic.frame.api.initialization.AdhocAppInitSyncAbs;
import com.nd.android.adhoc.communicate.impl.MdmTransferFactory;
import com.nd.sdp.android.serviceloader.annotation.Service;

/**
 * Created by HuangYK on 2018/12/6.
 */
@Service(AdhocAppInitSyncAbs.class)
public class MdmTransferInitSyncTask extends AdhocAppInitSyncAbs {

    @Override
    public boolean doInitSync() {
        MdmTransferFactory.getCommunicationModule().startAdhoc();
        MdmTransferFactory.getPushModel().start();
        return true;
    }
}
