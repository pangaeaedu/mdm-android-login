package com.nd.android.aioe.device.status.biz.api.judge;

public interface IDeviceStatusUpdateRetryJudger {

    boolean useLocalStatusAfterRetryFailed();

    void onSuccess();
}
