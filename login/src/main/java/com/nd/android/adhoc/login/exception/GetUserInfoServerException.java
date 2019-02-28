package com.nd.android.adhoc.login.exception;

import com.nd.android.adhoc.loginapi.exception.BaseInitException;

public class GetUserInfoServerException extends BaseInitException {
    public GetUserInfoServerException(String pMsg) {
        super("GetUserInfoServerException:"+pMsg);
    }

    public GetUserInfoServerException(int pCode, String pMsg) {
        super(pCode, "GetUserInfoServerException:"+pMsg);
    }
}
