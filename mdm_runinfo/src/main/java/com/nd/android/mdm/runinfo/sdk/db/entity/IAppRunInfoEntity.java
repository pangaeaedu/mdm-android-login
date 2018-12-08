package com.nd.android.mdm.runinfo.sdk.db.entity;

/**
 * Created by HuangYK on 2018/12/5.
 */

public interface IAppRunInfoEntity {

    String FIELD_ID = "package_name";
    String FIELD_PACKAGE_NAME = "package_name";
    String FIELD_HOUR = "hour";
    String FIELD_TIME = "time";
    String FIELD_COUNT = "count";
    String FIELD_RUN_DATE = "run_date";


    String getPackageName();

    void setPackageName(String packageName);

    Integer getHour();

    void setHour(Integer hour);

    Long getTime();

    void setTime(Long time);

    Integer getCount();

    void setCount(Integer count);


    Long getRunDate();

    void setRunDate(Long runDate);


}
