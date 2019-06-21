package com.nd.android.adhoc.communicate.request.interceptor;

import android.support.annotation.NonNull;

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

//    @Override
//    public String makeContent(@NonNull Request pRequest) {
//
////        ===== POST 请求 =====
////
////        request.url()
////        获取 请求 接口地址：pathSegments
////
////        获取请求参数：
////
////        request.body()
//
//
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
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("action", action.toString());
//
//            String bodyContent = AdhocRequestUtil.readRequestBody(pRequest);
//
//            jsonObject.put("content", bodyContent);
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
        try {
            return AdhocRequestUtil.readRequestBody(pRequest);
        } catch (Exception e) {
            Logger.e(TAG, "getContent error: " + e);
        }

        return "";
    }
}
