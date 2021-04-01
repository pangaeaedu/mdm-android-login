package com.nd.android.aioe.device.activate.biz.api;

import java.util.concurrent.atomic.AtomicBoolean;

public final class ActivateConfig {

    private int mLoginType = 0;
    private int mNeedGroup = 0;
    private String mGroupCode = "";
    private int mActivateRealType = 0;

    private volatile static ActivateConfig sInstance = null;

    private final AtomicBoolean mIsInited = new AtomicBoolean(false);


    public static ActivateConfig getInstance() {
        if (sInstance == null) {
            synchronized (ActivateConfig.class) {
                if (sInstance == null) {
                    sInstance = new ActivateConfig();
                }
            }
        }
        return sInstance;
    }

    public void init(@ActivateTypeRange int pLoginType, int pNeedGroup, int pActivateRealType) {
        init(pLoginType, pNeedGroup, pActivateRealType, "");
    }

    public void init(@ActivateTypeRange int pLoginType, int pNeedGroup, int pActivateRealType, String pGroupCode) {
        if (!mIsInited.compareAndSet(false, true)) {
            return;
        }

        mLoginType = pLoginType;
        mNeedGroup = pNeedGroup;
        mActivateRealType = pActivateRealType;
        mGroupCode = pGroupCode;

    }

    public boolean checkInited() {
        return mIsInited.get();
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
        return checkInited() && getAutoLogin() == 1;
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
