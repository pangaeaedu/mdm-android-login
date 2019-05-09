package com.nd.android.adhoc.reportAppRunning;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nd.android.adhoc.utils.AppRunInfoReportUtils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linsj on 2019/03/26.
 *  运行中的APP的数据
 */
public class RunningPackageInfo {
    /**表示监听回调app列表与上一次回调列表都包含了该app，用于差分比较，优化算法*/
    private boolean mbRunningListContainsThis;

    @Expose
    @SerializedName("package")
    private String mPackageName;

    @Expose
    @SerializedName("appname")
    private String mstrAppName;

    @Expose
    @SerializedName("runcount")
    private int miRunCount;

    @Expose
    @SerializedName("runtime")
    private long mlRunTime;

    @Expose
    private AtomicBoolean mbIsRunning = new AtomicBoolean();

    /**最近一次打开时间，用于在关闭时计算打开时长*/
    @Expose
    private long mLastOpenTime;

    public RunningPackageInfo(String strPackage, String strAppName){
        mPackageName = strPackage;
        mstrAppName = strAppName;
    }

    public void onOpen(){
        mbIsRunning.set(true);;
        mLastOpenTime = System.currentTimeMillis();
        miRunCount++;
    }

    public long getLastOpenTime(){
        return mLastOpenTime;
    }

    /**
     * 当前APP是否在运行
     */
    public boolean getOpenStatus(){
        return mbIsRunning.get();
    }

    /**
     * 如果101助手挂掉重启，加载缓存时，要给每一个正在运行的APP设置为运行状态
     */
    public void setOpenStatus(){
        mbIsRunning.set(true);
    }

    /**
     * 关闭APP的时候，统计时长
     */
    public void onClose(){
        if(mbIsRunning.compareAndSet(true, false)){
            refreshUseTime();
        }
    }

    /**
     * 写缓存或是上报的时候，填充正在运行APP的时长
     */
    public void fillUseTime(){
        if(mbIsRunning.compareAndSet(true, true)){
            refreshUseTime();
        }
    }

    private void refreshUseTime(){
        mlRunTime += System.currentTimeMillis() - mLastOpenTime;
        mlRunTime = Math.min(3600 * 1000L, mlRunTime);
        mLastOpenTime = System.currentTimeMillis();
    }

    /**
     * 跨小时，调整数据
     */
    public void switchToNextHour(){
        miRunCount = 0;
        mlRunTime = 0;
        mLastOpenTime = AppRunInfoReportUtils.getCurrentHourTimeStamp();
    }

    public boolean isMbRunningListContainsThis() {
        return mbRunningListContainsThis;
    }

    public void setMbRunningListContainsThis(boolean mbRunningListContainsThis) {
        this.mbRunningListContainsThis = mbRunningListContainsThis;
    }

    public String getPackageName() {
        return mPackageName;
    }
}