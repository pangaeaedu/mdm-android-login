package com.nd.android.aioe.device.activate.biz.operator;

import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.aioe.device.activate.biz.api.model.GetUserInfoModel;
import com.nd.android.aioe.device.activate.dao.api.IDeviceUserDao;
import com.nd.android.aioe.device.activate.dao.impl.DeviceActivateDaoHelper;
import com.nd.android.mdm.biz.env.MdmEvnFactory;

class UserInfoGetter {

    public static GetUserInfoModel getUserInfo(String pDeviceId, int pDeviceType) throws AdhocException {
        return getDeviceUserDao().getUserInfo(GetUserInfoModel.class, pDeviceId, pDeviceType);
    }


    private static IDeviceUserDao getDeviceUserDao() {
        return DeviceActivateDaoHelper.getDeviceUserDao(MdmEvnFactory.getInstance().getCurEnvironment().getUrl());
    }
}
