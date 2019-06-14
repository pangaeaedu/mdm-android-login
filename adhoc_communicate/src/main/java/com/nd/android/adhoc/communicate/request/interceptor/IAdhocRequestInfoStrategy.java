package com.nd.android.adhoc.communicate.request.interceptor;

import android.support.annotation.NonNull;

import okhttp3.Request;

/**
 * Created by HuangYK on 2019/6/13.
 */

interface IAdhocRequestInfoStrategy {

    @NonNull
    String getMethod();

    String makeContent(@NonNull Request pRequest);
}
