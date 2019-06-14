package com.nd.android.adhoc.communicate.request.interceptor;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.net.dao.interceptor.IAdhocRequestInterceptor;
import com.nd.sdp.android.serviceloader.annotation.Service;

import okhttp3.Interceptor;

/**
 * Created by HuangYK on 2019/6/14.
 */

@Service(IAdhocRequestInterceptor.class)
public class AdhocRequestInterceptor_Push implements IAdhocRequestInterceptor {

    @NonNull
    @Override
    public Interceptor getRequestInterceptor() {
        return new AdhocPushOptInterceptor();
    }
}
