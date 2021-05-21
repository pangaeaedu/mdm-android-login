package com.nd.android.aioe.device.activate.biz.operator;

import android.content.Context;
import androidx.annotation.NonNull;

import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.router_api.facade.annotation.Route;
import com.nd.android.aioe.device.activate.biz.api.model.GetUserInfoModel;
import com.nd.android.aioe.device.activate.biz.api.provider.IDeviceUserProvider;

@Route(path = IDeviceUserProvider.ROUTE_PATH)
public class DeviceUserProviderImpl implements IDeviceUserProvider{

    @Override
    public GetUserInfoModel getUserInfo(@NonNull String pDeviceID, int pDeviceType) throws AdhocException {
        return _UserInfoGetter.getUserInfo(pDeviceID, pDeviceType);
    }

    @Override
    public String getUserId() throws AdhocException {
        return _UserInfoGetter.getUserId();
    }

    @Override
    public String getNickName() throws AdhocException {
        return _UserInfoGetter.getNickName();
    }

    @Override
    public void init(@NonNull Context pContext) {

    }
}
