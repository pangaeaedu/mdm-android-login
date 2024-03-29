package com.nd.android.aioe.device.activate.biz.operator;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.aioe.device.activate.biz.api.ActivateConfig;
import com.nd.android.aioe.device.activate.biz.api.exception.AdhocActivateErrorCode;
import com.nd.android.aioe.device.activate.biz.api.exception.AdhocActivateException;
import com.nd.android.aioe.device.activate.biz.api.model.CheckActivateModel;
import com.nd.android.aioe.device.activate.dao.api.IDeviceActivateDao;
import com.nd.android.aioe.device.activate.dao.impl.DeviceActivateDaoHelper;
import com.nd.android.aioe.device.status.dao.impl.constant.DeviceType;
import com.nd.android.mdm.biz.env.MdmEvnFactory;

import androidx.annotation.IntRange;

class _ActivateResultChecker {

    private static final String TAG = "DeviceActivate";


    public static CheckActivateModel checkActivateResult(@IntRange(from = 1) int pTimes, String pDeviceID, String pRequestID) throws Exception {
        Logger.i(TAG, "ActivateResultChecker, checkActivateResult");

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
                Logger.e(TAG, "ActivateResultChecker, getActivateResult error: " + e);
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

                Logger.d(TAG, "GetActivateUserModel:" + model);
                // 激活中，就去重试
                if (model.isActivateStillProcessing()) {
                    Logger.e(TAG, "checkActivateResult, status still processing, retry again");
                    continue;
                }

                String errMsg = AdhocBasicConfig.getInstance().getAppContext()
                        .getString(AdhocActivateErrorCode.transformCheckResultMsg(model.getMsgcode()));

                int errCode = AdhocActivateErrorCode.transformCheckResultCode(model.getMsgcode());

                // 异常表示 是一个失败的结果
                throw new AdhocActivateException(errMsg, errCode);
            }

            return model;
        }

        // 试了三次还是失败的话，就抛异常
        Logger.i(TAG, "checkActivateResult retry times reached");
        throw new AdhocActivateException("checkActivateResult retry times reached", AdhocActivateErrorCode.ERROR_CHECK_RESULT_RETRY_TIMES_REACHED);

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
