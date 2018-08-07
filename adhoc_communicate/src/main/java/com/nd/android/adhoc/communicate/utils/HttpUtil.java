package com.nd.android.adhoc.communicate.utils;

import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.communicate.impl.MdmTransferFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;


/**
 * Created by yaoyue1019 on 8-19.
 */

public class HttpUtil {
    private static final String TAG = "HttpUtil";

    public static String post(String url, String content) {
        try {
            Logger.d(TAG, String.format("post json to %s %s", url, content));
            HttpPost httpPost = new HttpPost(url);
            StringEntity entity = new StringEntity(content, HTTP.UTF_8);
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
            httpPost.addHeader("Authorization", MdmEncodeUtil.encode(MdmTransferFactory.getPushModel().getDeviceId()));
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                String result = EntityUtils.toString(response.getEntity());
                Logger.d(TAG, result);
//                EventBus.getDefault().post(new PushDrmsLogEvent(System.currentTimeMillis(), true));
                return result;
            } else {
                Logger.e(TAG, "receive error code:" + response.getStatusLine().getStatusCode());
//                EventBus.getDefault().post(new PushDrmsLogEvent(System.currentTimeMillis(), false));
                return null;
            }
        } catch (Exception e) {
            Logger.e(TAG, e.toString());
            return null;
        }
    }

}
