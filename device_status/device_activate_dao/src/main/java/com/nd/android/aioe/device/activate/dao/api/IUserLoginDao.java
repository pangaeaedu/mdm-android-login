package com.nd.android.aioe.device.activate.dao.api;

import android.support.annotation.NonNull;

import com.nd.android.aioe.device.activate.dao.api.bean.ILoginUserResult;

public interface IUserLoginDao {

    ILoginUserResult login(@NonNull String pUsername, @NonNull String pPassword) throws Exception;

    ILoginUserResult loginUC(@NonNull String pUsername, @NonNull String pPassword, String pValidationCode, @NonNull String pUcOrgCode) throws Exception;


}
