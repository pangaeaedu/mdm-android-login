package com.nd.android.aioe.device.status.biz.provider;

import com.nd.android.adhoc.basic.common.util.AdhocDataCheckUtils;
import com.nd.android.aioe.device.status.biz.api.judge.IDeviceStatusUpdateRetryJudger;
import com.nd.sdp.android.serviceloader.AnnotationServiceLoader;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class _DeviceStatusRetryJudgerManager {

    private static List<IDeviceStatusUpdateRetryJudger> sUpdateRetryJudgers;

    static {
        sUpdateRetryJudgers = new CopyOnWriteArrayList<>();
        Iterator<IDeviceStatusUpdateRetryJudger> interceptors = AnnotationServiceLoader
                .load(IDeviceStatusUpdateRetryJudger.class).iterator();
        while (interceptors.hasNext()) {
            sUpdateRetryJudgers.add(interceptors.next());
        }
    }

    public static boolean useLocalStatusFirstOnFailed() {
        if (AdhocDataCheckUtils.isCollectionEmpty(sUpdateRetryJudgers)) {
            return true;
        }

        for (IDeviceStatusUpdateRetryJudger updateRetryJudger : sUpdateRetryJudgers) {
            if (updateRetryJudger.useLocalStatusFirstOnFailed()) {
                return true;
            }
        }

        return false;
    }

    public static void onUpdateRetrySuccess() {
        if (AdhocDataCheckUtils.isCollectionEmpty(sUpdateRetryJudgers)) {
            return;
        }

        for (IDeviceStatusUpdateRetryJudger updateRetryJudger : sUpdateRetryJudgers) {
            updateRetryJudger.onSuccess();
        }
    }

}
