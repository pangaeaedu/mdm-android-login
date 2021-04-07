package com.nd.android.aioe.device.status.biz.provider;

import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.common.util.AdhocDataCheckUtils;
import com.nd.android.aioe.device.status.biz.api.judge.IDeviceStatusUpdateRetryJudger;
import com.nd.sdp.android.serviceloader.AnnotationServiceLoader;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class DeviceStatusRetryJudgerManager {

    private static List<IDeviceStatusUpdateRetryJudger> sUpdateRetryJudgers;


    public static boolean useLocalStatusAfterUpdateRetryFailed() {
        List<IDeviceStatusUpdateRetryJudger> updateRetryJudgers = getUpdateRetryJudgers();
        if (AdhocDataCheckUtils.isCollectionEmpty(updateRetryJudgers)) {
            return false;
        }

        for (IDeviceStatusUpdateRetryJudger updateRetryJudger : updateRetryJudgers) {
            if (updateRetryJudger.useLocalStatusAfterRetryFailed()) {
                return true;
            }
        }

        return false;
    }

    public static void onUpdateRetrySuccess() {
        List<IDeviceStatusUpdateRetryJudger> updateRetryJudgers = getUpdateRetryJudgers();
        if (AdhocDataCheckUtils.isCollectionEmpty(updateRetryJudgers)) {
            return;
        }

        for (IDeviceStatusUpdateRetryJudger updateRetryJudger : updateRetryJudgers) {
            updateRetryJudger.onSuccess();
        }
    }


    private static List<IDeviceStatusUpdateRetryJudger> getUpdateRetryJudgers() {
        if (sUpdateRetryJudgers == null) {
            sUpdateRetryJudgers = new CopyOnWriteArrayList<>();
        }

        Iterator<IDeviceStatusUpdateRetryJudger> interceptors = AnnotationServiceLoader
                .load(IDeviceStatusUpdateRetryJudger.class).iterator();
        while (interceptors.hasNext()) {
            sUpdateRetryJudgers.add(interceptors.next());
        }
        return sUpdateRetryJudgers;
    }
}
