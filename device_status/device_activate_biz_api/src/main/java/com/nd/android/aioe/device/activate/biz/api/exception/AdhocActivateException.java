package com.nd.android.aioe.device.activate.biz.api.exception;

import com.nd.android.adhoc.basic.common.exception.AdhocException;

public class AdhocActivateException extends AdhocException {

    public AdhocActivateException(String pMessage, int pErrorCode) {
        super(pMessage, pErrorCode);
    }
}
