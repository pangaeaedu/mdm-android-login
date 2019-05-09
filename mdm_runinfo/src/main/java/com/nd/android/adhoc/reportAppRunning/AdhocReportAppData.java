package com.nd.android.adhoc.reportAppRunning;

import android.support.annotation.Nullable;
import android.util.Pair;

import com.nd.android.adhoc.basic.common.util.AdhocDataCheckUtils;
import com.nd.android.adhoc.utils.AppRunInfoReportUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linsj on 2019/03/26.
 * @deprecated
 * APP当天运行数据，服务端数据调整，使用{@link RunningPackageInfo} 替代
 */
public class AdhocReportAppData {

    private String mPackageName;

    /**最近一次打开时间，用于在关闭时计算打开时长*/
    private long mLastOpenTime;

    private AtomicBoolean mbIsRunning;

    private List<AppHourData> mAppHourDataList;

    @Nullable
    public List<AppHourData> getListData() {
        return mAppHourDataList;
    }

    public void onOpen(){
        mbIsRunning.set(true);;
        mLastOpenTime = System.currentTimeMillis();

        int iHourNextPoint = AppRunInfoReportUtils.getCurrentHour()+1;
        if(AdhocDataCheckUtils.isCollectionEmpty(getAppHourDataList())){
            //当天首次打开APP，往数组添加一条记录
            getAppHourDataList().add(new AppHourData().setHour(iHourNextPoint)
                    .setPairCount(new Pair<>(1, 0L)));
            return;
        }

        //之前有过打开记录，则尝试累加统计
        AppHourData dataLast = mAppHourDataList.get(mAppHourDataList.size() - 1);
        if(iHourNextPoint == dataLast.getHour()){
            //本次打开与最后一次打开在同一个时段，直接次数加1
            Pair<Integer, Long> oldPair = dataLast.getPairCount();
            dataLast.setPairCount(new Pair<>(oldPair.first + 1, oldPair.second));
        }else {
            //本次打开与最后一次打开不在同一个时段，往数组添加一条记录
            getAppHourDataList().add(new AppHourData().setHour(iHourNextPoint)
                    .setPairCount(new Pair<>(1, 0L)));
        }
    }

    public void onClose(){
        if(mbIsRunning.compareAndSet(true, false)){
            refreshUseTime();
        }
    }

    public void refreshUseTime(){
        int iHourNextPoint = AppRunInfoReportUtils.getCurrentHour()+1;
        if(AdhocDataCheckUtils.isCollectionEmpty(getAppHourDataList())){
            //跨天了,先填满最后一个小时前的全部
            for(int index = 1; index < iHourNextPoint; index++){
                getAppHourDataList().add(new AppHourData().setHour(index)
                        .setPairCount(new Pair<>(0, 3600 * 1000L)));
            }
            //填充最后一个小时
            getAppHourDataList().add(new AppHourData().setHour(iHourNextPoint)
                    .setPairCount(new Pair<>(0, AppRunInfoReportUtils.getCurrentMSInHour())));
            return;
        }

        //填充内存里的最后一个小时
        AppHourData dataLast = mAppHourDataList.get(mAppHourDataList.size() - 1);
        Pair<Integer, Long> oldPair = dataLast.getPairCount();
        //得到被减数，如果关掉时与最后一次打开不在同一个时段，则用3600*1000L，否则用当前时段的毫秒数
        long lMSPower = iHourNextPoint > dataLast.getHour() ? 3600*1000L : AppRunInfoReportUtils.getCurrentMSInHour();
        long iMinuteToFill = lMSPower - AppRunInfoReportUtils.getMSInHourOfSpecifyTime(mLastOpenTime);
        dataLast.setPairCount(new Pair<>(oldPair.first, Math.max(60 ,oldPair.second + iMinuteToFill)));

        //填充中间时段，如果有
        fillSectionData(dataLast.getHour(), iHourNextPoint);

        //填充最后一个小时
        if(iHourNextPoint > dataLast.getHour()){
            getAppHourDataList().add(new AppHourData().setHour(iHourNextPoint)
                    .setPairCount(new Pair<>(0, AppRunInfoReportUtils.getCurrentMSInHour())));
        }
    }

    /**
     * 设置区间内的数据
     * @param iStart 起始，exclusive
     * @param iEnd 终止，exclusive
     */
    private void fillSectionData(int iStart, int iEnd){
        if(iStart >= iEnd){
            return;
        }

        for(int index = iStart + 1; index < iEnd; index++){
            getAppHourDataList().add(new AppHourData().setHour(index)
                    .setPairCount(new Pair<>(0, 3600*1000L)));
        }
    }

    public void setListData(List<AppHourData> listData) {
        this.mAppHourDataList = new ArrayList<>();
        this.mAppHourDataList.addAll(listData);
    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String packageName) {
        mPackageName = packageName;
    }

    private List<AppHourData> getAppHourDataList(){
        if(null == mAppHourDataList){
            mAppHourDataList = new ArrayList<>();
        }
        return mAppHourDataList;
    }

    public static class AppHourData{
        /**时段*/
        private int mHour;
        /**次数，分钟数*/
        private Pair<Integer, Long> mPairCount;

        public int getHour() {
            return mHour;
        }

        public AppHourData setHour(int hour) {
            mHour = hour;
            return this;
        }

        public Pair<Integer, Long> getPairCount() {
            return mPairCount;
        }

        public AppHourData setPairCount(Pair<Integer, Long> pairCount) {
            mPairCount = pairCount;
            return this;
        }
    }
}
