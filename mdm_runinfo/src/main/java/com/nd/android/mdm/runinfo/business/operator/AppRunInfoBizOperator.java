package com.nd.android.mdm.runinfo.business.operator;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceHelper;
import com.nd.android.adhoc.basic.common.util.AdhocDataCheckUtils;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.net.exception.AdhocHttpException;
import com.nd.android.mdm.runinfo.business.bean.AppRunPackageInfoBean;
import com.nd.android.mdm.runinfo.sdk.db.entity.IAppRunInfoEntity;
import com.nd.android.mdm.runinfo.sdk.db.operator.AppRunInfoDbOperatorFactory;
import com.nd.android.mdm.runinfo.sdk.http.dao.AppRunInfoDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by HuangYK on 2018/12/7.
 */

class AppRunInfoBizOperator implements IAppRunInfoBizOperator {

    private static final String TAG = "AppRunInfoBizOperator";

//    {
//
//        "deviceToken": "",
//            "info": [{
//        "2018-6-14": {
//            "pckage:versioncode": {
//                "时段": [次数, 时长],
//                "10": [10, 1],
//                "12": [5, 1]
//            },
//            "com.nd.sdp.demo:456": {
//                "1": [10, 1],
//                "2": [10, 2],
//                "3": [10, 2]
//            }
//        }
//    }]
//    }

    @Override
    public boolean postAppRunInfo() {
        List<IAppRunInfoEntity> entityList =
                AppRunInfoDbOperatorFactory.getInstance().getAppExecutionDbOperator().getAllEntityList();

        if (AdhocDataCheckUtils.isCollectionEmpty(entityList)) {
            return false;
        }
        //"2018-12-12","packagename",["hour(时段)","count(次数)"]
        Map<Long, Map<String, AppRunPackageInfoBean>> packageInfoMap = new HashMap<>();

        for (IAppRunInfoEntity runInfoEntity : entityList) {
            Map<String, AppRunPackageInfoBean> infoBeanMap =
                    packageInfoMap.get(runInfoEntity.getRunDate());

            if (infoBeanMap == null) {
                infoBeanMap = new HashMap<>();
                packageInfoMap.put(runInfoEntity.getRunDate(), infoBeanMap);
            }
            AppRunPackageInfoBean infoBean = infoBeanMap.get(runInfoEntity.getPackageName());

            if (infoBean == null) {
                infoBean = new AppRunPackageInfoBean(runInfoEntity.getPackageName());
            }
            infoBean.addPackageInfo(runInfoEntity.getHour(),
                    runInfoEntity.getRunDate(),
                    runInfoEntity.getCount());
        }

        JSONObject infojson = new JSONObject();
        JSONObject jsonList = new JSONObject();
        try {
            infojson.put("info", jsonList);
            Set<Long> dateSet = packageInfoMap.keySet();
            for (Long dayOfDate : dateSet) {
                Map<String, AppRunPackageInfoBean> infoBeanMap = packageInfoMap.get(dayOfDate);

                JSONObject jsonDayLog = new JSONObject();
                jsonList.put(String.valueOf(dayOfDate), jsonDayLog);
                Set<String> packageNames = infoBeanMap.keySet();
                for (String packageName : packageNames) {
                    AppRunPackageInfoBean infoBean = infoBeanMap.get(packageName);
                    JSONObject contents = new JSONObject();
                    for (int i = 0; i < infoBean.getSize(); i++) {
                        JSONArray array = new JSONArray();
                        array.put(infoBean.getCountList().get(i));
                        array.put(infoBean.getDurationList().get(i));
                        contents.put(String.valueOf(infoBean.getHourList().get(i)), array);
                    }
                    jsonDayLog.put(packageName, contents);
                }
            }

        } catch (JSONException e) {
            Logger.e(TAG, "make info json error: " + e.getMessage());
            return false;
        }

        Logger.i(TAG, "postAppRunInfo");
        Logger.d(TAG, "postAppRunInfo, info json: " + infojson);
        try {
            new AppRunInfoDao()
                    .postAppRunInfo(infojson.toString(), DeviceHelper.getDeviceToken());

            Logger.d(TAG, "postAppRunInfo success");
            return true;
        } catch (AdhocHttpException e) {
            Logger.e(TAG, "post app run info json error: " + e.getMessage());
        }
        return false;
    }
}
