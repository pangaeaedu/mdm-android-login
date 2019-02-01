package com.nd.android.adhoc.login.processOptimization.utils;

import com.nd.android.adhoc.login.exception.DeviceBindedException;
import com.nd.android.adhoc.login.exception.SimOrOtherException;
import com.nd.android.adhoc.login.exception.UcVerificationException;
import com.nd.android.adhoc.login.exception.UserBindedException;
import com.nd.android.adhoc.login.processOptimization.ActivateUserError;
import com.nd.android.adhoc.loginapi.exception.QueryActivateUserUnknownException;

public class LoginExceptionUtils {

    public static Exception convertErrorToException(ActivateUserError pActivateError) {
        if (pActivateError == ActivateUserError.UserVerifyFailed) {
            return new UcVerificationException();
        }

        if (pActivateError == ActivateUserError.UserBinded) {
            return new UserBindedException();
        }

        if (pActivateError == ActivateUserError.DeviceBinded) {
            return new DeviceBindedException();
        }

        if (pActivateError == ActivateUserError.OtherError) {
            return new SimOrOtherException();
        }

        return new QueryActivateUserUnknownException();
    }
}
