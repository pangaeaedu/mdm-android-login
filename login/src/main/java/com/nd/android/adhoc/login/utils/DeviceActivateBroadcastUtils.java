package com.nd.android.adhoc.login.utils;

import android.content.Context;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.log.Logger;

public class DeviceActivateBroadcastUtils {
    private static final String TAG = "yhq";

    public static void sendActivateSuccessBroadcast(){
        Logger.i(TAG, "sendActivateSuccessBroadcast");
        Context context = AdhocBasicConfig.getInstance().getAppContext();
        Intent intent = new Intent();
        intent.setAction("com.nd.sdp.adhoc.main.ui.login.activated");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void sendActivateFailedBroadcast(){
        Logger.i(TAG, "sendActivateFailedBroadcast");
        Context context = AdhocBasicConfig.getInstance().getAppContext();
        Intent intent = new Intent();
        intent.setAction("com.nd.sdp.adhoc.main.ui.login.activated.failed");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}
