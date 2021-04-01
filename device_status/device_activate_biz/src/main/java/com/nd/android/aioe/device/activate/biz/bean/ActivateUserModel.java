package com.nd.android.aioe.device.activate.biz.bean;

import android.text.TextUtils;

import com.nd.android.aioe.device.activate.dao.api.bean.ActivateUserResult;

public class ActivateUserModel extends ActivateUserResult {

    public boolean isSuccess() {
        if (TextUtils.isEmpty(getResult())) {
            return false;
        }

        return getResult().equalsIgnoreCase("success");
    }
}
