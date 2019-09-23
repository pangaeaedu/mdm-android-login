package com.nd.android.adhoc.reportAppRunInfoByDb;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.common.util.AdhocDataCheckUtils;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.sp.ISharedPreferenceModel;
import com.nd.android.adhoc.basic.sp.SharedPreferenceFactory;
import com.nd.android.adhoc.db.entity.MdmRunInfoEntity;
import com.nd.android.adhoc.db.entity.intfc.IMdmRunInfoEntity;
import com.nd.android.adhoc.db.operator.MdmRunInfoDbOperatorFactory;
import com.nd.android.adhoc.reportAppRunning.RunningPackageInfo;
import com.nd.android.adhoc.utils.AppRunInfoReportConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @name adhoc-login
 * @class name：com.nd.android.adhoc.reportAppRunInfoByDb
 * @class describe
 * @time 2019/8/27 21:19
 * @change
 * @chang time
 * @class describe
 */
public class AdhocAppRunningDataTransfer {
    private static final String TAG = "AdhocAppRunningDataTransfer";

    public static void convertSpToDb(){
        ISharedPreferenceModel spModel = SharedPreferenceFactory.getInstance().getModel(AdhocBasicConfig.getInstance().getAppContext());
        convertToReportDataToDb(spModel);
        convertCacheAppListToDb(spModel);
        convertOldRunningToNewRunning();
    }

    private static void convertOldRunningToNewRunning(){
        ISharedPreferenceModel spModel = SharedPreferenceFactory.getInstance().getModel(AdhocBasicConfig.getInstance().getAppContext());
        String strCache = spModel.getString(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_RUNNING_APP_MAP, "");

        if(!TextUtils.isEmpty(strCache)){
            Gson gson = new GsonBuilder().create();
            Map<String, RunningPackageInfo> mapOldRunningApps = gson.fromJson(strCache, new TypeToken<HashMap<String, RunningPackageInfo>>(){}.getType());

            if(null == mapOldRunningApps || mapOldRunningApps.isEmpty()){
                return;
            }
            Map<String, MdmRunInfoEntity> mapNewRunningApps = new HashMap<>();
            for (Map.Entry<String, RunningPackageInfo> entry : mapOldRunningApps.entrySet()) {
                MdmRunInfoEntity entity = new MdmRunInfoEntity(java.util.UUID.randomUUID().toString(),
                        entry.getValue().getPackageName(), entry.getValue().getAppName());
                entity.setLastOpenTime(entry.getValue().getLastOpenTime());
                if(entry.getValue().getOpenStatus()){
                    entity.setOpenStatus();
                }
                mapNewRunningApps.put(entry.getKey(), entity);
            }
            spModel.applyPutString(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_RUNNING_APP_MAP_VDB, gson.toJson(mapNewRunningApps));
        }
    }

