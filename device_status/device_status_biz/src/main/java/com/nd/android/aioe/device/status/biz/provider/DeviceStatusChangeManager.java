package com.nd.android.aioe.device.status.biz.provider;

import androidx.annotation.NonNull;

import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.aioe.device.status.biz.api.cache.DeviceStatusCache;
import com.nd.android.aioe.device.status.biz.api.constant.DeviceStatus;
import com.nd.android.aioe.device.status.biz.api.listener.IDeviceStatusListener;
import com.nd.sdp.android.serviceloader.AnnotationServiceLoader;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

final class DeviceStatusChangeManager {
    private static final String TAG = "DeviceStatusChangeManager";

    private static final List<IDeviceStatusListener> sStatusChangeListeners = new CopyOnWriteArrayList<>();

    static {
        Iterator<IDeviceStatusListener> listenerIterator = AnnotationServiceLoader.load(IDeviceStatusListener.class).iterator();
        while (listenerIterator.hasNext()) {
            IDeviceStatusListener listener = listenerIterator.next();
            sStatusChangeListeners.add(listener);
        }
    }

    public static void notifyDeviceStatus(@NonNull DeviceStatus pStatus) {
        Logger.i(TAG, "notifyDeviceStatus: " + pStatus);

        StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
        if (stackTraceElements != null && stackTraceElements.length > 3) {
            Logger.d(TAG, "notifyDeviceStatus called stack trace: " + Logger.getExtInfo(stackTraceElements[3]));
        }

        DeviceStatus curStatus = DeviceStatusCache.getDeviceStatus();

        // 先取出旧的，然后更新缓存
        DeviceStatusCache.setDeviceStatus(pStatus);

        for (IDeviceStatusListener listener : sStatusChangeListeners) {
            try {
                listener.onStatusChange(curStatus, pStatus);
            } catch (Exception e) {
                Logger.e(TAG, "notifyDeviceStatus, run [" + listener.getClass().getCanonicalName() + "] onError failed: " + e);
            }
        }

    }


}
