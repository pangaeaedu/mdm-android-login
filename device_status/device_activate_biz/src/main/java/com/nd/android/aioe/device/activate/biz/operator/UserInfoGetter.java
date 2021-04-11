package com.nd.android.aioe.device.activate.biz.operator;

import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.aioe.device.activate.biz.api.model.GetUserInfoModel;
import com.nd.android.aioe.device.activate.dao.api.IDeviceUserDao;
import com.nd.android.aioe.device.activate.dao.impl.DeviceActivateDaoHelper;
import com.nd.android.aioe.device.info.cache.DeviceIdCache;
import com.nd.android.aioe.device.info.config.DeviceInfoSpConfig;
import com.nd.android.aioe.device.status.dao.impl.constant.DeviceType;
import com.nd.android.mdm.biz.env.MdmEvnFactory;

class UserInfoGetter {

    public static GetUserInfoModel getUserInfo(String pDeviceId, int pDeviceType) throws AdhocException {
        return getDeviceUserDao().getUserInfo(GetUserInfoModel.class, pDeviceId, pDeviceType);
    }


    public static String getUserId() throws AdhocException {
        String userId = DeviceInfoSpConfig.getUserID();
        if (!TextUtils.isEmpty(userId)) {
            return userId;
        }

        String deviceId = DeviceIdCache.getDeviceId();
        GetUserInfoModel userInfo = getUserInfo(deviceId, DeviceType.getValue());

        if (userInfo.isSuccess()) {
            return userInfo.getUser_id();
        }

        throw new AdhocException("get user info from server unsuccessful" + userInfo.getMsgcode());
    }


    private static IDeviceUserDao getDeviceUserDao() {
        return DeviceActivateDaoHelper.getDeviceUserDao(MdmEvnFactory.getInstance().getCurEnvironment().getUrl());
    }
}
