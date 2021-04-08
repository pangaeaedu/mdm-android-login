package com.nd.android.aioe.device.status.biz.api.judge;

public interface IDeviceIdConfirmRetryJudger {

    boolean isContinueRetryOnFailed();

    void onSuccess();
}
