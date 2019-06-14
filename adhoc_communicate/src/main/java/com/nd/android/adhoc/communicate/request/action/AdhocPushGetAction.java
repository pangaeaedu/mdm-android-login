package com.nd.android.adhoc.communicate.request.action;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nd.android.adhoc.basic.net.dao.action.IAdhocHttpGetAction;
import com.nd.android.adhoc.basic.net.exception.AdhocHttpException;
import com.nd.android.adhoc.communicate.impl.MdmTransferFactory;

import java.util.Map;
import java.util.UUID;

/**
 * Created by HuangYK on 2019/6/4.
 */

public class AdhocPushGetAction implements IAdhocHttpGetAction {


    @Override
    public String getBaseUrl() {
        return null;
    }

    @Override
    public <R> R get(@NonNull String pApi, @NonNull Class<R> pResultClass) throws AdhocHttpException {
        return get(pApi, pResultClass, null);
    }

    @Override
    public <R> R get(@NonNull String pApi, @NonNull Class<R> pResultClass, Map<String, Object> pParams) throws AdhocHttpException {
        return get(pApi, pResultClass, pParams, null);
    }

    @Override
    public <R> R get(@NonNull String pApi, @NonNull Class<R> pResultClass, Map<String, Object> pParams, @Nullable Map<String, String> pHeaders) throws AdhocHttpException {



        MdmTransferFactory.getPushModel().sendUpStreamMsg(UUID.randomUUID().toString(),10,"","");

        return null;
    }
}
