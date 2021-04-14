package com.nd.android.aioe.device.activate.biz.operator;

import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.aioe.device.activate.biz.api.ActivateConfig;
import com.nd.android.aioe.device.activate.biz.api.injection.ISchoolGroupCodeRetriever;
import com.nd.sdp.android.serviceloader.AnnotationServiceLoader;

import java.util.Iterator;

class _SchoolCodeGetter {

    private static final String TAG = "DeviceActivate";

    // 这里回调上层去拿 SchoolCode
    public static String getSchoolCode(String pRootCode, boolean isSchoolNotFound) throws Exception {
        Iterator<ISchoolGroupCodeRetriever> interceptors = AnnotationServiceLoader
                .load(ISchoolGroupCodeRetriever.class).iterator();
        if (!interceptors.hasNext()) {
            throw new AdhocException("!!!getSchoolCode failed, SchoolGroupCodeRetriever not found!!!");
        }

        // 把取回的school groupCode放在result中，返回给下一个调用点
        Logger.i(TAG, "getSchoolCode, retrieveGroupCode root group code:" + pRootCode);

        ISchoolGroupCodeRetriever retriever = interceptors.next();

        String realRootCode;

        if (!ActivateConfig.getInstance().checkInited() || TextUtils.isEmpty(ActivateConfig.getInstance().getGroupCode())) {
            realRootCode = pRootCode;
        } else {
            realRootCode = ActivateConfig.getInstance().getGroupCode();
        }

        String schoolGroupCode;
        if (isSchoolNotFound) {
            schoolGroupCode = retriever.onGroupNotFound(realRootCode);
        } else {
            schoolGroupCode = retriever.retrieveGroupCode(realRootCode);
        }

        return schoolGroupCode;
    }

}
