package com.nd.android.mdm.event_define;

/**
 * date: 2017/2/23 0023
 * author: cbs
 * 记录pad最近一次上报drms日志，包含是否成功，日期
 */

public class PushDrmsLogEvent {

    private long mLastReportTime;
    private boolean isSuccess;

    public PushDrmsLogEvent(long time, boolean isSuccess) {
        this.mLastReportTime = time;
        this.isSuccess = isSuccess;
    }

    public long getmLastReportTime() {
        return mLastReportTime;
    }

    public void setmLastReportTime(long mLastReportTime) {
        this.mLastReportTime = mLastReportTime;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
