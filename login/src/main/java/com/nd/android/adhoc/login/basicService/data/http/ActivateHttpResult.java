package com.nd.android.adhoc.login.basicService.data.http;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActivateHttpResult {
    @JsonProperty("result")
    public String mResult = "";

    @JsonProperty("requestid")
    public String mSessionID = "";
}
