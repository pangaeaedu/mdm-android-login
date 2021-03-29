package com.nd.android.aioe.device.status.biz.api;

public class ActivateConfig {

    private int mLoginType = 0;
    private int mNeedGroup = 0;
    private String mGroupCode = "";
    private int mActivateRealType = 0;


    public ActivateConfig(@ActivateTypeRange int pLoginType, int pNeedGroup, int pActivateRealType) {
        mLoginType = pLoginType;
        mNeedGroup = pNeedGroup;
        mActivateRealType = pActivateRealType;
    }

    public ActivateConfig(@ActivateTypeRange int pLoginType, int pNeedGroup, int pActivateRealType, String pGroupCode) {
        mLoginType = pLoginType;
        mNeedGroup = pNeedGroup;
        mActivateRealType = pActivateRealType;
        mGroupCode = pGroupCode;
    }

    public int getAutoLogin() {
        if (mLoginType == ActivateType.TYPE_AUTO) {
            return 1;
        }

        return 0;
    }

    public int getLoginType() {
        return mLoginType;
    }

    public boolean isAutoLogin() {
        return getAutoLogin() == 1;
    }

    public int getActivateRealType() {
        return mActivateRealType;
    }

    public int getNeedGroup() {
        return mNeedGroup;
    }

    public String getGroupCode() {
        return mGroupCode;
    }
}
