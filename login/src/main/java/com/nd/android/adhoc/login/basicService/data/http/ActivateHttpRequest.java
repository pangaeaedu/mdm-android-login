package com.nd.android.adhoc.login.basicService.data.http;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActivateHttpRequest {
    public String mUserToken = "";

    public String mDeviceToken = "";
    @JsonProperty("type")
    public int mType = 1;

    public UcParams mParams = new UcParams();
}
