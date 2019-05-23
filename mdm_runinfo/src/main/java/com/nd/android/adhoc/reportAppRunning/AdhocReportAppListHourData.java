package com.nd.android.adhoc.reportAppRunning;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceHelper;
import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.net.dao.AdhocHttpDao;
import com.nd.android.adhoc.basic.net.exception.AdhocHttpException;
import com.nd.android.adhoc.basic.sp.ISharedPreferenceModel;
import com.nd.android.adhoc.basic.sp.SharedPreferenceFactory;
import com.nd.android.adhoc.utils.AppRunInfoReportConstant;
import com.nd.android.adhoc.utils.AppRunInfoReportUtils;
import com.nd.android.mdm.biz.env.MdmEvnFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by linsj on 2019/03/26.
 * 某个时段App运行数据列表
 */
public class AdhocReportAppListHourData {
    /**以此对象的构造时间做随机数种子*/
    private final static long RANDOM_SECONDS_SEED = System.currentTimeMillis();

    private Random mRandom;

    private static final String TAG = "AdhocReportAppListHourData";
    /**超过这个数，当成垃圾数据，否则可能引起推栈爆满*/
    private static int s_MAX_CACHE_DATA_SIZE = 512 * 1000;

    /**随机3000秒内上报*/
    private static int RANDOM_SECONDS_BOUND = 3000;

    @Expose
    @SerializedName("time")
    private long mlMsOfCurDay;

    /**存储了所有这个时间段内运行的APP*/
    @Expose
    @SerializedName("info")
    private List<RunningPackageInfo> mlistApps = new ArrayList<>();

    private Map<String, RunningPackageInfo> mMapAppsToReport = new HashMap<>();

    private Gson mGson;

    public void setListApps(List<RunningPackageInfo> listApps){
        mlistApps = listApps;
    }

    public AdhocReportAppListHourData(){
        mlMsOfCurDay = AppRunInfoReportUtils.getCurrentDayTimeStamp();
        mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        mRandom = new Random(RANDOM_SECONDS_SEED);
    }

    public Map<String, RunningPackageInfo> getMapApps(){
        return mMapAppsToReport;
    }

    /**
     * 如果101助手挂掉重启，加载缓存时，由于map没有序列化，这里从列表里补齐
     * 这里不能把所有app都设成运行状态，因为这里包含　了未运行的。还没处理掉，还需要上报
     */
    public void holdMap(){
        if(mMapAppsToReport.isEmpty() && !mlistApps.isEmpty()){
            for(RunningPackageInfo info : mlistApps){
                mMapAppsToReport.put(info.getPackageName(), info);
            }

            //当前时段与上次上报(形成上报数据时间点)不在同一个小时段，立即上报一次
            ISharedPreferenceModel spModel = SharedPreferenceFactory.getInstance().getModel(AdhocBasicConfig.getInstance().getAppContext());
            long lLastReportTime = spModel.getLong(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_LAST_REPORT_TIME, 0);
            if(AppRunInfoReportUtils.getCurrentDayOfYear() != AppRunInfoReportUtils.getDayOfYearOfSpecifyTime(lLastReportTime)){
                Logger.i(TAG, "report after load cache");
                dealReportToServer(true);
            }
        }
    }

    /**
     * 跨小时，调整数据
     */
    private void switchToNextDay(){
        mlMsOfCurDay = AppRunInfoReportUtils.getCurrentDayTimeStamp();

        //跨过一天，把关闭的移除掉
        Iterator<RunningPackageInfo> iterator = mlistApps.iterator();
        while (iterator.hasNext()){
            RunningPackageInfo packageInfo = iterator.next();
            if(!packageInfo.getOpenStatus()){
                iterator.remove();
                mMapAppsToReport.remove(packageInfo.getPackageName());
            }else {
                packageInfo.switchToNextDay();
            }
        }
    }

    public void cacheData(){
        mlistApps.clear();
        mlistApps.addAll(mMapAppsToReport.values());
        for (RunningPackageInfo packageInfoWithTag : mlistApps) {
            packageInfoWithTag.fillUseTime(System.currentTimeMillis());
        }

        ISharedPreferenceModel spModel = SharedPreferenceFactory.getInstance().getModel(AdhocBasicConfig.getInstance().getAppContext());
        spModel.applyPutString(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_APP_LIST, mGson.toJson(this));
        spModel.applyPutLong(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_TIME, System.currentTimeMillis());
    }

