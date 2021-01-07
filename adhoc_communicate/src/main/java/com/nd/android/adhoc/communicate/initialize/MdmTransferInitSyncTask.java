package com.nd.android.adhoc.communicate.initialize;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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
            MdmTransferFactory.getPushModel().start();

            Context context = AdhocBasicConfig.getInstance().getAppContext();
            try {
                ApplicationInfo appInfo = context.getPackageManager()
                        .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                boolean excludeAdhoc = appInfo.metaData.getBoolean("EXCLUDE_ADHOC");
                Log.i("yhq", "doInitSync: exclude adhoc = " + excludeAdhoc);
                if (!excludeAdhoc) {
                    libadhoc.setContext(context);
                    MdmTransferFactory.getCommunicationModule().startAdhoc();
                }
            } catch (Exception pE) {
                pE.printStackTrace();
                MdmTransferFactory.getCommunicationModule().startAdhoc();
            }

            pCallback.onSuccess();
        } catch (Exception e) {
            pCallback.onFailed(AdhocException.newException(e));
        }

    }

    @Override
    public AdhocAppInitPriority getInitPriority() {
        return AdhocAppInitPriority.HEIGHTEST;
    }
}
