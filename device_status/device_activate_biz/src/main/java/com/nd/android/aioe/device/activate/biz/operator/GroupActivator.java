package com.nd.android.aioe.device.activate.biz.operator;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.aioe.device.activate.biz.api.ActivateConfig;
import com.nd.android.aioe.device.activate.biz.api.ISchoolGroupCodeRetriever;
import com.nd.android.aioe.device.activate.biz.bean.ActivateUserModel;
import com.nd.android.aioe.device.activate.biz.cache.DeviceActivateCache;
import com.nd.android.aioe.device.activate.dao.api.IDeviceActivateDao;
import com.nd.android.aioe.device.activate.dao.api.constant.ActivateChannel;
import com.nd.android.aioe.device.activate.dao.impl.DeviceActivateDaoHelper;
import com.nd.android.aioe.device.info.util.DeviceInfoHelper;
import com.nd.android.aioe.device.info.util.DeviceInfoManager;
import com.nd.android.aioe.device.status.dao.impl.constant.DeviceType;
import com.nd.android.mdm.biz.env.MdmEvnFactory;
import com.nd.sdp.android.serviceloader.AnnotationServiceLoader;

import java.util.Iterator;

class GroupActivator {


    public static void activate(@NonNull String pRootCode, @NonNull String pSchoolCode) throws AdhocException {

        String deviceID = DeviceInfoManager.getInstance().getDeviceID();
        String serialNum = DeviceInfoHelper.getSerialNumberThroughControl();
        String deviceSerialNumber = DeviceInfoHelper.getDeviceSerialNumberThroughControl();

        if (TextUtils.isEmpty(deviceID)) {
            throw new AdhocException("device id is empty");
        }

        if (TextUtils.isEmpty(serialNum)) {
            throw new AdhocException("serial number is empty");
        }

        String orgId = DeviceActivateCache.getOrgId();

        try {
            ActivateUserModel model =
                    getDeviceActivateDao().activate(
                            ActivateUserModel.class,
                            deviceID,
                            DeviceType.getValue(),
                            serialNum,
                            deviceSerialNumber,
                            ActivateChannel.AutoLogin,
                            "",
                            orgId);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    // 回调上层页面去获取 SchoolCode
    private String getSchoolCode(String pRootCode, boolean isSchoolNotFound) throws Exception {
        Iterator<ISchoolGroupCodeRetriever> interceptors = AnnotationServiceLoader
                .load(ISchoolGroupCodeRetriever.class).iterator();
        if (!interceptors.hasNext()) {
            Logger.e("yhq", "getSchoolCode, ISchoolGroupCodeRetriever not found");
            return null;
        }

        // 把取回的school groupCode放在result中，返回给下一个调用点
        Logger.i("yhq", "retrieveGroupCode root group code:" + pRootCode);
        ISchoolGroupCodeRetriever retriever = interceptors.next();
//        UserLoginConfig loginConfig = DeviceInfoManager.getInstance().getUserLoginConfig();


        String realRootCode;

        if (!ActivateConfig.getInstance().checkInited() || TextUtils.isEmpty(ActivateConfig.getInstance().getGroupCode())) {
            realRootCode = pRootCode;
        } else {
            realRootCode = ActivateConfig.getInstance().getGroupCode();
        }

        String schoolGroupCode;
        if (isSchoolNotFound) {
            schoolGroupCode = retriever.onGroupNotFound(realRootCode);
        } else {
            schoolGroupCode = retriever.retrieveGroupCode(realRootCode);
        }

        return schoolGroupCode;
    }


    private static IDeviceActivateDao getDeviceActivateDao() {
        return DeviceActivateDaoHelper.getDeviceActivateDao(MdmEvnFactory.getInstance().getCurEnvironment().getUrl());
    }
}
