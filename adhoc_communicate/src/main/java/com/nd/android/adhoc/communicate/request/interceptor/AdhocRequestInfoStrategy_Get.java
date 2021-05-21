package com.nd.android.adhoc.communicate.request.interceptor;

import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.util.AdhocDataCheckUtils;
import com.nd.android.adhoc.basic.log.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Created by HuangYK on 2019/6/13.
 */

class AdhocRequestInfoStrategy_Get extends AdhocRequestInfoStrategyBasic {

    private static final String TAG = "AdhocRequestInfoStrategy_Get";

    @NonNull
    @Override
    public String getMethod() {
        return "GET";
    }

    @NonNull
    @Override
    protected String getContent(@NonNull Request pRequest) {
        HttpUrl httpUrl = pRequest.url();
        Set<String> parameterNames = httpUrl.queryParameterNames();

        try {
            JSONObject content = new JSONObject();
            if (!AdhocDataCheckUtils.isCollectionEmpty(parameterNames)) {
                for (String parameterName : parameterNames) {
                    if (TextUtils.isEmpty(parameterName)) {
                        continue;
                    }
                    content.put(parameterName, httpUrl.queryParameter(parameterName));
                }
            }
            return content.toString();
        } catch (JSONException e) {
            Logger.e(TAG, "getContent error: " + e);
        }
        return "";
    }


}
