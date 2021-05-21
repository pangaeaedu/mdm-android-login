package com.nd.android.mdm.monitor.initialize;

import android.content.Context;
import androidx.annotation.NonNull;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.frame.api.initialization.AdhocAppInitPriority;
import com.nd.android.adhoc.basic.frame.api.initialization.AdhocAppInitSyncAbs;
import com.nd.android.adhoc.basic.frame.api.initialization.IAdhocInitCallback;
import com.nd.android.mdm.monitor.MonitorModule;

/**
 * Created by HuangYK on 2018/12/6.
 */
//@Service(AdhocAppInitSyncAbs.class)
@Deprecated
public class MonitorInitSyncTask extends AdhocAppInitSyncAbs {

    @Override
    public void doInitSync(@NonNull IAdhocInitCallback pCallback) {
        try {
            Context context = AdhocBasicConfig.getInstance().getAppContext();

//            Intent intent = new Intent(context, AssistantService.class);
//            context.startService(intent);

//            DaemonModule.getInstance().init(context);
            MonitorModule.getInstance().init(context);

//            SystemControFactory.getInstance().init();
            pCallback.onSuccess();
        } catch (Exception e) {
            pCallback.onFailed(AdhocException.newException(e));
        }
    }

    @Override
    public AdhocAppInitPriority getInitPriority() {
        return AdhocAppInitPriority.MEDIUM;
    }
}
