package com.nd.android.aioe.device.status.biz.api.listener;

import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.aioe.device.status.biz.api.listener.IDeviceStatusErrorListener;
import com.nd.sdp.android.serviceloader.AnnotationServiceLoader;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class DeviceStatusErrorManager {
    private static final String TAG = "DeviceStatusErrorManage";

    private static final List<IDeviceStatusErrorListener> sErrorListeners = new CopyOnWriteArrayList<>();

    static {
        Iterator<IDeviceStatusErrorListener> errorListenerIterator = AnnotationServiceLoader.load(IDeviceStatusErrorListener.class).iterator();
        while (errorListenerIterator.hasNext()) {
            IDeviceStatusErrorListener policyTask = errorListenerIterator.next();
            sErrorListeners.add(policyTask);
        }
    }

    public static void notifyError(int pErrorCode) {

        for (IDeviceStatusErrorListener errorListener : sErrorListeners) {
            try {
                errorListener.onError(pErrorCode);
            } catch (Exception e) {
                Logger.e(TAG, "notifyError, run [" + errorListener.getClass().getCanonicalName() + "] onError failed: " + e);
            }
        }
    }


}
