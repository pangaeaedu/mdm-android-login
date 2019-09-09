package com.nd.android.adhoc.communicate.request.interceptor;

import android.text.TextUtils;

import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.communicate.impl.MdmTransferConfig;
import com.nd.android.adhoc.communicate.request.constant.AdhocNetworkChannel;
import com.nd.android.adhoc.communicate.request.operator.AdhocPushRequestOperator;
import com.nd.android.adhoc.communicate.request.operator.PushFakeResponseManager;

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

    private static final List<AdhocRequestInfoStrategyBasic> sRequestInfoStrategies =
            new CopyOnWriteArrayList<AdhocRequestInfoStrategyBasic>() {
                {
                    add(new AdhocRequestInfoStrategy_Get());
                    add(new AdhocRequestInfoStrategy_Post());
                }
            };


    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        // 如果不需要走 push，直接走原先的 通道
        if (AdhocNetworkChannel.CHANNEL_HTTP == MdmTransferConfig.getNetworkChannel()) {
            return chain.proceed(request);
        }


        final String method = request.method();
        if (TextUtils.isEmpty(method)) {
            return null;
        }
        String content = null;

        for (AdhocRequestInfoStrategyBasic requestInfoStrategy : sRequestInfoStrategies) {
            if (method.equalsIgnoreCase(requestInfoStrategy.getMethod())) {
                content = requestInfoStrategy.makeContent(request);
                break;
            }
        }

        if (TextUtils.isEmpty(content)) {
            Logger.w(TAG, "intercept，makeContent failed");
            return null;
        }


        String msgID = UUID.randomUUID().toString();
        PushFakeResponseManager.getInstance().addRequest(msgID, content);

        return AdhocPushRequestOperator.doRequest(msgID, MdmTransferConfig.getRequestTimeout(), "",
                content).toBlocking().first();
    }
}
