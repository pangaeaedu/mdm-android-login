package com.nd.android.aioe.device.activate.biz;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.nd.android.aioe.device.activate.biz.api.ActivateConfig;
import com.nd.android.aioe.device.activate.biz.cache.DeviceActivateCache;
import com.nd.android.aioe.device.status.biz.api.constant.DeviceStatus;
import com.nd.android.aioe.device.status.biz.api.listener.IDeviceStatusListener;
import com.nd.sdp.android.serviceloader.annotation.Service;

@Service(IDeviceStatusListener.class)
public class DeviceStatusListener_Activate implements IDeviceStatusListener {


    @Override
    public void onStatusChange(@NonNull DeviceStatus pOldStatus, @NonNull DeviceStatus pNewStatus) {

        // 这里要取一次 OrgId，为了后面的逻辑可以用，有点 low ，但是暂时没办法

        DeviceActivateCache.getOrgId();

        // 已激活
        if (!pNewStatus.isUnActivated()) {
            // TODO：已经激活了，那么就直接跳转 到 主页去

            return;
        }


        // 还未激活
        if (ActivateConfig.getInstance().isAutoLogin()) {

            autoActivateOperate(pOldStatus, pNewStatus);
            return;
        }

        manualActivateOperate(pOldStatus, pNewStatus);
    }

    // TODO：自动激活处理
    private void autoActivateOperate(@NonNull DeviceStatus pOldStatus, @NonNull DeviceStatus pNewStatus) {


    }

    // TODO：手动激活处理
    private void manualActivateOperate(@NonNull DeviceStatus pOldStatus, @NonNull DeviceStatus pNewStatus) {


    }
}
