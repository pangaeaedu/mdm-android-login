package com.nd.android.mdm.runinfo.sdk.db.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by HuangYK on 2018/12/5.
 */

@DatabaseTable(tableName = "app_execution_info")
class AppRunInfoEntity implements IAppRunInfoEntity {


    @DatabaseField(canBeNull = false, id = true, columnName = FIELD_ID, generatedId = true)
    private Long mId;

    // 包名
    @DatabaseField(canBeNull = false, columnName = FIELD_PACKAGE_NAME)
    private String mPackageName;

    // 小时，按时段划分
    @DatabaseField(canBeNull = false, columnName = FIELD_HOUR)
    private Integer mHour;

    // 持续时间
    @DatabaseField(canBeNull = false, columnName = FIELD_TIME)
    private Long mTime;

    // 执行次数
    @DatabaseField(canBeNull = false, columnName = FIELD_COUNT)
    private Integer mExecuteCount;

    // 更新时间
    @DatabaseField(canBeNull = false, columnName = FIELD_RUN_DATE)
    private Long mRunDate;

    AppRunInfoEntity(String packageName, Integer hour, Long time, Integer executeCount, Long runDate) {
        mPackageName = packageName;
        mHour = hour;
        mTime = time;
        mExecuteCount = executeCount;
        mRunDate = runDate;
    }

    @Override
    public String getPackageName() {
        return mPackageName;
    }

    @Override
    public void setPackageName(String packageName) {
        mPackageName = packageName;
    }

    @Override
    public Integer getHour() {
        return mHour;
    }

    @Override
    public void setHour(Integer hour) {
        mHour = hour;
    }

    @Override
    public Long getTime() {
        return mTime;
    }

    @Override
    public void setTime(Long time) {
        mTime = time;
    }

    @Override
    public Integer getCount() {
        return mExecuteCount;
    }

    @Override
    public void setCount(Integer executeCount) {
        mExecuteCount = executeCount;
    }

    @Override
    public Long getRunDate() {
        return mRunDate;
    }

    @Override
    public void setRunDate(Long runDate) {
        mRunDate = runDate;
    }
}
