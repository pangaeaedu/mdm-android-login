package com.nd.android.adhoc.communicate.request.interceptor;

import androidx.annotation.NonNull;

import com.nd.android.adhoc.basic.log.Logger;

import okhttp3.Request;

/**
 * Created by HuangYK on 2019/6/13.
 */

class AdhocRequestInfoStrategy_Post extends AdhocRequestInfoStrategyBasic {

    private static final String TAG = "AdhocRequestInfoStrategy_Post";

    @NonNull
    @Override
    public String getMethod() {
        return "POST";
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
