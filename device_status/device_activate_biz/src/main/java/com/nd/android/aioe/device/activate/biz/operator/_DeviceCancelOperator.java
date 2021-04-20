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

//        //登出的时候，不要清掉DeviceID。DeviceID只有在切换环境的时候才会被清理
//        DeviceInfoManager.getInstance().resetStatusAndPushIDSubject();

        // 如果是注销的，那么这里要把 isDeleted 带上通知出去，便于外部使用
//        if (DeviceStatusCache.getDeviceStatus() != DeviceStatus.Init) {
//
//            DeviceStatus status = DeviceStatus.Init;
//            status.setIsDeleted(true);
//            DeviceStatusChangeManager.notifyDeviceStatus(status);
//        }
    }


//    private static void enterLogoutUI() {
//        // 嫩模情人杨亿万：这段代码是为了防止自动登录的情况下，后台注销会跳到账号登录页而存在，为临时策略，麻烦找机会改掉
//
//        //  这个是原先的旧逻辑，判断当前是否有页面在前台，如果有，则跳转，如果没有，就直接自杀应用
//
//        if (ActivateConfig.getInstance().isAutoLogin()
//                && AdhocDataCheckUtils.isCollectionEmpty(ActivityStackManager.INSTANCE.getActivityStack())) {
//            Logger.i(TAG, "auto login and slient run, do not need to jump to login activity");
//
//            ActivityStackManager.INSTANCE.closeAllActivitys();
//            AdhocExitAppManager.exitApp(0);
//            return;
//        }
//
//        ActivityStackManager.INSTANCE.closeAllActivitys();
//
//        Context context = AdhocBasicConfig.getInstance().getAppContext();
//        AdhocFrameFactory.getInstance().getAdhocRouter().build(AdhocRouteConstant.PATH_AFTER_LOGOUT)
//                .navigation(context, new NavCallback() {
//                    @Override
//                    public void onInterrupt(@NonNull Postcard postcard) {
//                        super.onInterrupt(postcard);
//                    }
//
//                    @Override
//                    public void onLost(@NonNull Postcard postcard) {
//                        super.onLost(postcard);
//                    }
//
//                    @Override
//                    public void onArrival(@NonNull Postcard postcard) {
//                    }
//                });
//    }

}
