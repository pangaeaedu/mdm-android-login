package com.nd.android.adhoc.communicate.initialize;

import android.support.annotation.NonNull;
import android.util.Log;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.frame.api.initialization.AdhocAppInitPriority;
import com.nd.android.adhoc.basic.frame.api.initialization.AdhocAppInitSyncAbs;
import com.nd.android.adhoc.basic.frame.api.initialization.IAdhocInitCallback;
import com.nd.android.adhoc.communicate.impl.MdmTransferFactory;
import com.nd.eci.sdk.lib.libadhoc;
import com.nd.sdp.android.serviceloader.annotation.Service;

/**
 * Created by HuangYK on 2018/12/6.
 */
@Service(AdhocAppInitSyncAbs.class)
public class MdmTransferInitSyncTask extends AdhocAppInitSyncAbs {

    @Override
    public void doInitSync(@NonNull IAdhocInitCallback pCallback) {
        try {
            Log.e("yhq", "init Transfer lib");
            libadhoc.setContext(AdhocBasicConfig.getInstance().getAppContext());
            MdmTransferFactory.getCommunicationModule().startAdhoc();
            MdmTransferFactory.getPushModel().start();
            pCallback.onSuccess();
        } catch (Exception e) {
            pCallback.onFailed(AdhocException.newException(e));
        }

    }

    @Override
    public AdhocAppInitPriority getInitPriority() {
        return AdhocAppInitPriority.LOWEST;
    }
}
