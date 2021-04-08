package com.nd.android.aioe.device.status.biz.provider;

import com.nd.android.adhoc.basic.common.util.AdhocDataCheckUtils;
import com.nd.android.aioe.device.status.biz.api.judge.IDeviceIdConfirmRetryJudger;
import com.nd.sdp.android.serviceloader.AnnotationServiceLoader;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class DeviceIdRetryJudgerManager {

    private static List<IDeviceIdConfirmRetryJudger> sConfirmRetryJudgers;


    public static boolean isContinueRetryOnFailed() {
        List<IDeviceIdConfirmRetryJudger> confirmRetryJudgers = getConfirmRetryJudgers();
        if (AdhocDataCheckUtils.isCollectionEmpty(confirmRetryJudgers)) {
            return false;
        }

        for (IDeviceIdConfirmRetryJudger confirmRetryJudger : confirmRetryJudgers) {
            if (confirmRetryJudger.isContinueRetryOnFailed()) {
                return true;
            }
        }

        return false;
    }

    public static void onConfirmRetrySuccess() {
        List<IDeviceIdConfirmRetryJudger> confirmRetryJudgers = getConfirmRetryJudgers();
        if (AdhocDataCheckUtils.isCollectionEmpty(confirmRetryJudgers)) {
            return;
        }

        for (IDeviceIdConfirmRetryJudger confirmRetryJudger : confirmRetryJudgers) {
            confirmRetryJudger.onSuccess();
        }
    }


    private static List<IDeviceIdConfirmRetryJudger> getConfirmRetryJudgers() {
        if (sConfirmRetryJudgers == null) {
            sConfirmRetryJudgers = new CopyOnWriteArrayList<>();
        }

        Iterator<IDeviceIdConfirmRetryJudger> interceptors = AnnotationServiceLoader
                .load(IDeviceIdConfirmRetryJudger.class).iterator();
        while (interceptors.hasNext()) {
            sConfirmRetryJudgers.add(interceptors.next());
        }
        return sConfirmRetryJudgers;
    }
}
