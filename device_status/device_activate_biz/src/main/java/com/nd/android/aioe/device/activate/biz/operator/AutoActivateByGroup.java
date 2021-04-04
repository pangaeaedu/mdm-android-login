package com.nd.android.aioe.device.activate.biz.operator;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.aioe.device.activate.biz.api.ActivateConfig;
import com.nd.android.aioe.device.activate.biz.api.model.CheckActivateModel;
import com.nd.android.aioe.device.activate.biz.api.model.DeviceActivateModel;
import com.nd.android.aioe.device.info.config.DeviceInfoSpConfig;
import com.nd.android.aioe.device.status.biz.api.constant.DeviceStatus;

import java.util.Random;

class AutoActivateByGroup {

    private static final String TAG = "DeviceActivateBusiness";

    @WorkerThread
    public static DeviceStatus autoActivateByGroupCode(@NonNull String pRootCode, String pSchoolCode) throws Exception {

        String schoolCode = pSchoolCode;

        DeviceActivateModel activateModel = null;
        CheckActivateModel checkActivateModel;

        // 如果学校是空的，那么这里要去选学校，要传 false 去取
        if (TextUtils.isEmpty(schoolCode)) {
            schoolCode = SchoolCodeGetter.getSchoolCode(pRootCode, false);
        }

        while (true) {
            try {
                activateModel = GroupActivator.activate(schoolCode);
            } catch (Exception e) {
                Logger.e(TAG, "groupActivate, do activate error: " + e);
            }

            if (activateModel == null) {
                Logger.i(TAG, "activateModel is null, wait to retry");
                Thread.sleep(5000);
                continue;
            }

            if (!activateModel.isSuccess()) {
                int delayTime = getRetrySleepSec(activateModel.getDelayTime()) * 1000;
                Logger.i(TAG, "activate failed, wait to retry :" + delayTime);
                Thread.sleep(delayTime);
                continue;
            }

            try {
                checkActivateModel = ActivateResultChecker.checkActivateResult(3, DeviceInfoSpConfig.getDeviceID(), activateModel.getRequestid());
            } catch (Exception e) {
                Logger.e(TAG, "checkActivateResult error: " + e);
                continue;
            }

            // 返回 null 表示  学校不存在，要重新去获取一下
            if (checkActivateModel == null) {
                schoolCode = SchoolCodeGetter.getSchoolCode(pRootCode, true);
                continue;
            }

            // 自动激活的情况下，如果不成功，重试
            if (ActivateConfig.getInstance().isAutoLogin() && checkActivateModel.getDeviceStatus().isUnActivated()) {
                Logger.e(TAG, "checkActivateResult retusn success, but device status is unActivated");
                continue;
            }
            break;
        }
        ActivateResultOperator.operateActivateResult(checkActivateModel);

        return checkActivateModel.getDeviceStatus();
    }


    private static int getRetrySleepSec(int limit) {
        Random r = new Random();
        return r.nextInt(limit) + 20;
    }

}
