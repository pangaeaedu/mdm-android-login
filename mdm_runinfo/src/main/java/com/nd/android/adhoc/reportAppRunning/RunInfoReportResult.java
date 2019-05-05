package com.nd.android.adhoc.reportAppRunning;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by linsj on 2019/03/26.
 *  上报结果
 */
public class RunInfoReportResult {
    @Expose
    @SerializedName("errcode")
    private int miErrorCode;

    @Expose
    @SerializedName("device_token")
    private String mstrDeviceToken;

    public int getMiErrorCode() {
        return miErrorCode;
    }

    public String getMstrDeviceToken() {
        return mstrDeviceToken;
    }
}
