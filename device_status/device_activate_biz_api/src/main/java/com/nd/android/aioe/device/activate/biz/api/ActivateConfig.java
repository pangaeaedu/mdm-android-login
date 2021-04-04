package com.nd.android.aioe.device.activate.biz.api;

import com.nd.android.aioe.device.activate.biz.api.constant.ActivateRealType;
import com.nd.android.aioe.device.activate.biz.api.constant.ActivateType;
import com.nd.android.aioe.device.activate.biz.api.constant.ActivateTypeRange;

import java.util.concurrent.atomic.AtomicBoolean;

public final class ActivateConfig {

    private int mLoginType = ActivateType.TYPE_AUTO;
    private int mNeedGroup = 0;
    private String mGroupCode = "";

    //"realtype":11,  // 11=不需要用户登陆(pc)，12=egypt/saas化，不填使用默认激活方式，非必填

    private ActivateRealType mActivateRealType = ActivateRealType.NORMAL;

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

    public void init(@ActivateTypeRange int pLoginType, int pNeedGroup, ActivateRealType pActivateRealType) {
        init(pLoginType, pNeedGroup, pActivateRealType, "");
    }

    public void init(@ActivateTypeRange int pLoginType, int pNeedGroup, ActivateRealType pActivateRealType, String pGroupCode) {
        if (!mIsInited.compareAndSet(false, true)) {
            return;
        }

        mLoginType = pLoginType;
        mNeedGroup = pNeedGroup;
        mActivateRealType = pActivateRealType == null ? ActivateRealType.NORMAL : pActivateRealType;
        mGroupCode = pGroupCode;

    }

    public boolean checkInited() {
        return mIsInited.get();
    }

    public int getLoginType() {
        return mLoginType;
    }

    public boolean isAutoLogin() {
        return checkInited() && getLoginType() == ActivateType.TYPE_AUTO;
    }

    public int getActivateRealType() {
        return mActivateRealType.getValue();
    }

    public int getNeedGroup() {
        return mNeedGroup;
    }

    public String getGroupCode() {
        return mGroupCode;
    }
}
