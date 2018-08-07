package com.nd.android.adhoc.communicate.utils;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;

public class BroadcastUtil {

    public static boolean mStartAutoLink = true;

    private static final String ACTION_SEND_LOG = "com.nd.pad.demo.ACTION_SEND_LOG";

    private static final String EXTRA_LOG = "extra_log";


    public static void sendLogBroadcast(String log) {
        String formatLog = log;
        Intent intent = new Intent(ACTION_SEND_LOG);
        intent.putExtra(EXTRA_LOG, formatLog);

        LocalBroadcastManager.getInstance(AdhocBasicConfig.getInstance().getAppContext()).sendBroadcastSync(intent);
    }
}