    private static void convertToReportDataToDb(final ISharedPreferenceModel spModel){
        String strToReportCache = spModel.getString(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_TO_REPORT_DATA, "");
        if(!TextUtils.isEmpty(strToReportCache)){
            try {
                Logger.i(TAG, "begin to convert to report cache");
                List<IMdmRunInfoEntity> listEntity = getRunInfoEntityList(new JSONObject(strToReportCache));
                if(!AdhocDataCheckUtils.isCollectionEmpty(listEntity)){
                    Logger.i(TAG, "get to report list entity and begin to write");
                    MdmRunInfoDbOperatorFactory.getInstance().getRunInfoDbOperator().saveOrUpdateRunInfo(listEntity);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Logger.e(TAG, "convertToReportDataToDb error: " + e.getMessage());
            }
        }
        Logger.i(TAG, "convert to report cache over");
        spModel.applyPutString(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_TO_REPORT_DATA, "");
    }

    private static void convertCacheAppListToDb(final ISharedPreferenceModel spModel){
        String strAppListCache = spModel.getString(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_APP_LIST, "");
        long lCacheTime = spModel.getLong(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_TIME, 0);
        Logger.i(TAG , "cached app list time: " + lCacheTime);
        if(0 != lCacheTime && !TextUtils.isEmpty(strAppListCache)){
            try {
                Logger.i(TAG, "begin to convert app list cache");
                List<IMdmRunInfoEntity> listEntityCache = getRunInfoEntityListFromSingleData(new JSONObject(strAppListCache));
                List<IMdmRunInfoEntity> listToReport = MdmRunInfoDbOperatorFactory.getInstance().
                        getRunInfoDbOperator().getToReportRunInfo();
                for(IMdmRunInfoEntity entityToReport : listToReport){
                    for(IMdmRunInfoEntity entityCache : listEntityCache){
                        if(entityCache.getDayBeginTimeStamp() == entityToReport.getDayBeginTimeStamp()
                                && entityCache.getPackageName().equals(entityToReport.getPackageName())){
                            Logger.i(TAG, "remove one same package :" + entityToReport.getPackageName() + " from cache list");
                            listEntityCache.remove(entityCache);
                            break;
                        }
                    }
                }

                if(!AdhocDataCheckUtils.isCollectionEmpty(listEntityCache)){
                    Logger.i(TAG, "get app list cache entity and begin to write");
                    MdmRunInfoDbOperatorFactory.getInstance().getRunInfoDbOperator().saveOrUpdateRunInfo(listEntityCache);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Logger.e(TAG, "convertCacheAppListToDb error: " + e.getMessage());
            }
        }
        Logger.i(TAG, "convert app list cache over");
        spModel.applyPutString(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_APP_LIST, "");
    }

    private static List<IMdmRunInfoEntity> getRunInfoEntityList(final JSONObject jsonData){
        JSONObject data = jsonData.optJSONObject("data");
        if(null == data){
            return null;
        }

        JSONArray arrRuninfoList = data.optJSONArray("runinfolist");
        if(null == arrRuninfoList){
            return null;
        }

        List<IMdmRunInfoEntity> listRet = new ArrayList<>();
        for(int index = 0; index < arrRuninfoList.length(); index++){
            JSONObject object = arrRuninfoList.optJSONObject(index);
            if(null == object) {
                continue;
            }

            List<IMdmRunInfoEntity> listEntity = getRunInfoEntityListFromSingleData(object);
            if(null != listEntity){
                listRet.addAll(listEntity);
            }
        }
        return listRet;
    }

    private static List<IMdmRunInfoEntity> getRunInfoEntityListFromSingleData(final JSONObject jsonData){
        JSONArray arrInfos = jsonData.optJSONArray("info");
        if(null == arrInfos){
            return null;
        }
        List<IMdmRunInfoEntity> listRet = new ArrayList<>();
        long lDayTimeStamp = jsonData.optLong("time");
        for(int indexInfos = 0; indexInfos < arrInfos.length(); indexInfos++){

            JSONObject appInfo = arrInfos.optJSONObject(indexInfos);
            MdmRunInfoEntity entity = new MdmRunInfoEntity(java.util.UUID.randomUUID().toString(),
                    appInfo.optString("package"), appInfo.optString("appname"));
            entity.setDayBeginTimeStamp(lDayTimeStamp);
            entity.setRunTime(appInfo.optLong("runtime"));
            entity.setRunCount(appInfo.optInt("runcount"));
            entity.setLastOpenTime(appInfo.optLong("mLastOpenTime"));
            boolean bIsRunning = appInfo.optBoolean("mbIsRunning");
            if(bIsRunning){
                entity.setOpenStatus();
            }

            if(isDataValid(entity)){
                listRet.add(entity);
            }
        }
        return listRet;
    }

    private static boolean isDataValid(final MdmRunInfoEntity entity){
        return !TextUtils.isEmpty(entity.getAppName()) &&
                !TextUtils.isEmpty(entity.getPackageName()) &&
                0 != entity.getRunCount();
    }
}
