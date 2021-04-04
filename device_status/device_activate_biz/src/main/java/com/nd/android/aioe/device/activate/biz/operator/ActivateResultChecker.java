package com.nd.android.aioe.device.activate.biz.operator;

import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.aioe.device.activate.biz.api.ActivateConfig;
import com.nd.android.aioe.device.activate.biz.api.model.CheckActivateModel;
import com.nd.android.aioe.device.activate.dao.api.IDeviceActivateDao;
import com.nd.android.aioe.device.activate.dao.impl.DeviceActivateDaoHelper;
import com.nd.android.aioe.device.status.dao.impl.constant.DeviceType;
import com.nd.android.mdm.biz.env.MdmEvnFactory;

class ActivateResultChecker {

    private static final String TAG = "DeviceActivateResultChecker";


    public static CheckActivateModel checkActivateResult(int pTimes, String pDeviceID, String pRequestID) throws Exception {
        Logger.i(TAG, "checkActivateResult");

        for (int i = 0; i < pTimes; i++) {
            try {
                Thread.sleep((i * 3 + 1) * 1000);
            } catch (InterruptedException ignored) {
            }

            CheckActivateModel model = null;
            // 去服务端查询
            try {
                model = getDeviceActivateDao().getActivateResult(CheckActivateModel.class, pDeviceID, DeviceType.getValue(), pRequestID);
            } catch (Exception e) {
                Logger.e(TAG, "getActivateResult error: " + e);
            }

            // 结果为空，继续尝试
            if (model == null){
                continue;
            }

            // 失败了
            if (!model.isSuccess()) {

                // 自动登录的情况下，并且组织不存在，才返回空，去通知重新选择组织，否则直接通知失败
                if (ActivateConfig.getInstance().isAutoLogin() && model.isGroupNotFound()) {
                    // 学校不存在的错误，直接返回
                    return null;
                }

                Logger.e(TAG, "GetActivateUserModel:" + model);
                // 激活中，就去重试
                if (model.isActivateStillProcessing()) {
                    Logger.e(TAG, "checkActivateResult, status still processing, retry again");
                    continue;
                }

                // TODO： 这里为什么要抛异常
                throw new AdhocException("activate result check failed, msgcode: " + model.getMsgcode());
            }

            return model;
        }

        // 试了三次还是失败的话，就抛异常
        Logger.i(TAG, "checkActivateResult times reached");
        throw new AdhocException("checkActivateResult retry times reached");

//        if (isAutoLogin()) {
//            Log.e("yhq", "queryActivateResultUntilTimesReach times reached");
//            DeviceActivateBroadcastUtils.sendActivateFailedBroadcast();
//            sendFailedAndQuitApp(120);
//            return;
//        }
//
//        return false;
    }

    private static IDeviceActivateDao getDeviceActivateDao() {
        return DeviceActivateDaoHelper.getDeviceActivateDao(MdmEvnFactory.getInstance().getCurEnvironment().getUrl());
    }

}
