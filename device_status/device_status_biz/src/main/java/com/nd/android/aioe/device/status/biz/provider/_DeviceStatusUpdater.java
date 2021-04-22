package com.nd.android.aioe.device.status.biz.provider;

import android.support.annotation.WorkerThread;

import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.communicate.impl.MdmTransferFactory;
import com.nd.android.adhoc.communicate.push.listener.IAdhocPushConnectListener;
import com.nd.android.aioe.device.activate.biz.api.IUserLoginWayConfirmRetrieve;
import com.nd.android.aioe.device.info.config.DeviceInfoSpConfig;
import com.nd.android.aioe.device.status.biz.api.cache.DeviceStatusCache;
import com.nd.android.aioe.device.status.biz.api.listener.DeviceStatusErrorManager;
import com.nd.android.aioe.device.status.biz.api.listener.IDeviceStatusErrorListener;
import com.nd.android.aioe.device.status.biz.api.model.GetDeviceStatusModel;
import com.nd.sdp.android.serviceloader.AnnotationServiceLoader;

import java.util.Iterator;

class _DeviceStatusUpdater {

    private static final String TAG = "DeviceStatusBusiness";

    @WorkerThread
    public static void updateDeviceStatus() throws AdhocException {
        String deviceId;
        try {
            deviceId = _DeviceIdGetter.getDeviceId();
        } catch (Exception e) {
            DeviceStatusErrorManager.notifyError(IDeviceStatusErrorListener.ERROR_CODE_CONFIRM_DEVICE_ID_ERROR);
            throw AdhocException.newException(e);
        }

        GetDeviceStatusModel deviceStatusModel = null;

        int retryCount = 0;
        while (deviceStatusModel == null) {
            try {
                deviceStatusModel = _DeviceStatusGetter.queryDeviceStatusFromServer(deviceId);
            } catch (Exception e) {
                Logger.e(TAG, "queryDeviceStatusFromServer error: " + e);
            }


            // TODO： 这里可能需要确认一下状态通知的机制
            if ((deviceStatusModel == null || !deviceStatusModel.isSuccess()) && retryCount >= 1) {

                // 如果允许在失败的时候，先通知出去本地的状态
                if (_DeviceStatusRetryJudgerManager.useLocalStatusFirstOnFailed()) {
                    DeviceStatusChangeManager.notifyDeviceStatus(DeviceStatusCache.getDeviceStatus());
                }

//                DeviceStatus deviceStatus = DeviceStatusCache.getDeviceStatus();
//
//                if (deviceStatus != DeviceStatus.Init
//                        && deviceStatus != DeviceStatus.Unknown) {
//                    DeviceStatusChangeManager.notifyDeviceStatus(deviceStatus);
//                }
            }

            // 第一次获取，不算重试，所以在判断后再 ++
            retryCount++;
        }

        // 如果重试成功的，这里才去通知重试判断者
        if (retryCount >= 1) {
            _DeviceStatusRetryJudgerManager.onUpdateRetrySuccess();
        }

        // 如果获取回来的状态是 已删除的
        if (deviceStatusModel.getDevicesStatus().isDeleted()) {

            Iterator<IUserLoginWayConfirmRetrieve> interceptors = AnnotationServiceLoader
                    .load(IUserLoginWayConfirmRetrieve.class, IUserLoginWayConfirmRetrieve.class.getClassLoader()).iterator();
            if (interceptors.hasNext()) {
                interceptors.next().pauseAutoLogin();
            } else {
                Logger.e(TAG, "IUserLoginWayConfirmRetrieve impl not found, which means go on");
            }
        }

        DeviceStatusChangeManager.notifyDeviceStatus(deviceStatusModel.getDevicesStatus());
    }


}
