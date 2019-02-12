package com.nd.android.adhoc.login;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceInfoManager;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;
import com.nd.android.adhoc.basic.frame.constant.AdhocRouteConstant;
import com.nd.android.adhoc.basic.frame.factory.AdhocFrameFactory;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.router_api.facade.Postcard;
import com.nd.android.adhoc.router_api.facade.annotation.Interceptor;
import com.nd.android.adhoc.router_api.facade.callback.InterceptorCallback;
import com.nd.android.adhoc.router_api.facade.callback.NavCallback;
import com.nd.android.adhoc.router_api.facade.template.IInterceptor;

@Interceptor(priority = 1)
public class DeviceAfterInitInterceptor implements IInterceptor {

    private static final String TAG = "DeviceAfterInitInterceptor";

    private Context mContext;

    @Override
    public void init(@NonNull Context pContext) {
        mContext = pContext;
    }

    @Override
    public void process(@NonNull Postcard pPostcard, @NonNull InterceptorCallback pCallback) {
        // 不是登录的跳转，直接返回
        if (!AdhocRouteConstant.PATH_AFTER_INIT.equals(pPostcard.getPath())) {
            pCallback.onContinue(pPostcard);
            return;
        }

        DeviceStatus status = DeviceInfoManager.getInstance().getCurrentStatus();

        if(status == DeviceStatus.Unknown || status == DeviceStatus.Enrolled){
            enterLoginUI();
        } else {
            enterAfterLoginUI();
        }

        pCallback.onInterrupt(null);
    }

    private void enterAfterLoginUI() {
        AdhocFrameFactory.getInstance().getAdhocRouter().build( AdhocRouteConstant.PATH_AFTER_LOGIN)
                .navigation(mContext, new NavCallback() {

                    @Override
                    public void onInterrupt(@NonNull Postcard postcard) {
                        super.onInterrupt(postcard);
                        Logger.w(TAG, "onInterrupt");
                    }

                    @Override
                    public void onLost(@NonNull Postcard postcard) {
                        super.onLost(postcard);
                        Logger.e(TAG, "onLost");
                    }

                    @Override
                    public void onArrival(@NonNull Postcard postcard) {

                    }
                });
    }

    protected String getLoginRoutePath(){
        return "/login/login_activity";
    }

    private void enterLoginUI() {
        AdhocFrameFactory.getInstance().getAdhocRouter().build(getLoginRoutePath())
                .navigation(mContext, new NavCallback() {

                    @Override
                    public void onInterrupt(@NonNull Postcard postcard) {
                        super.onInterrupt(postcard);
                        Logger.w(TAG, "onInterrupt");
                    }

                    @Override
                    public void onLost(@NonNull Postcard postcard) {
                        super.onLost(postcard);
                        Logger.e(TAG, "onLost");
                    }

                    @Override
                    public void onArrival(@NonNull Postcard postcard) {
                    }
                });
    }
}
