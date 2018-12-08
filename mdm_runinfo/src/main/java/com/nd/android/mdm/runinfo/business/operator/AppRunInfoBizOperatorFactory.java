package com.nd.android.mdm.runinfo.business.operator;


/**
 * Created by HuangYK on 2018/12/6.
 */

public final class AppRunInfoBizOperatorFactory {


    private volatile static AppRunInfoBizOperatorFactory sInstance = null;


    private IAppRunInfoBizOperator mAppRunInfoBizOperator;

    private final byte[] mAppExecutionDbLock = new byte[0];


    public static AppRunInfoBizOperatorFactory getInstance() {
        if (sInstance == null) {
            synchronized (AppRunInfoBizOperatorFactory.class) {
                if (sInstance == null) {
                    sInstance = new AppRunInfoBizOperatorFactory();
                }
            }
        }
        return sInstance;
    }

    public IAppRunInfoBizOperator getAppRunInfoBizOperator() {
        if (mAppRunInfoBizOperator == null) {
            synchronized (mAppExecutionDbLock) {
                if (mAppRunInfoBizOperator == null) {
                    mAppRunInfoBizOperator = new AppRunInfoBizOperator();
                }
            }
        }
        return mAppRunInfoBizOperator;
    }
}
