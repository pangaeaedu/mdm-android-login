package com.nd.android.aioe.device.activate.biz.api.provider;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.router_api.facade.template.IProvider;
import com.nd.android.aioe.device.activate.biz.api.model.GetUserInfoModel;

public interface IDeviceUserProvider extends IProvider {

    String ROUTE_PATH = "cmp_device_activate_biz/user_provider";

    GetUserInfoModel getUserInfo(@NonNull String pDeviceID, int pDeviceType) throws AdhocException;
}
