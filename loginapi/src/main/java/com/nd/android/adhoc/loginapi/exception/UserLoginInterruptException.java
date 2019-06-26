package com.nd.android.adhoc.loginapi.exception;

import android.content.Context;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.loginapi.R;

public class UserLoginInterruptException extends Exception {
    @Override
    public String getMessage() {
        return getContext().getString(R.string.exception_check_user_type);
    }

    protected Context getContext(){
        return AdhocBasicConfig.getInstance().getAppContext();
    }
}
