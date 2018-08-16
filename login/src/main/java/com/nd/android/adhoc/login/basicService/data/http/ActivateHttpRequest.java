package com.nd.android.adhoc.login.basicService.data.http;

public class ActivateHttpRequest {
    public String user_token = "";

    public String device_token = "";
    public int type = 1;

    public UcParams uc = new UcParams();
}
