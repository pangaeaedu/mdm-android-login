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
        GetUserInfoModel userInfoModel = getDeviceUserDao().getUserInfo(GetUserInfoModel.class, pDeviceId, pDeviceType);

        if (userInfoModel.isSuccess()) {
            saveUserInfoToSp(userInfoModel);
        }
        return userInfoModel;
    }

    public static String getUserId() throws AdhocException {
        String userId = DeviceInfoSpConfig.getUserID();
        if (!TextUtils.isEmpty(userId)) {
            return userId;
        }

        String deviceId = DeviceIdCache.getDeviceId();
        GetUserInfoModel userInfoModel = getUserInfo(deviceId, DeviceType.getValue());

        if (userInfoModel.isSuccess()) {
            return userInfoModel.getUser_id();
        }

        throw new AdhocException("get user id from server unsuccessful" + userInfoModel.getMsgcode());
    }

    public static String getNickName() throws AdhocException {
        String nickname = DeviceInfoSpConfig.getNickname();
        if (!TextUtils.isEmpty(nickname)) {
            return nickname;
        }

        String deviceId = DeviceIdCache.getDeviceId();
        GetUserInfoModel userInfoModel = getUserInfo(deviceId, DeviceType.getValue());
        if (userInfoModel.isSuccess()) {
            return userInfoModel.getUser_id();
        }

        throw new AdhocException("get nickname from server unsuccessful" + userInfoModel.getMsgcode());
    }

    private static void saveUserInfoToSp(GetUserInfoModel pUserInfoModel) {
        DeviceInfoSpConfig.saveNickname(pUserInfoModel.getNickName());
        DeviceInfoSpConfig.saveUserID(pUserInfoModel.getUser_id());
        DeviceInfoSpConfig.saveDeviceCode(pUserInfoModel.getDevice_code());
        DeviceInfoSpConfig.saveGroupCode(pUserInfoModel.getGroupcode());
    }

    private static IDeviceUserDao getDeviceUserDao() {
        return DeviceActivateDaoHelper.getDeviceUserDao(MdmEvnFactory.getInstance().getCurEnvironment().getUrl());
    }
}
