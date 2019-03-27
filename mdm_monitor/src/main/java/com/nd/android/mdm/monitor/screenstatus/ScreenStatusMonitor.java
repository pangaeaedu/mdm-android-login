package com.nd.android.mdm.monitor.screenstatus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.command.basic.response.ResponseBase;
import com.nd.android.adhoc.communicate.constant.AdhocCmdFromTo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Created by HuangYK on 2019/3/18 0018.
 */

public class ScreenStatusMonitor {

    private static final String TAG = "ScreenStatusMonitor";

    private volatile static ScreenStatusMonitor sInstance = null;

    public static ScreenStatusMonitor getInstance() {
        if (sInstance == null) {
            synchronized (ScreenStatusMonitor.class) {
                if (sInstance == null) {
                    sInstance = new ScreenStatusMonitor();
                }
            }
        }
        return sInstance;
    }

    public void startMonitor(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        AdhocBasicConfig.getInstance().getAppContext().registerReceiver(mScreenStatus, filter);
    }

    private BroadcastReceiver mScreenStatus = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }

            int screenStatus = -1;  // 0：熄屏，1：亮屏
            switch (action) {
                case Intent.ACTION_SCREEN_OFF:
                    screenStatus = 0;
                    break;
                case Intent.ACTION_USER_PRESENT:
                    screenStatus = 1;
                    break;
            }

            Logger.e(TAG, "screen status receiver: screenStatus = " + screenStatus);

            if (screenStatus == -1) {
                return;
            }

            try {
                ResponseBase responseBase = new ResponseBase("light",
                        UUID.randomUUID().toString(),
                        AdhocCmdFromTo.MDM_CMD_DRM.getValue(),
                        "",
                        System.currentTimeMillis());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("status", screenStatus);
                responseBase.setJsonData(jsonObject);
                responseBase.postAsync();
            } catch (JSONException e) {
                Logger.e(TAG, "response screen status error: " + e);
            }
        }
    };
}
