package com.nd.android.adhoc.login.basicService.data.http;

/**
 * Created by Administrator on 2019/9/19 0019.
 */

public class EnrollUserInfo {
    private String device_token = "";
    private long type = 0;
    private String assetcode = "";

    public EnrollUserInfo(String strDeviceToken, long lType, String strAssetCode){
        device_token = strDeviceToken;
        type = lType;
        assetcode = strAssetCode;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public String getAssetcode() {
        return assetcode;
    }

    public void setAssetcode(String assetcode) {
        this.assetcode = assetcode;
    }
}
