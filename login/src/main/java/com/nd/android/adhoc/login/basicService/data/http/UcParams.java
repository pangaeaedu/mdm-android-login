package com.nd.android.adhoc.login.basicService.data.http;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UcParams {
    @JsonProperty("access_token")
    public String mAccessToken = "";

    @JsonProperty("mac")
    public String mMac = "";

    @JsonProperty("nonce")
    public String mNonce = "";
}
