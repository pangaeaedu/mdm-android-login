package com.nd.android.adhoc.communicate.request.interceptor;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.log.Logger;

import okhttp3.Request;

/**
 * Created by HuangYK on 2020/12/17.
 */

class AdhocRequestInfoStrategy_Put extends AdhocRequestInfoStrategyBasic {

    private static final String TAG = "AdhocRequestInfoStrategy_Put";

    @NonNull
    @Override
    public String getMethod() {
        return "PUT";
    }


    @NonNull
    @Override
    protected String getContent(@NonNull Request pRequest) {
        try {
            return AdhocRequestUtil.readRequestBody(pRequest);
        } catch (Exception e) {
            Logger.e(TAG, "getContent error: " + e);
        }

        return "";
    }
}
