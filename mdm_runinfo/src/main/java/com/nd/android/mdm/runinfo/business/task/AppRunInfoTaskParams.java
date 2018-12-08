package com.nd.android.mdm.runinfo.business.task;

/**
 * Created by HuangYK on 2018/12/7.
 */

public class AppRunInfoTaskParams {

    private String mPackageName;

    private long mUpdateTime;

    private boolean mIsNewInfo;

    public AppRunInfoTaskParams(String packageName, long updateTime, boolean isNewInfo) {
        mPackageName = packageName;
        mUpdateTime = updateTime;
        mIsNewInfo = isNewInfo;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public long getUpdateTime() {
        return mUpdateTime;
    }

    public boolean isNewInfo() {
        return mIsNewInfo;
    }
}