    /**
     * 上报到服务端
     * @param bImmediately 是否马上上报
     */
    public void dealReportToServer(boolean bImmediately){
        mlistApps.clear();
        mlistApps.addAll(mMapAppsToReport.values());

        long lDeadTimeToMinus = System.currentTimeMillis();
        if(bImmediately){
            //如果是马上上报，则是缓存里的数据，这里截止时间就要用最后一次写缓存的时间来减
            ISharedPreferenceModel spModel = SharedPreferenceFactory.getInstance().getModel(AdhocBasicConfig.getInstance().getAppContext());
            lDeadTimeToMinus = spModel.getLong(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_TIME, System.currentTimeMillis());
        }
        for (RunningPackageInfo packageInfoWithTag : mlistApps) {
            packageInfoWithTag.fillUseTime(lDeadTimeToMinus);
        }

        final String strRuninfoCurHour = mGson.toJson(this);
        final JSONObject jsonData = generateRespJson(strRuninfoCurHour);
        switchToNextDay();
        ISharedPreferenceModel spModel = SharedPreferenceFactory.getInstance().getModel(AdhocBasicConfig.getInstance().getAppContext());
        spModel.applyPutString(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_TO_REPORT_DATA, jsonData.toString());
        spModel.applyPutLong(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_LAST_REPORT_TIME, System.currentTimeMillis());
        if(bImmediately){
            reportToServer(jsonData);
        }else {
            Observable.timer(getRandomDelaySeconds() * 1000, TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            reportToServer(jsonData);
                        }
                    });
        }
    }

    /**
     * 把未上报成功的数据写到缓存里，下次上报
     */
    private static void writeReportFailedCache(JSONObject jsonData){
        String strRunInfoList = getRunInfoListFromToReportData(jsonData);
        if(strRunInfoList.length() > s_MAX_CACHE_DATA_SIZE){
            //数据太多的清掉，有可能是多次上报没成功，防脏数据
            strRunInfoList = "";
        }
        ISharedPreferenceModel spModel = SharedPreferenceFactory.getInstance().getModel(AdhocBasicConfig.getInstance().getAppContext());
        spModel.applyPutString(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_FAILED_REPORTED_APP_LIST, strRunInfoList);
    }

    private static String getRunInfoListFromToReportData(JSONObject jsonData){
        if(null == jsonData || 0 == jsonData.length()){
            return "";
        }
        JSONObject dataObject = jsonData.optJSONObject("data");
        if(null != dataObject && 0 != jsonData.length()){
            JSONArray arrayRunInfo = dataObject.optJSONArray("runinfolist");
            if(null != arrayRunInfo){
                return arrayRunInfo.toString();
            }
        }
        return "";
    }

    /**
     * 将当前时段要汇报的数据，与本地缓存合并，形成本次要上报的数据
     * @param strCurHour
     * @return
     */
    private JSONObject generateRespJson(String strCurHour){
        JSONObject resp = new JSONObject();
        try {
            resp.put("cmd", "appruninfo");
            resp.put("device_token", DeviceHelper.getDeviceToken());
            resp.put("sessionid", java.util.UUID.randomUUID().toString());
            resp.put("timestamp", String.valueOf(System.currentTimeMillis()));

            JSONObject jsonData = new JSONObject();

            ISharedPreferenceModel spModel = SharedPreferenceFactory.getInstance().getModel(AdhocBasicConfig.getInstance().getAppContext());
            String strCache = spModel.getString(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_FAILED_REPORTED_APP_LIST, "");
            if(TextUtils.isEmpty(strCache)){
                Logger.i(TAG, "no un reported cache");
                strCache = "[]";
            }
            JSONArray arrayRunInfo = new JSONArray(strCache);
            arrayRunInfo.put(new JSONObject(strCurHour));

            jsonData.put("runinfolist", arrayRunInfo);

            resp.put("data", jsonData);
            return resp;
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        return resp;
    }

    private static String generateServerUrl(){
        String strHost = getHost();
//        String strHost = "http://192.168.252.45:8080";
        StringBuilder sb = new StringBuilder(strHost);
        sb.append("/v1/device/appruninfo/");

        return sb.toString();
    }

    private static String getHost(){
        String strHost = "";
        try {
            strHost = MdmEvnFactory.getInstance().getCurEnvironment().getUrl();
            if(TextUtils.isEmpty(strHost)){
                strHost = "http://drms.dev.web.nd";
            }
        }catch (NullPointerException e){
            strHost = "http://drms.dev.web.nd";
        }

        return strHost;
    }

    public void setMlMsOfCurHour(long lMsOfCurDay) {
        this.mlMsOfCurDay = lMsOfCurDay;
    }

    private long getRandomDelaySeconds(){
        long lSecondsDelay = mRandom.nextInt(RANDOM_SECONDS_BOUND);
        Logger.i(TAG, "will report "+ lSecondsDelay + " seconds later");
        return 0;
    }

    private JSONObject filterData(JSONObject jsonData){
        return removeDataLessThan3Minutes(jsonData);
    }

    private JSONObject removeDataLessThan3Minutes(JSONObject jsonData){
        try {
            JSONObject data = jsonData.optJSONObject("data");
            if(null == data){
                return jsonData;
            }

            JSONArray arrRuninfoList = data.optJSONArray("runinfolist");
            if(null == arrRuninfoList){
                return jsonData;
            }

            for(int index = 0; index < arrRuninfoList.length(); index++){
                JSONObject object = arrRuninfoList.optJSONObject(index);
                if(null == object) {
                    continue;
                }
                JSONArray arrInfos = object.optJSONArray("info");
                if(null == arrInfos){
                    continue;
                }

                JSONArray newArrInfos = new JSONArray();
                for(int indexInfos = 0; indexInfos < arrInfos.length(); indexInfos++){
                    JSONObject appInfo = arrInfos.optJSONObject(indexInfos);
                    if(null != appInfo){
                        long lRunTime = appInfo.optLong("runtime");
                        //17的版本没有remove，只能new 一个，把适合的都put进去
                        if(lRunTime >= 3 * 60 * 1000L){
                            newArrInfos.put(appInfo);
                        }
                    }
                }
                object.put("info", newArrInfos);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonData;
    }

    public void reportToServer(final JSONObject jsonData){
        if (null != jsonData && 0 != jsonData.length()) {
            filterData(jsonData);
            Observable.create(new Observable.OnSubscribe<RunInfoReportResult>() {
                @Override
                public void call(Subscriber<? super RunInfoReportResult> subscriber) {
                    try {
                        RunInfoReportResult result = new AdhocHttpDao(getHost()).postAction().post(generateServerUrl(),
                                RunInfoReportResult.class, jsonData.toString());
                        if (null == result || 0 != result.getMiErrorCode()) {
                            subscriber.onError(new AdhocException("report not success"));
                        }else {
                            subscriber.onNext(result);
                        }
                    } catch (AdhocHttpException e) {
                        subscriber.onError(e);
                    }
                }
            }).subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<RunInfoReportResult>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            Logger.e(TAG, e.getMessage());
                            e.printStackTrace();
                            //存到补报缓存里
                            writeReportFailedCache(jsonData);

                            if(curReportTheSameWithCache(jsonData)){
                                ISharedPreferenceModel spModel = SharedPreferenceFactory.getInstance().getModel(AdhocBasicConfig.getInstance().getAppContext());
                                spModel.applyPutString(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_TO_REPORT_DATA, "");
                            }
                        }

                        @Override
                        public void onNext(RunInfoReportResult result) {
                            //成功了，清除需要补报的缓存
                            Logger.i(TAG, "上报成功");
                            ISharedPreferenceModel spModel = SharedPreferenceFactory.getInstance().getModel(AdhocBasicConfig.getInstance().getAppContext());
                            spModel.applyPutString(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_FAILED_REPORTED_APP_LIST, "");

                            if(curReportTheSameWithCache(jsonData)){
                                spModel.applyPutString(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_TO_REPORT_DATA, "");
                            }
                        }
                    });
        }
    }

    private boolean curReportTheSameWithCache(JSONObject jsonData){
        boolean bRet = false;
        if(null == jsonData){
            bRet = true;
        }else {
            ISharedPreferenceModel spModel = SharedPreferenceFactory.getInstance().getModel(AdhocBasicConfig.getInstance().getAppContext());
            bRet = jsonData.toString().equals(spModel.getString(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_TO_REPORT_DATA, ""));
        }
        Logger.i(TAG,"the same as cache ? " + bRet );
        return bRet;
    }
}
