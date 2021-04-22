package com.nd.android.aioe.device.activate.biz.operator;

import com.nd.android.adhoc.basic.frame.api.user.IAdhocLoginStatusNotifier;
import com.nd.android.adhoc.basic.frame.constant.AdhocRouteConstant;
import com.nd.android.adhoc.basic.frame.factory.AdhocFrameFactory;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.aioe.device.info.config.DeviceInfoSpConfig;

class _DeviceCancelOperator {

    private static final String TAG = "DeviceActivate";

    public static void cancelDevice(){
        clearData();

        notifyLogout();
    }

    private static void notifyLogout(){

        IAdhocLoginStatusNotifier api = (IAdhocLoginStatusNotifier) AdhocFrameFactory.getInstance().getAdhocRouter()
                .build(AdhocRouteConstant.PATH_LOGIN_STATUS_NOTIFIER).navigation();
        if (api == null) {
            Logger.w(TAG, "DeviceCancelProvider, onDeviceCancel failed, IAdhocLoginStatusNotifier not found");
            return;
        }

        api.onLogout();
    }

    private static void clearData(){
        Logger.i(TAG, "DeviceCancelOperator, clearData");

        DeviceInfoSpConfig.clearData();

    }

}
