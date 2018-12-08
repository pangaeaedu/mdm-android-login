package com.nd.android.mdm.runinfo.sdk.db.entity;

/**
 * Created by HuangYK on 2018/12/5.
 */

public final  class AppRunInfoEntityHelper {


    public static IAppRunInfoEntity newAppRunInfoEntity(String packageName, Integer hour, Long duration, Integer executeCount, Long runTime) {
        return new AppRunInfoEntity(packageName, hour, duration, executeCount, runTime);
    }


    public static Class<? extends IAppRunInfoEntity> getAppRunInfoEntityClass() {
        return AppRunInfoEntity.class;
    }

}
