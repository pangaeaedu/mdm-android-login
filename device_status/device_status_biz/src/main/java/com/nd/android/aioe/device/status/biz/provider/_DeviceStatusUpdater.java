package com.nd.android.aioe.device.status.biz.provider;

import android.support.annotation.WorkerThread;

import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.communicate.impl.MdmTransferFactory;
import com.nd.android.adhoc.communicate.push.listener.IAdhocPushConnectListener;
import com.nd.android.aioe.device.info.config.DeviceInfoSpConfig;
import com.nd.android.aioe.device.status.biz.api.cache.DeviceStatusCache;
import com.nd.android.aioe.device.status.biz.api.listener.DeviceStatusChangeManager;
import com.nd.android.aioe.device.status.biz.api.listener.DeviceStatusErrorManager;
import com.nd.android.aioe.device.status.biz.api.listener.IDeviceStatusErrorListener;
import com.nd.android.aioe.device.status.biz.api.model.GetDeviceStatusModel;

class _DeviceStatusUpdater {

    private static final String TAG = "DeviceStatusBusiness";

    private static final IAdhocPushConnectListener sPushConnectListener = new IAdhocPushConnectListener() {
        @Override
        public void onPushDeviceToken(String deviceToken) {
            Logger.i(TAG, "PushOperator, onPushDeviceToken: ");
            Logger.d(TAG, "PushOperator, onPushDeviceToken: " + deviceToken);

            onConnected();
        }

        @Override
        public void onConnected() {

            // 绑定 PushId
            _DeviceIdBinder.setPushId(MdmTransferFactory.getPushModel().getDeviceId());

            long lastUpdateTime = DeviceStatusCache.getLastUpdateTime();

            // 如果距离最后一次检测成功的时间 >= 24小时，就检测一遍状态
            if (Math.abs(System.currentTimeMillis() - lastUpdateTime) >= 24 * 60 * 60 * 1000) {
                Logger.e(TAG, "The current time is one day away from the last update time, recheck device status");

                _DeviceStatusChecker.checkDeviceStatusFromServer(DeviceInfoSpConfig.getDeviceID());
            }

        }

        @Override
        public void onDisconnected() {

        }
    };

    static {
        MdmTransferFactory.getPushModel().addConnectListener(sPushConnectListener);

        if (MdmTransferFactory.getPushModel().isConnected()) {
            sPushConnectListener.onConnected();
        }
    }

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

        DeviceStatusChangeManager.notifyDeviceStatus(deviceStatusModel.getDevicesStatus());
    }


}
