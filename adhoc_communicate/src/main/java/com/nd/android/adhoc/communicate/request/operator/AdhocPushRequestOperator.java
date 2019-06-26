package com.nd.android.adhoc.communicate.request.operator;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.log.CrashAnalytics;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.communicate.constant.AdhocCommunicateConstant;
import com.nd.android.adhoc.communicate.impl.MdmTransferFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * Created by HuangYK on 2019/6/13.
 */

public class AdhocPushRequestOperator {

    private static final String TAG = "AdhocPushRequestOperator";

    private static final int DEFAULT_ERROR_CODE = -9999;

    private static PublishSubject<String> mPushFeedbackSub = PublishSubject.create();


    /**
     * 执行请求
     *
     * @param msgid       消息ID，如果不传，默认随机生成 UUID
     * @param ttlSeconds  超时时间，必填
     * @param contentType 内容类型，选填
     * @param content     请求内容，必填
     * @return Observable<Response>
     */
    public static Observable<Response> doRequest(String msgid, final long ttlSeconds, final String contentType, @NonNull final String content) {
        if (TextUtils.isEmpty(msgid)) {
            msgid = UUID.randomUUID().toString();
        }

        Logger.d(TAG, "doRequest: msgid = " + msgid + ", content = " + content);

        final String finalMsgid = msgid;
        return mPushFeedbackSub.asObservable()
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String result) {

                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String resultMsgId = jsonObject.optString("message_id");
                            // message_id 在 请求的列表中，才返回 true，继续执行，并且移除自身

                            if (finalMsgid.equals(resultMsgId)) {
                                return true;
                            }

                        } catch (JSONException e) {
                            Logger.w(TAG, "doRequest, on filter error: " + e);
                        }

                        return false;
                    }
                })
//                .map(new Func1<String, String>() {
//                    @Override
//                    public String call(String result) {
//                        // 内容为空，直接过滤掉
//                        if (TextUtils.isEmpty(result)) {
//                            return null;
//                        }
//
//                        try {
//                            JSONObject jsonObject = new JSONObject(result);
//                            String resultMsgId = jsonObject.optString("message_id");
//                            // message_id 在 请求的列表中，才返回 true，继续执行，并且移除自身
//
//                            if (finalMsgid.equals(resultMsgId)) {
//                                return result;
//                            }
//
//                        } catch (JSONException e) {
//                            Logger.w(TAG, "doRequest, on filter error: " + e);
//                        }
//
//                        return null;
//                    }
//                })
//                // 规定时间内还没有收到 有效的请求，就按照超时处理
                .timeout(ttlSeconds, TimeUnit.SECONDS)
                // 这里把返回的结果转为 Response
                .map(new Func1<String, Response>() {
                    @Override
                    public Response call(String result) {

                        String resultContent = "";
                        String message;
                        int code;

                        try {

                            if (TextUtils.isEmpty(result)) {
                                code = -9999;
                                message = "result is null";
                            } else {
                                JSONObject jsonObject = new JSONObject(result);
                                resultContent = jsonObject.optString("content");
                                code = jsonObject.optInt("code", DEFAULT_ERROR_CODE);
                                message = jsonObject.optString("message");
                                if (DEFAULT_ERROR_CODE == code && TextUtils.isEmpty(message)) {
                                    message = "unknow result message...";
                                }
                            }

                            Logger.d(TAG, "message_id = " + finalMsgid + ", code = " + code + ", message = " + message + ", resultContent = " + result);

                            Response.Builder builder = new Response.Builder();
                            builder.body(ResponseBody.create(MediaType.parse("application/json; charset=UTF-8"), resultContent))
                                    .code(code)
                                    .message(message)
                                    .protocol(Protocol.HTTP_1_1)
                                    .request(new Request.Builder().url("http://localhost/").build());

                            return builder.build();
                        } catch (Exception e) {
                            Logger.w(TAG, "parsing result error: " + e);

                            Response.Builder builder = new Response.Builder();
                            builder.body(ResponseBody.create(MediaType.parse("application/json; charset=UTF-8"), result))
                                    .code(DEFAULT_ERROR_CODE)
                                    .message("parsing result error: " + e)
                                    .protocol(Protocol.HTTP_1_1)
                                    .request(new Request.Builder().url("http://localhost/").build());

                            return builder.build();

                        }
                    }
                })
                // 订阅之后再发起请求，以免 先发起再订阅，会丢失返回结果
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        MdmTransferFactory.getPushModel().sendUpStreamMsg(finalMsgid, ttlSeconds, contentType, content);
                    }
                });
    }


    static void receiveFeedback(String pContent) {
        if (TextUtils.isEmpty(pContent)) {
            CrashAnalytics.INSTANCE.reportException(
                    AdhocCommunicateConstant.CMP_NAME,
                    String.valueOf(DEFAULT_ERROR_CODE),
                    "receiveFeedback content is empty",
                    new Exception("receiveFeedback content is empty"),
                    null);
            return;
        }
        mPushFeedbackSub.onNext(pContent);
    }
}
