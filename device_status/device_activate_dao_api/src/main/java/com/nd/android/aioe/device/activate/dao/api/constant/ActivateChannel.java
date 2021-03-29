package com.nd.android.aioe.device.activate.dao.api.constant;

public enum ActivateChannel {
    Uc("uc"),
    AutoLogin("autologin");

    private String mValue = "";

    ActivateChannel(String pValue) {
        mValue = pValue;
    }

    public String getValue() {
        return mValue;
    }
}
