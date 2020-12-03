package com.nd.android.adhoc.login.processOptimization.login;

import android.util.Log;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceIDEncryptUtils;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.login.basicService.http.UserTypeDao;
import com.nd.android.adhoc.loginapi.IUserLoginInterceptor;
import com.nd.android.mdm.biz.env.IMdmEnvModule;
import com.nd.android.mdm.biz.env.MdmEvnFactory;

public abstract class BaseUserLoginInterceptor implements IUserLoginInterceptor {

    @Override
    public boolean isNeedContinueLogin(String pRawUserName, String pRawPassword) throws Exception {
        Logger.e("UserIntercept", "need");
        UserTypeDao dao = new UserTypeDao(getBaseUrl());
        String encryptUserName = DeviceIDEncryptUtils.encrypt(pRawUserName);
        String encryptPassword = DeviceIDEncryptUtils.encryptPassword(pRawPassword);

        String type = dao.getUserType(encryptUserName, encryptPassword);
        return isThisUserTypeRight(type);
    }

    protected abstract boolean isThisUserTypeRight(String pUserType);

    private String getBaseUrl() {
        IMdmEnvModule module = MdmEvnFactory.getInstance().getCurEnvironment();
        return module.getUrl();
    }
}
