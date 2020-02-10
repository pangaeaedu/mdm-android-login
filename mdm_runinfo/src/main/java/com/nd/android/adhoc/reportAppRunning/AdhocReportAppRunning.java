package com.nd.android.adhoc.reportAppRunning;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nd.android.adhoc.IAppListListenner;
import com.nd.android.adhoc.RunningAppWatchManager;
import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.sp.ISharedPreferenceModel;
import com.nd.android.adhoc.basic.sp.SharedPreferenceFactory;
import com.nd.android.adhoc.utils.AppRunInfoReportConstant;
import com.nd.android.adhoc.utils.AppRunInfoReportUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


/**
 * APP运行统计类
 */
@Deprecated
public class AdhocReportAppRunning {
    private static final String TAG = "AdhocReportAppRunning";
    private static AdhocReportAppRunning instance;

    /**上一次检查完，正在运行的APP*/
    private Map<String, RunningPackageInfo> mMapRunningApps = new HashMap<>();

    /**须排除不统计的包名*/
    private HashSet<String> mSetExcluded;

    /**上一次写缓存时间*/
    private long mlLastWriteCacheTime;

    /**上一次上报时间*/
    private long mlLastReportTime;

    private Gson mGson;

    private boolean mbIsWathcing = false;

    private final Object mSyncObject = new Object();

    public synchronized static AdhocReportAppRunning getInstance() {
        if (instance == null) {
            instance = new AdhocReportAppRunning();
        }
        return instance;
    }

    private void generateExcluedPackages(){
        mSetExcluded = new HashSet<>();
        mSetExcluded.add("com.nd.pad.eci.demo");
    }

    private AdhocReportAppRunning() {
        mGson = new GsonBuilder().create();
        generateExcluedPackages();
    }

    public void deal(){
        synchronized (mSyncObject){
            if(!mbIsWathcing){
                loadCache();
                RunningAppWatchManager.getInstance().addListeners(mAppListListenner);
                RunningAppWatchManager.getInstance().watch();
                mbIsWathcing = true;
            }
        }
    }

    public void stopWatching(){
        synchronized (mSyncObject){
            if(!mbIsWathcing){
                return;
            }
            //移除监听
            RunningAppWatchManager.getInstance().removeListeners(mAppListListenner);

            //取消上报
            getCurrentToReportHourData().stopReporting();
            destroyCurToReportHourData();

            //清除缓存
            ISharedPreferenceModel spModel = SharedPreferenceFactory.getInstance().getModel(AdhocBasicConfig.getInstance().getAppContext());
            spModel.applyPutString(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_RUNNING_APP_MAP, "");
            spModel.applyPutString(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_APP_LIST, "");
            spModel.applyPutLong(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_TIME, 0);
            spModel.applyPutString(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_FAILED_REPORTED_APP_LIST, "");
            spModel.applyPutString(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_TO_REPORT_DATA, "");
            spModel.applyPutLong(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_LAST_REPORT_TIME, 0);

            //重置内存
            mlLastWriteCacheTime = 0;
            mlLastReportTime = 0;
            mMapRunningApps.clear();

            mbIsWathcing = false;
        }
    }

    private IAppListListenner mAppListListenner = new IAppListListenner() {
        @Override
        public void onRetrievedAppList(@Nullable PackageInfo[] installedList, @Nullable PackageInfo[] runningList) {
            //如果时间段跨过新的十分钟段，且与上次写缓存不是同一分钟段(这个条件用于避免同一分钟重复写缓存)，则缓存一次本地
            int iCurrentMinute = AppRunInfoReportUtils.getCurrentMinute();
            if(iCurrentMinute % 10 == 9 &&
                    (0 == mlLastWriteCacheTime || iCurrentMinute != AppRunInfoReportUtils.getMinuteOfSpecifyTime(mlLastWriteCacheTime))) {
                writeCacheToLocal();
            }
            //如果时间段跨过新的一天，且与上次上报不是同一小时段(这个条件用于避免同一分钟重复上报)，上报到服务端
            if(AppRunInfoReportUtils.getCurrentHour() == 0 &&
                    AppRunInfoReportUtils.getCurrentMinute() == 0 &&
                    (0 == mlLastReportTime ||
                        AppRunInfoReportUtils.getCurrentDayOfYear() != AppRunInfoReportUtils.getDayOfYearOfSpecifyTime(mlLastReportTime))){
                reportToServer();
            }

            dealAppReport(runningList);
        }
    };

    private void writeCacheToLocal(){
        Logger.i(TAG, "writeCacheToLocal");
        //把正在运行列表也写进去，防助手被杀
        ISharedPreferenceModel spModel = SharedPreferenceFactory.getInstance().getModel(AdhocBasicConfig.getInstance().getAppContext());
        spModel.applyPutString(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_RUNNING_APP_MAP, mGson.toJson(mMapRunningApps));

        getCurrentToReportHourData().cacheData();
        mlLastWriteCacheTime = System.currentTimeMillis();
    }

    private void reportToServer(){
        Logger.i(TAG, "dealReportToServer");
        getCurrentToReportHourData().dealReportToServer(false);
        mlLastReportTime = System.currentTimeMillis();
    }

