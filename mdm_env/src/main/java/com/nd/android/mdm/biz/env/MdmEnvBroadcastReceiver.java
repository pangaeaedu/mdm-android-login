package com.nd.android.mdm.biz.env;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

public class MdmEnvBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_NAME = "com.nd.mdm.env.switch";
    public static final String ENV_VALUE = "env_value";

    private static final String TAG = "env_broadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "MdmEnvBroadcastReceiver");
        if(TextUtils.isEmpty(intent.getAction())){
            return;
        }

        if(!intent.getAction().equalsIgnoreCase(ACTION_NAME)){
            return;
        }

        int value = intent.getIntExtra(ENV_VALUE, 1);
        int curEnvValue = MdmEvnFactory.getInstance().getCurIndex();
        if(value == curEnvValue){
            return;
        }

        Log.e(TAG, "new value:"+value+" pre value:"+curEnvValue);
        MdmEvnFactory.getInstance().setCurEnvironment(value);
    }
}
