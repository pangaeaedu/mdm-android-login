package com.nd.android.adhoc.login.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;

public class DeviceActivateBroadcastUtils {
    private static final String TAG = "yhq";

    public static void sendActivateSuccessBroadcast(){
        Log.e(TAG, "sendActivateSuccessBroadcast: com.nd.sdp.adhoc.main.ui.login.activated");
        Context context = AdhocBasicConfig.getInstance().getAppContext();
        Intent intent = new Intent();
        intent.setAction("com.nd.sdp.adhoc.main.ui.login.activated");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void sendActivateFailedBroadcast(){
        Log.e(TAG, "sendActivateFailedBroadcast: com.nd.sdp.adhoc.main.ui.login.activated.failed");
        Context context = AdhocBasicConfig.getInstance().getAppContext();
        Intent intent = new Intent();
        intent.setAction("com.nd.sdp.adhoc.main.ui.login.activated.failed");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}
