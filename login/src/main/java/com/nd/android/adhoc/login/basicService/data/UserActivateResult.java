package com.nd.android.adhoc.login.basicService.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserActivateResult {

    @JsonProperty("cmd")
    public String mCmd = "";

    @JsonProperty("sessionid")
    public String mSessionID = "";

    @JsonProperty()
    public int mMsgType = 1;

    public int mErrorCode = 1;

    public UserActivateData mData = null;


}
