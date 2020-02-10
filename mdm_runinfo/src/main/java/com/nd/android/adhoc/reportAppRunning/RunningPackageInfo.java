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
    @SerializedName("id")
    private String mstrId;

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

    public RunningPackageInfo(String strId, String strPackage, String strAppName){
        mstrId = strId;
        mPackageName = strPackage;
        mstrAppName = strAppName;
    }

    public String getId(){
        return mstrId;
    }

    public void resetId(){
        mstrId = java.util.UUID.randomUUID().toString();
    }

    public void onOpen(){
        mbIsRunning.set(true);
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
            refreshUseTime(System.currentTimeMillis());
        }
    }

    /**
     * 写缓存或是上报的时候，填充正在运行APP的时长
     */
    public void fillUseTime(long lDeadTimeToMinus){
        if(mbIsRunning.compareAndSet(true, true)){
            refreshUseTime(lDeadTimeToMinus);
        }
    }

    private void refreshUseTime(long lDeadTimeToMinus){
        mlRunTime += lDeadTimeToMinus - mLastOpenTime;
        mlRunTime = Math.min(24 * 3600 * 1000L, mlRunTime);
        mLastOpenTime = System.currentTimeMillis();
    }

    /**
     * 跨天，调整数据
     */
    public void switchToNextDay(){
        resetId();
        miRunCount = 0;
        mlRunTime = 0;
        mLastOpenTime = AppRunInfoReportUtils.getCurrentDayTimeStamp();
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

    public void setRunCount(int iRunCount) {
        this.miRunCount = iRunCount;
    }

    public void setRunTime(long lRunTime) {
        this.mlRunTime = lRunTime;
    }

    public long getRunTime() {
        return mlRunTime;
    }

    public void setIsRunning(boolean bIsRunning){
        mbIsRunning.set(bIsRunning);
    }

    public String getAppName() {
        return mstrAppName;
    }

    public int getRunCount() {
        return miRunCount;
    }
}
