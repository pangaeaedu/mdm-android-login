package com.nd.android.adhoc.communicate.request.interceptor;

import android.text.TextUtils;

import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.communicate.impl.MdmTransferFactory;
import com.nd.android.adhoc.communicate.request.operator.AdhocPushRequestOperator;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by HuangYK on 2019/6/13.
 */

class AdhocPushOptInterceptor implements Interceptor {

    private static final String TAG = "AdhocPushOptInterceptor";

    private static final List<IAdhocRequestInfoStrategy> sRequestInfoStrategies =
            new CopyOnWriteArrayList<IAdhocRequestInfoStrategy>() {
                {
                    add(new AdhocRequestInfoStrategy_Get());
                    add(new AdhocRequestInfoStrategy_Post());
                }
            };


    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        // 如果不需要走 push，直接走原先的 通道
        if(!MdmTransferFactory.getPushChannel()){
            return chain.proceed(request);
        }


        final String method = request.method();
        if (TextUtils.isEmpty(method)) {
            return null;
        }

        String content = null;

        for (IAdhocRequestInfoStrategy requestInfoStrategy : sRequestInfoStrategies) {
            if (method.equalsIgnoreCase(requestInfoStrategy.getMethod())) {
                content = requestInfoStrategy.makeContent(request);
                break;
            }
        }

        if (TextUtils.isEmpty(content)) {
            Logger.w(TAG, "intercept，makeContent failed");
            return null;
        }

        return AdhocPushRequestOperator.doRequest(UUID.randomUUID().toString(), 20, "", content).toBlocking().first();
    }
}
