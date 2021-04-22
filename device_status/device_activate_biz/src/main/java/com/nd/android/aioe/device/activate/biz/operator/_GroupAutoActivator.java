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

class _GroupAutoActivator {

    private static final String TAG = "_GroupAutoActivator";

    @WorkerThread
    public static DeviceStatus autoActivateByGroupCode(@NonNull String pRootCode, String pSchoolCode) throws Exception {

        String schoolCode = pSchoolCode;

        DeviceActivateModel activateModel = null;
        CheckActivateModel checkActivateModel;

        // 如果学校是空的，那么这里要去选学校，要传 false 去取
        if (TextUtils.isEmpty(schoolCode)) {
            schoolCode = _SchoolCodeGetter.getSchoolCode(pRootCode, false);
        }

        while (true) {
            try {
                activateModel = _GroupActivator.activate(schoolCode);
            } catch (Exception e) {
                Logger.e(TAG, "groupActivate, do activate error: " + e);
            }

            if (activateModel == null) {
                Logger.i(TAG, "activateModel is null, wait to retry");
                Thread.sleep(5000);
                continue;
            }

            if (!activateModel.isSuccess()) {
//                int delayTime = getRetrySleepSec(activateModel.getDelayTime()) * 1000;
//                Thread.sleep(delayTime);

                Logger.i(TAG, "activate failed, wait to retry");
                sleep(activateModel.getDelayTime());
                continue;
            }

            try {
                checkActivateModel = _ActivateResultChecker.checkActivateResult(3, DeviceInfoSpConfig.getDeviceID(), activateModel.getRequestid());
            } catch (Exception e) {
                Logger.e(TAG, "checkActivateResult error, wait to retry: " + e);
                sleep(activateModel.getDelayTime());
                continue;
            }

            // 自动激活情况下 返回 null 表示  学校不存在，要重新去获取一下
            if (checkActivateModel == null) {
                schoolCode = _SchoolCodeGetter.getSchoolCode(pRootCode, true);
                continue;
            }

            // 自动激活的情况下，如果不成功，重试
            if (ActivateConfig.getInstance().isAutoLogin() && checkActivateModel.getDeviceStatus().isUnActivated()) {
                Logger.e(TAG, "checkActivateResult return success, but device status is unActivated, wait to retry");
                sleep(activateModel.getDelayTime());
                continue;
            }
            break;
        }
        _ActivateResultOperator.operateActivateResult(checkActivateModel);

        return checkActivateModel.getDeviceStatus();
    }


    private static int getRetrySleepSec(int limit) {
        Random r = new Random();
        return r.nextInt(limit) + 20;
    }

    private static void sleep(int delayTime){
        int sleepTime = getRetrySleepSec(delayTime) * 1000;
        Logger.i(TAG, "sleep :" + delayTime);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
