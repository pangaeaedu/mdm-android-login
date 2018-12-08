package com.nd.android.mdm.runinfo.sdk.db.operator;


import com.nd.android.mdm.runinfo.sdk.db.constant.AppRunInfoConstant;

/**
 * Created by HuangYK on 2018/12/6.
 */

public final class AppRunInfoDbOperatorFactory {


    private volatile static AppRunInfoDbOperatorFactory sInstance = null;


    private IAppRunInfoDbOperator mAppExecutionDbOperator;

    private final byte[] mAppExecutionDbLock = new byte[0];


    public static AppRunInfoDbOperatorFactory getInstance() {
        if (sInstance == null) {
            synchronized (AppRunInfoDbOperatorFactory.class) {
                if (sInstance == null) {
                    sInstance = new AppRunInfoDbOperatorFactory();
                }
            }
        }
        return sInstance;
    }

    public IAppRunInfoDbOperator getAppExecutionDbOperator() {
        if (mAppExecutionDbOperator == null) {
            synchronized (mAppExecutionDbLock) {
                if (mAppExecutionDbOperator == null) {
                    mAppExecutionDbOperator = new AppRunInfoDbOperator(AppRunInfoConstant.DB_NAME);
                }
            }
        }
        return mAppExecutionDbOperator;
    }
}
