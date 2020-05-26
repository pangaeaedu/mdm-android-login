package com.nd.android.adhoc.db.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.db.constant.MdmRunInfoDbConstant;
import com.nd.android.adhoc.db.entity.intfc.IMdmRunInfoEntity;
import com.nd.android.adhoc.utils.AppRunInfoReportUtils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * APP运行信息 实体类
 * <p>
 * Created by linsj on 2018/3/28.
 */
@DatabaseTable(tableName = MdmRunInfoDbConstant.MDM_RUNINFO_TABLE_NAME)
public class MdmRunInfoEntity implements IMdmRunInfoEntity {
    private static final String TAG = "MdmRunInfoEntity";

    public static final String ID = "id";
    public static final String DAY_TIME_STAMP = "day_time_stamp";
    public static final String PACKAGE_NAME = "package_name";
    public static final String APP_NAME = "app_name";
    public static final String RUN_TIME = "run_time";
    public static final String RUN_COUNT = "run_count";

    @Expose
    @SerializedName("id")
    @DatabaseField(id = true, canBeNull = false, columnName = "id")
    public String mstrId;

    @Expose
    @SerializedName("day_time_stamp")
    @DatabaseField(canBeNull = false, columnName = DAY_TIME_STAMP)
    private long mlDayTimeStamp = AppRunInfoReportUtils.getCurrentDayTimeStamp();

    @Expose
    @SerializedName("package_name")
    @DatabaseField(canBeNull = false, columnName = PACKAGE_NAME)
    private String mstrPackageName;

    @Expose
    @SerializedName("app_name")
    @DatabaseField(columnName = APP_NAME)
    private String mstrAppName;

    @Expose
    @SerializedName("run_time")
    @DatabaseField(canBeNull = false, columnName = RUN_TIME)
    private long mlRunTime;

    @Expose
    @SerializedName("run_count")
    @DatabaseField(canBeNull = false, columnName = RUN_COUNT)
    private int miRunCount;


    /**表示监听回调app列表与上一次回调列表都包含了该app，用于差分比较，优化算法*/
    private boolean mbRunningListContainsThis;

    @Expose
    private AtomicBoolean mbIsRunning = new AtomicBoolean();

    /**最近一次打开时间，用于在关闭时计算打开时长*/
    @Expose
    private long mLastOpenTime;

    public MdmRunInfoEntity(){
    }

    public MdmRunInfoEntity(String strId, String strPackage, String strAppName){
        mstrId = strId;
        mstrPackageName = strPackage;
        mstrAppName = strAppName;
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

    public void setLastOpenTime(long lastOpenTime){
         mLastOpenTime = lastOpenTime;
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

    /**
     * 强制加时间，用于读缓存时
     * @param lDeadTimeToMinus
     */
    public void forceFillUseTime(long lDeadTimeToMinus){
        refreshUseTime(lDeadTimeToMinus);
    }

    private void refreshUseTime(long lDeadTimeToMinus){
        mlRunTime += lDeadTimeToMinus - mLastOpenTime;
        if(mlRunTime < 0){
            //负数一般是回调时间了。比如25号零点打开APP，又调回24号22点,
            //这个时候归零是不可能归零的，这辈子都不能归零，
            //就给他个五分钟运行时长，意思一下吧。
            mlRunTime = 300 * 1000;
            Logger.w(TAG, "force make a positive value");
        }
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
        mlDayTimeStamp = AppRunInfoReportUtils.getCurrentDayTimeStamp();
        mLastOpenTime = mlDayTimeStamp;
    }

    public boolean isMbRunningListContainsThis() {
        return mbRunningListContainsThis;
    }

    public void setMbRunningListContainsThis(boolean mbRunningListContainsThis) {
        this.mbRunningListContainsThis = mbRunningListContainsThis;
    }

    @Override
    public String getId() {
        return mstrId;
    }

    @Override
    public void setId(String strId) {
        mstrId = strId;
    }

    @Override
    public long getDayBeginTimeStamp() {
        return mlDayTimeStamp;
    }

    @Override
    public void setDayBeginTimeStamp(long lTimeStamp) {
        mlDayTimeStamp = lTimeStamp;
    }

    @Override
    public String getPackageName() {
        return mstrPackageName;
    }

    @Override
    public void setPackageName(String strPackageName) {
        mstrPackageName = strPackageName;
    }

    @Override
    public String getAppName() {
        return mstrAppName;
    }

    @Override
    public void setAppName(String strAppName) {
        mstrAppName = strAppName;
    }

    @Override
    public long getRunTime() {
        return mlRunTime;
    }

    @Override
    public void setRunTime(long lTime) {
        mlRunTime = lTime;
    }

    @Override
    public int getRunCount() {
        return miRunCount;
    }

    @Override
    public void setRunCount(int iRunCount) {
        miRunCount = iRunCount;
    }
}
