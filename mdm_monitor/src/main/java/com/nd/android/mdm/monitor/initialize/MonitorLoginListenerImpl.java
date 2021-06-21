package com.nd.android.mdm.monitor.initialize;

import android.content.Context;
import androidx.annotation.NonNull;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.frame.api.user.IAdhocLoginInfo;
import com.nd.android.adhoc.basic.frame.api.user.IAdhocLoginListener;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.mdm.monitor.MonitorModule;
import com.nd.sdp.android.serviceloader.annotation.Service;

/**
 * Created by HuangYK on 2018/12/6.
 */
@Service(IAdhocLoginListener.class)
public class MonitorLoginListenerImpl implements IAdhocLoginListener {

    private static final String TAG = "MonitorLoginListenerImpl";

    @Override
    public void onLogin(@NonNull IAdhocLoginInfo pLoginInfo) {
        try {
            Context context = AdhocBasicConfig.getInstance().getAppContext();
            MonitorModule.getInstance().init(context);
        } catch (Exception e) {
            Logger.e(TAG, "onLogin exception:" + e);
        }
    }
}