    private void dealAppReport(@Nullable PackageInfo[] runningList){
        if(null == runningList){
            return;
        }
//        Logger.i(TAG, "开始在内存中记录");
        //先重置状态
        for (Map.Entry<String, RunningPackageInfo> entry : mMapRunningApps.entrySet()) {
            entry.getValue().setMbRunningListContainsThis(false);
        }

        PackageManager pm = AdhocBasicConfig.getInstance().getAppContext().getPackageManager();

        List<PackageInfo> listOpen = new ArrayList<>();
        List<RunningPackageInfo> listClosed = new ArrayList<>();
        for (PackageInfo packageInfo : runningList){
            if(mSetExcluded.contains(packageInfo.packageName)){
                continue;
            }
            if(!mMapRunningApps.containsKey(packageInfo.packageName)){
                //map里没有这个包名，则是刚运行的
                listOpen.add(packageInfo);
            }else {
                //把两个结构体里都有的包名标记到map里去
                mMapRunningApps.get(packageInfo.packageName).setMbRunningListContainsThis(true);
            }
        }

        //现在根据map里的标记取出map里有，而当前运行列表里没有的，说明APP被关闭了
        for (Map.Entry<String, RunningPackageInfo> entry : mMapRunningApps.entrySet()) {
            if(!entry.getValue().isMbRunningListContainsThis()){
                listClosed.add(entry.getValue());
            }
        }

        //处理新打开的APP
        for(PackageInfo info : listOpen){
            RunningPackageInfo newPack = new RunningPackageInfo(
                    java.util.UUID.randomUUID().toString(),
                    info.packageName,
                    pm.getApplicationLabel(info.applicationInfo).toString());
            mMapRunningApps.put(info.packageName, newPack);

            if(!getCurrentToReportHourData().getMapApps().containsKey(info.packageName)){
                getCurrentToReportHourData().getMapApps().put(info.packageName, newPack);
            }
            getCurrentToReportHourData().getMapApps().get(info.packageName).onOpen();
        }

        //处理被关闭的APP
        for(RunningPackageInfo info : listClosed){
            mMapRunningApps.remove(info.getPackageName());

            if(getCurrentToReportHourData().getMapApps().containsKey(info.getPackageName())){
                getCurrentToReportHourData().getMapApps().get(info.getPackageName()).onClose();
            }
        }

        //启动时，有可能从缓存里读出正在运行的app，但是本时段的需上传的为空，这里重新再添加一下
        for (Map.Entry<String, RunningPackageInfo> entry : mMapRunningApps.entrySet()) {
            if(!getCurrentToReportHourData().getMapApps().containsKey(entry.getKey())){
                Logger.i(TAG, "readd open apps");
                getCurrentToReportHourData().getMapApps().put(entry.getKey(), entry.getValue());
                getCurrentToReportHourData().getMapApps().get(entry.getKey()).setOpenStatus();
            }
        }
//        Logger.i(TAG, "内存中记录结束");
    }

    private void destroyCurToReportHourData(){
        mToReportHourData = null;
    }

    private AdhocReportAppListHourData mToReportHourData;
    private AdhocReportAppListHourData getCurrentToReportHourData(){
        if(null == mToReportHourData){
            mToReportHourData = new AdhocReportAppListHourData();
        }
        return mToReportHourData;
    }

    private void loadCache(){
        Logger.i(TAG, "loadCache");
        ISharedPreferenceModel spModel = SharedPreferenceFactory.getInstance().getModel(AdhocBasicConfig.getInstance().getAppContext());
        String strCache = spModel.getString(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_RUNNING_APP_MAP, "");

        if(!TextUtils.isEmpty(strCache)){
            mMapRunningApps = mGson.fromJson(strCache, new TypeToken<HashMap<String, RunningPackageInfo>>(){}.getType());
        }

        //如果缓存里没有上次上报时间，就把当前时段的上报时间写上去，表示上一个时间段的数据已在当前时段汇报过, 这样与后续逻辑统一
        if(0 == spModel.getLong(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_LAST_REPORT_TIME, 0)){
            spModel.applyPutLong(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_LAST_REPORT_TIME, System.currentTimeMillis());
        }

        //上报可能没及时上报的数据，要在恢复缓存数据之前处理这个，否则数据被覆盖
        String strToReportCache = spModel.getString(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_TO_REPORT_DATA, "");
        if(!TextUtils.isEmpty(strToReportCache)){
            try {
                getCurrentToReportHourData().reportToServer(new JSONObject(strToReportCache));
            } catch (JSONException e) {
                Logger.e(TAG, "什么鸟数据");
                e.printStackTrace();
                spModel.applyPutString(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_TO_REPORT_DATA, "");
            }
        }

        strCache = spModel.getString(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_APP_LIST);
        if(!TextUtils.isEmpty(strCache)){
            getDataFromJson(strCache);
        }
    }

    /**
     * Gson在OPS上莫名其妙的无法反序列化OpsReportAppListHourData对象，这里逐个反序列化
     * @param strData
     */
    private void getDataFromJson(String strData){
        Logger.i(TAG, "begin getDataFromJson");
        mToReportHourData = new AdhocReportAppListHourData();
        try {
            JSONObject jsonData = new JSONObject(strData);
            mToReportHourData.setMlMsOfCurHour(jsonData.optLong("time"));
            JSONArray array = jsonData.optJSONArray("info");
            List<RunningPackageInfo> listApps = new ArrayList<>();
            for(int index = 0; index < array.length(); index++){
                RunningPackageInfo info = mGson.fromJson(array.get(index).toString(), RunningPackageInfo.class);
                listApps.add(info);
            }
            mToReportHourData.setListApps(listApps);
            mToReportHourData.holdMap();
        }catch (JSONException e ){
            Logger.e(TAG, "not valid cache");
            ISharedPreferenceModel spModel = SharedPreferenceFactory.getInstance().getModel(AdhocBasicConfig.getInstance().getAppContext());
            spModel.applyPutString(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_APP_LIST, "");
        }
        Logger.i(TAG, "end getDataFromJson");
    }

}
