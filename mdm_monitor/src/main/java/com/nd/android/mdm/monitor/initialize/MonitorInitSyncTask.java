package com.nd.android.mdm.monitor.initialize;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.frame.api.initialization.AdhocAppInitSyncAbs;
import com.nd.android.adhoc.basic.frame.api.initialization.IAdhocInitCallback;
import com.nd.android.mdm.monitor.DaemonModule;
import com.nd.android.mdm.monitor.MonitorModule;
import com.nd.pad.net.AssistantService;
import com.nd.sdp.android.serviceloader.annotation.Service;

/**
 * Created by HuangYK on 2018/12/6.
 */
@Service(AdhocAppInitSyncAbs.class)
public class MonitorInitSyncTask extends AdhocAppInitSyncAbs {

    @Override
    public void doInitSync(@NonNull IAdhocInitCallback pCallback) {
        try {
            Context context = AdhocBasicConfig.getInstance().getAppContext();

            Intent intent = new Intent(context, AssistantService.class);
            context.startService(intent);

            DaemonModule.getInstance().init(context);
            MonitorModule.getInstance().init(context);
            pCallback.onSuccess();
        } catch (Exception e) {
            pCallback.onFailed(AdhocException.newException(e));
        }
    }
}
