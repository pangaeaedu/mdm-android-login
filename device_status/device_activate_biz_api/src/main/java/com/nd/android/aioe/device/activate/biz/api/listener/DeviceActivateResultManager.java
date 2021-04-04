package com.nd.android.aioe.device.activate.biz.api.listener;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DeviceActivateResultManager {


    private static final List<IDeviceActivateResultListener> sActivateResultListeners;


    static {
        sActivateResultListeners = new CopyOnWriteArrayList<>();
    }

    public static void addActivateResultListener(@NonNull IDeviceActivateResultListener pListener) {
        if (sActivateResultListeners.contains(pListener)) {
            return;
        }

        sActivateResultListeners.add(pListener);
    }


    public static void removeActivateResultListener(@NonNull IDeviceActivateResultListener pListener) {
        sActivateResultListeners.remove(pListener);
    }


    public static void notifyActivateResult(boolean pIsSuccess) {

        if (sActivateResultListeners.isEmpty()) {
            return;
        }

        for (IDeviceActivateResultListener activateResultListener : sActivateResultListeners) {
            activateResultListener.onActivateResult(pIsSuccess);
        }

    }
}
