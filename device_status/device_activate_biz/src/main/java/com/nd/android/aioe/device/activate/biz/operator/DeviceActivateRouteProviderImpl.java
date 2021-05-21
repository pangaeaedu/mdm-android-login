package com.nd.android.aioe.device.activate.biz.operator;

import android.content.Context;
import androidx.annotation.NonNull;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.frame.constant.AdhocRouteConstant;
import com.nd.android.adhoc.basic.frame.factory.AdhocFrameFactory;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.router_api.facade.Postcard;
import com.nd.android.adhoc.router_api.facade.annotation.Route;
import com.nd.android.adhoc.router_api.facade.callback.NavCallback;
import com.nd.android.aioe.device.activate.biz.api.constant.LoginApiRoutePathConstants;
import com.nd.android.aioe.device.activate.biz.api.provider.IDeviceActivateRouteProvider;

@Route(path = IDeviceActivateRouteProvider.ROUTE_PATH)
public class DeviceActivateRouteProviderImpl implements IDeviceActivateRouteProvider {

    private static final String TAG = "DeviceActivateRouteProviderImpl";

    @Override
    public void navigationActivateRoute() {
        AdhocFrameFactory.getInstance().getAdhocRouter()
                .build(LoginApiRoutePathConstants.PATH_LOGINAPI_LOGINUI)
                .navigation(AdhocBasicConfig.getInstance().getAppContext(), new NavCallback() {
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

    @Override
    public void navigationDeActivateRoute() {
        AdhocFrameFactory.getInstance().getAdhocRouter().build(AdhocRouteConstant.PATH_AFTER_LOGOUT)
                .navigation(AdhocBasicConfig.getInstance().getAppContext(), new NavCallback() {
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

    @Override
    public void init(@NonNull Context pContext) {

    }
}
