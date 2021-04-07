package com.nd.android.aioe.device.activate.biz.operator;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.common.util.AdhocDataCheckUtils;
import com.nd.android.adhoc.basic.frame.api.initialization.AdhocExitAppManager;
import com.nd.android.adhoc.basic.frame.api.user.IAdhocLoginStatusNotifier;
import com.nd.android.adhoc.basic.frame.constant.AdhocRouteConstant;
import com.nd.android.adhoc.basic.frame.factory.AdhocFrameFactory;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.ui.activity.ActivityStackManager;
import com.nd.android.adhoc.router_api.facade.Postcard;
import com.nd.android.adhoc.router_api.facade.callback.NavCallback;
import com.nd.android.aioe.device.activate.biz.api.ActivateConfig;
import com.nd.android.aioe.device.info.config.DeviceInfoSpConfig;
import com.nd.android.aioe.device.info.util.DeviceInfoManager;
import com.nd.android.aioe.device.status.biz.api.constant.DeviceStatus;
import com.nd.android.aioe.device.status.biz.api.listener.DeviceStatusChangeManager;

class DeviceCancelOperator {

    private static final String TAG = "DeviceActivate";

    public static void cancelDevice(){
        notifyLogout();

        if (!clearData()) {
            return;
        }

        enterLogoutUI();
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

    private static boolean clearData(){
        Logger.i(TAG, "DeviceCancelOperator, clearData");

        IAdhocLoginStatusNotifier api = (IAdhocLoginStatusNotifier) AdhocFrameFactory.getInstance().getAdhocRouter()
                .build(AdhocRouteConstant.PATH_LOGIN_STATUS_NOTIFIER).navigation();
        if (api == null) {
            Logger.w(TAG, "DeviceCancelOperator, clearData failed, IAdhocLoginStatusNotifier not found");
            return false;
        }

        api.onLogout();

        // TODO：清除策略的操作，应该放在 策略模块中，通过注册 注销监听，自行完成
//        clearPolicy();

        DeviceInfoSpConfig.clearData();

        //登出的时候，不要清掉DeviceID。DeviceID只有在切换环境的时候才会被清理
        DeviceInfoManager.getInstance().resetStatusAndPushIDSubject();

        // 如果是注销的，那么这里要把 isDeleted 带上通知出去，便于外部使用
        DeviceStatus status = DeviceStatus.Init;
        status.setIsDeleted(true);

        DeviceStatusChangeManager.notifyDeviceStatus(status);
        return true;
    }


    private static void enterLogoutUI() {
        //TODO 嫩模情人杨亿万：这段代码是为了防止自动登录的情况下，后台注销会跳到账号登录页而存在，为临时策略，麻烦找机会改掉

        // TODO： 这个是原先的旧逻辑，如果自动激活的情况下就完全不跳转注销后的页面，会有问题
        //  但是如果都跳转，那么也会有问题，比如程序这时候是后台静默启动的，被注销后就会打开一个页面
        //  是否应该判断当前是否有页面在前台，如果有，则跳转，如果没有，就直接自杀应用



        if (ActivateConfig.getInstance().isAutoLogin()
                && AdhocDataCheckUtils.isCollectionEmpty(ActivityStackManager.INSTANCE.getActivityStack())) {
            Logger.i(TAG, "auto login and slient run, do not need to jump to login activity");

            ActivityStackManager.INSTANCE.closeAllActivitys();
            AdhocExitAppManager.exitApp(0);
            return;
        }

        ActivityStackManager.INSTANCE.closeAllActivitys();

        Context context = AdhocBasicConfig.getInstance().getAppContext();
        AdhocFrameFactory.getInstance().getAdhocRouter().build(AdhocRouteConstant.PATH_AFTER_LOGOUT)
                .navigation(context, new NavCallback() {
                    @Override
                    public void onInterrupt(@NonNull Postcard postcard) {
                        super.onInterrupt(postcard);
                    }

                    @Override
                    public void onLost(@NonNull Postcard postcard) {
                        super.onLost(postcard);
                    }

                    @Override
                    public void onArrival(@NonNull Postcard postcard) {
                    }
                });
    }

}
