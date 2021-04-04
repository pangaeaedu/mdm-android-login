package com.nd.android.aioe.device.status.biz.provider;

import android.support.annotation.WorkerThread;

import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.communicate.impl.MdmTransferFactory;
import com.nd.android.adhoc.communicate.push.listener.IAdhocPushConnectListener;
import com.nd.android.aioe.device.info.config.DeviceInfoSpConfig;
import com.nd.android.aioe.device.status.biz.api.cache.DeviceStatusCache;
import com.nd.android.aioe.device.status.biz.api.constant.DeviceStatus;
import com.nd.android.aioe.device.status.biz.api.listener.DeviceStatusChangeManager;
import com.nd.android.aioe.device.status.biz.api.listener.DeviceStatusErrorManager;
import com.nd.android.aioe.device.status.biz.api.listener.IDeviceStatusErrorListener;
import com.nd.android.aioe.device.status.biz.model.GetDeviceStatusModel;

class DeviceStatusUpdater {

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
            DeviceIdBinder.setPushId(MdmTransferFactory.getPushModel().getDeviceId());

            // 这个应邀移到 device status 业务层

            long lastUpdateTime = DeviceStatusCache.getLastUpdateTime();

            if (Math.abs(System.currentTimeMillis() - lastUpdateTime) >= 24 * 60 * 60 * 1000) {
                Logger.e(TAG, "The current time is one day away from the last update time, recheck device status");

                DeviceStatusChecker.checkDeviceStatusFromServer(DeviceInfoSpConfig.getDeviceID());
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
            deviceId = DeviceIdGetter.getDeviceId();
        } catch (Exception e) {
            DeviceStatusErrorManager.notifyError(IDeviceStatusErrorListener.ERROR_CODE_CONFIRM_DEVICE_ID_ERROR);
            throw AdhocException.newException(e);
        }

        DeviceIdBinder.setDeviceId(deviceId);

        GetDeviceStatusModel deviceStatusModel = null;

        // TODO： 如果查询上来的结果是空的，待选择处理方案：
        //   1、根据本地现有的状态来处理
        //   2、直接当做查询失败，重试
        while (deviceStatusModel == null) {
            try {
                deviceStatusModel = DeviceStatusGetter.queryDeviceStatusFromServer(deviceId);
            } catch (Exception e) {
                Logger.e(TAG, "queryDeviceStatusFromServer error: " + e);
            }
        }

        if (deviceStatusModel == null) {
            // TODO：查询失败，那么 查看本地状态，如果本地状态 不是 Init，那么就先用，然后再在后台循环去查

            DeviceStatusChangeManager.notifyDeviceStatus(DeviceStatus.Init);
            return;
        }

        DeviceStatusCache.setAllowCheckStatus(true);
        DeviceStatusChangeManager.notifyDeviceStatus(deviceStatusModel.getDevicesStatus());
    }


}
