package com.nd.android.aioe.device.activate.biz;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.aioe.device.activate.biz.api.ActivateConfig;
import com.nd.android.aioe.device.activate.biz.cache.DeviceActivateCache;
import com.nd.android.aioe.device.info.config.DeviceInfoSpConfig;
import com.nd.android.aioe.device.info.util.DeviceIDSPUtils;
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

            // 如果本地原先是已激活，这里要清楚一些数据 TODO ??? 不明白为什么要清空 DeviceId 和 PushId ？
            if (!pOldStatus.isUnActivated()) {
                Logger.e(TAG, "checkDeviceStatus, the local state is different from the server state");
                DeviceInfoSpConfig.saveDeviceIDSync("");
                DeviceInfoSpConfig.clearPushIDSync();
            }

            // TODO：如果是 deleted 的 或者 非自动激活的，这里应该走注销流程
            if (pNewStatus.isDeleted() || !ActivateConfig.getInstance().isAutoLogin()) {

                return;
            }

            // TODO：这里直接重新走 自动激活的流程

            return;
        }

        // TODO：如果新的是已激活，就直接发送激活成功的通知出去


    }

}
