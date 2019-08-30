package com.nd.android.adhoc.reportAppRunInfoByDb;

import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.common.util.AdhocDataCheckUtils;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.sp.ISharedPreferenceModel;
import com.nd.android.adhoc.basic.sp.SharedPreferenceFactory;
import com.nd.android.adhoc.db.entity.MdmRunInfoEntity;
import com.nd.android.adhoc.db.entity.intfc.IMdmRunInfoEntity;
import com.nd.android.adhoc.db.operator.MdmRunInfoDbOperatorFactory;
import com.nd.android.adhoc.utils.AppRunInfoReportConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
                List<IMdmRunInfoEntity> listEntity = getRunInfoEntityListFromSingleData(new JSONObject(strAppListCache));
                if(!AdhocDataCheckUtils.isCollectionEmpty(listEntity)){
                    Logger.i(TAG, "get app list cache entity and begin to write");
                    MdmRunInfoDbOperatorFactory.getInstance().getRunInfoDbOperator().saveOrUpdateRunInfo(listEntity);
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
            listRet.add(entity);
        }
        return listRet;
    }
}
