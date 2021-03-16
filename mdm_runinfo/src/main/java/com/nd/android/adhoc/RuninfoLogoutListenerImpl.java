package com.nd.android.adhoc;

import com.nd.android.adhoc.basic.frame.api.user.IAdhocLogoutListener;
import com.nd.android.adhoc.db.operator.MdmRunInfoDbOperatorFactory;
import com.nd.android.mdm.appusage.AdhocAppUsageFactory;
import com.nd.android.mdm.runinfo.sdk.db.operator.AppRunInfoDbOperatorFactory;
import com.nd.sdp.android.serviceloader.annotation.Service;

/**
 * 用于注销时停止后台APP
 */
@Service(IAdhocLogoutListener.class)
public class RuninfoLogoutListenerImpl implements IAdhocLogoutListener {
    private static final String TAG = "RuninfoLogoutListenerImpl";

    @Override
    public void onLogout() {
        RunningAppWatchManager.getInstance().stopWatching();
        AdhocAppUsageFactory.cancel();

        AppRunInfoDbOperatorFactory.getInstance().getAppExecutionDbOperator().dropTable();
        MdmRunInfoDbOperatorFactory.getInstance().getRunInfoDbOperator().dropTable();
    }
}
