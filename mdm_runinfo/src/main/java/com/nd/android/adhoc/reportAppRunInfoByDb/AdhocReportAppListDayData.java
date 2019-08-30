package com.nd.android.adhoc.reportAppRunInfoByDb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.sp.ISharedPreferenceModel;
import com.nd.android.adhoc.basic.sp.SharedPreferenceFactory;
import com.nd.android.adhoc.basic.util.thread.AdhocRxJavaUtil;
import com.nd.android.adhoc.db.entity.MdmRunInfoEntity;
import com.nd.android.adhoc.db.entity.intfc.IMdmRunInfoEntity;
import com.nd.android.adhoc.db.operator.MdmRunInfoDbOperatorFactory;
import com.nd.android.adhoc.utils.AppRunInfoReportConstant;
import com.nd.android.adhoc.utils.AppRunInfoReportUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by linsj on 2019/03/26.
 * 某个时段App运行数据列表
 */
public class AdhocReportAppListDayData {
    private static final String TAG = "AdhocReportAppListDayData";

    /**以此对象的构造时间做随机数种子*/
    private final static long RANDOM_SECONDS_SEED = System.currentTimeMillis();

    private Random mRandom;

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
    private List<MdmRunInfoEntity> mlistApps = new ArrayList<>();

    private Map<String, MdmRunInfoEntity> mMapAppsToReport = new HashMap<>();

    private Gson mGson;

    private Subscription mDelayReportSubscription;

    public void setListApps(List<MdmRunInfoEntity> listApps){
        mlistApps = listApps;
    }

    public AdhocReportAppListDayData(){
        mlMsOfCurDay = AppRunInfoReportUtils.getCurrentDayTimeStamp();
        mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        mRandom = new Random(RANDOM_SECONDS_SEED);
    }

    public Map<String, MdmRunInfoEntity> getMapApps(){
        return mMapAppsToReport;
    }

    /**
     * 如果101助手挂掉重启，加载缓存时，由于map没有序列化，这里从列表里补齐
     * 这里不能把所有app都设成运行状态，因为这里包含　了未运行的。还没处理掉，还需要上报
     */
    public void holdMap(){
        if(mMapAppsToReport.isEmpty() && !mlistApps.isEmpty()){
            for(MdmRunInfoEntity info : mlistApps){
                mMapAppsToReport.put(info.getPackageName(), info);
            }
        }
    }

    public void stopReporting(){
        AdhocRxJavaUtil.doUnsubscribe(mDelayReportSubscription);
    }

    /**
     * 跨小时，调整数据
     */
    private void switchToNextDay(){
        mlMsOfCurDay = AppRunInfoReportUtils.getCurrentDayTimeStamp();

        //跨过一天，把关闭的移除掉
        Iterator<MdmRunInfoEntity> iterator = mlistApps.iterator();
        while (iterator.hasNext()){
            MdmRunInfoEntity packageInfo = iterator.next();
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

        for (MdmRunInfoEntity entity : mlistApps) {
            entity.fillUseTime(System.currentTimeMillis());
        }
        List<IMdmRunInfoEntity> listEntity = new ArrayList<>();
        listEntity.addAll(mlistApps);
        MdmRunInfoDbOperatorFactory.getInstance().getRunInfoDbOperator().saveOrUpdateRunInfo(listEntity);

        ISharedPreferenceModel spModel = SharedPreferenceFactory.getInstance().getModel(AdhocBasicConfig.getInstance().getAppContext());
        spModel.applyPutLong(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_TIME, System.currentTimeMillis());
    }

    /**
     * 上报到服务端
     * @param bImmediately 是否马上上报
     */
    public void dealReportToServer(boolean bImmediately){
        mlistApps.clear();
        mlistApps.addAll(mMapAppsToReport.values());

        ISharedPreferenceModel spModel = SharedPreferenceFactory.getInstance().getModel(AdhocBasicConfig.getInstance().getAppContext());
        long lDeadTimeToMinus = System.currentTimeMillis();
        if(bImmediately){
            //如果是马上上报，则是缓存里的数据，这里截止时间就要用最后一次写缓存的时间来减
            lDeadTimeToMinus = spModel.getLong(AppRunInfoReportConstant.OPS_SP_KEY_CACHE_TIME, System.currentTimeMillis());
        }
        for (MdmRunInfoEntity entity : mlistApps) {
            entity.fillUseTime(lDeadTimeToMinus);
        }

        switchToNextDay();
        final List<IMdmRunInfoEntity> listEntity = new ArrayList<>();
        listEntity.addAll(mlistApps);
        MdmRunInfoDbOperatorFactory.getInstance().getRunInfoDbOperator().saveOrUpdateRunInfo(listEntity);

        if(bImmediately){
            RunInfoReportHelper.reportToServerBusiness();
        }else {
            mDelayReportSubscription = Observable.timer(getRandomDelaySeconds() * 1000, TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            RunInfoReportHelper.reportToServerBusiness();
                        }
                    });
        }
    }

    public void setMlMsOfCurHour(long lMsOfCurDay) {
        this.mlMsOfCurDay = lMsOfCurDay;
    }

    private long getRandomDelaySeconds(){
        long lSecondsDelay = mRandom.nextInt(RANDOM_SECONDS_BOUND);
        Logger.i(TAG, "will report "+ lSecondsDelay + " seconds later");
        return lSecondsDelay;
    }
}
