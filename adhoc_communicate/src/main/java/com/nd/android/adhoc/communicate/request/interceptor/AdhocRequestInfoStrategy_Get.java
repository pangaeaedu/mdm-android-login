package com.nd.android.adhoc.communicate.request.interceptor;

import android.support.annotation.NonNull;
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

//    @Override
//    public String makeContent(@NonNull Request pRequest) {
//
////         ===== GET 请求 =====
////
////        * 获取 请求 接口地址：
////        request.url()
////          - pathSegments
////
////          * 获取请求参数：
////        request.url()
////          - queryParamenterNamesAndValues
////          - queryParamenterNames()
////          - queryParamenter(“name”)
////
//        HttpUrl httpUrl = pRequest.url();
//
//        // url 后面的路径
//        List<String> pathSegments = httpUrl.pathSegments();
//        if (AdhocDataCheckUtils.isCollectionEmpty(pathSegments)) {
//            return null;
//        }
//
//        StringBuilder action = new StringBuilder();
//        for (String pathSegment : pathSegments) {
//            action.append("/").append(pathSegment);
//        }
//
//
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("action", action.toString());
//
//            // ===== start 拼接 headers ======
//            Set<String> headersNames = pRequest.headers().names();
//
//            JSONObject headers = new JSONObject();
//            if (!AdhocDataCheckUtils.isCollectionEmpty(headersNames)) {
//                for (String headersName : headersNames) {
//                    if (TextUtils.isEmpty(headersName)) {
//                        continue;
//                    }
//                    headers.put(headersName, pRequest.headers().get(headersName));
//                }
//            }
//            jsonObject.put("headers", headers.toString());
//            // ===== finish 拼接 headers ======
//
//
//            // ===== start 拼接 content ======
//            Set<String> parameterNames = httpUrl.queryParameterNames();
//
//            JSONObject content = new JSONObject();
//            if (!AdhocDataCheckUtils.isCollectionEmpty(parameterNames)) {
//                for (String parameterName : parameterNames) {
//                    if (TextUtils.isEmpty(parameterName)) {
//                        continue;
//                    }
//                    content.put(parameterName, httpUrl.queryParameter(parameterName));
//                }
//            }
//            jsonObject.put("content", content.toString());
//            // ===== finish 拼接 content ======
//
//            return jsonObject.toString();
//
//        } catch (Exception e) {
//            Logger.w(TAG, "make content json error: " + e);
//        }
//
//        return null;
//    }

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
        } catch (JSONException e) {
            Logger.e(TAG, "getContent error: " + e);
        }
        return "";
    }


}
