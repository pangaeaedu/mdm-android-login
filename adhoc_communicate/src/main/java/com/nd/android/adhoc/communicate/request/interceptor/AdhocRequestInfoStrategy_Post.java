package com.nd.android.adhoc.communicate.request.interceptor;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.common.util.AdhocDataCheckUtils;
import com.nd.android.adhoc.basic.log.Logger;

import org.json.JSONObject;

import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Created by HuangYK on 2019/6/13.
 */

 class AdhocRequestInfoStrategy_Post implements IAdhocRequestInfoStrategy {

    private static final String TAG = "AdhocRequestInfoStrategy_Post";


    @NonNull
    @Override
    public String getMethod() {
        return "POST";
    }

    @Override
    public String makeContent(@NonNull Request pRequest) {

//        ===== POST 请求 =====
//
//        request.url()
//        获取 请求 接口地址：pathSegments
//
//        获取请求参数：
//
//        request.body()


        HttpUrl httpUrl = pRequest.url();

        // url 后面的路径
        List<String> pathSegments = httpUrl.pathSegments();
        if (AdhocDataCheckUtils.isCollectionEmpty(pathSegments)) {
            return null;
        }


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("version", pathSegments.get(0));
            jsonObject.put("action", pathSegments.get(pathSegments.size() - 1));


            String bodyContent = AdhocRequestUtil.readRequestBody(pRequest);

            jsonObject.put("content", bodyContent);

            return jsonObject.toString();

        } catch (Exception e) {
            Logger.w(TAG, "make content json error: " + e);
        }

        return null;
    }
}
