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
            return new UcVerificationException(pActivateError.name());
        }

        if (pActivateError == ActivateUserError.UserBinded) {
            return new UserBindedException(pActivateError.name());
        }

        if (pActivateError == ActivateUserError.DeviceBinded) {
            return new DeviceBindedException(pActivateError.name());
        }

        if (pActivateError == ActivateUserError.OtherError) {
            return new SimOrOtherException(pActivateError.name());
        }

        return new QueryActivateUserUnknownException();
    }
}
