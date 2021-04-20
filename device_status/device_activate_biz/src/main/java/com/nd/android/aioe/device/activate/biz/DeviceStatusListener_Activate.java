package com.nd.android.aioe.device.activate.biz;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.frame.api.initialization.AdhocExitAppManager;
import com.nd.android.adhoc.basic.frame.factory.AdhocFrameFactory;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.aioe.device.activate.biz.api.ActivateConfig;
import com.nd.android.aioe.device.activate.biz.api.provider.IDeviceActivateProvider;
import com.nd.android.aioe.device.activate.biz.api.provider.IDeviceCancelProvider;
import com.nd.android.aioe.device.activate.biz.cache.DeviceActivateCache;
import com.nd.android.aioe.device.info.config.DeviceInfoSpConfig;
import com.nd.android.aioe.device.status.biz.api.constant.DeviceStatus;
import com.nd.android.aioe.device.status.biz.api.listener.IDeviceStatusListener;
import com.nd.sdp.android.serviceloader.annotation.Service;

@Service(IDeviceStatusListener.class)
public class DeviceStatusListener_Activate implements IDeviceStatusListener {

    private static final String TAG = "DeviceStatusListener_Activate";

    @Override
    public void onStatusChange(@NonNull DeviceStatus pOldStatus, @NonNull DeviceStatus pNewStatus) {

        // 这里要取一次 OrgId，为了后面的逻辑可以用，有点 low ，但是暂时没办法

        DeviceActivateCache.getOrgId();

        // 如果 新的 是未激活，本地的是已激活
        if (pNewStatus.isUnActivated()) {

            // 如果本地原先是已激活，这里要清楚一些数据 ，是由于当时 lsj 和 碧总 对接的 ，服务端迁移导致 没数据的
            if (!pOldStatus.isUnActivated()) {
                Logger.e(TAG, "checkDeviceStatus, the local state is different from the server state");
                DeviceInfoSpConfig.saveDeviceIDSync("");
                DeviceInfoSpConfig.clearPushIDSync();
            }

            // 如果是 deleted 的 或者 非自动激活的，这里应该走注销流程
            if (pNewStatus.isDeleted() || !ActivateConfig.getInstance().isAutoLogin()) {
                doCancelDevice();
                return;
            }

            // 这里直接重新走 自动激活的流程
            doAutoActivate();
        }

        // 如果新的是已激活状态，就不再做任何处理
    }

    private void doCancelDevice() {
        IDeviceCancelProvider cancelProvider = (IDeviceCancelProvider) AdhocFrameFactory.getInstance().getAdhocRouter()
                .build(IDeviceCancelProvider.ROUTE_PATH).navigation();
        if (cancelProvider == null) {
            // provider 如果都为空，表示遇到了 极端异常情况，尝试自杀
            Logger.e(TAG, "IDeviceCancelProvider implement not found");
            AdhocExitAppManager.exitApp(0);
            return;
        }
        cancelProvider.onDeviceCancel();
    }

    private void doAutoActivate() {
        IDeviceActivateProvider activateProvider = (IDeviceActivateProvider) AdhocFrameFactory.getInstance().getAdhocRouter()
                .build(IDeviceActivateProvider.ROUTE_PATH).navigation();
        if (activateProvider == null) {
            // provider 如果都为空，表示遇到了 极端异常情况，尝试自杀
            Logger.e(TAG, "IDeviceActivateProvider implement not found");
            AdhocExitAppManager.exitApp(0);
            return;
        }

        try {
            // 如果已激活，内部会发出通知
            DeviceStatus deviceStatus = activateProvider.autoActivateByGroup(ActivateConfig.getInstance().getGroupCode(), "");
            if (deviceStatus.isUnActivated()) {
                doCancelDevice();
            }
//            else {
//                DeviceActivateResultManager.notifyActivateResult(true);
//                DeviceStatusChangeManager.notifyDeviceStatus(deviceStatus);
//            }
        } catch (Exception e) {
            Logger.e(TAG, "autoActivateByGroup error: " + e);
            // TODO： 这里搞出异常了的话，怎么处理？ 继续重试？自杀应用？

        }
    }

}
