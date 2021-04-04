package com.nd.android.aioe.device.activate.biz.api.constant;

public enum ActivateRealType {

    NORMAL(11),
    EGYPT_OR_SAAS(12);

    private int mValue;

    ActivateRealType(int value) {
        mValue = value;
    }

    public int getValue() {
        return mValue;
    }
}
