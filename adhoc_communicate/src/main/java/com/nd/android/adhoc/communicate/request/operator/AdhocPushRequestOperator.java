package com.nd.android.adhoc.communicate.request.operator;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.communicate.impl.MdmTransferFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Response;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * Created by HuangYK on 2019/6/13.
 */

public class AdhocPushRequestOperator {


    private static final String TAG = "AdhocPushRequestOperator";

    private static List<String> mRequestIds = new CopyOnWriteArrayList<>();

    private static PublishSubject<String> mPushFeedbackSub = PublishSubject.create();


    public static Observable<Response> doRequest(String msgid, final long ttlSeconds, final String contentType, @NonNull final String content) {
        if (TextUtils.isEmpty(msgid)) {
            msgid = UUID.randomUUID().toString();
        }

        mRequestIds.add(msgid);

        final String finalMsgid = msgid;
        return mPushFeedbackSub.asObservable()
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String content) {
                        // HYK: 这里要去解析判断 返回的 消息 id，是否在 mRequestIds 队列当中

                        // 内容为空，直接过滤掉
                        if (TextUtils.isEmpty(content)) {
                            return false;
                        }

                        try {
                            JSONObject jsonObject = new JSONObject(content);

                            String resultMsgId = jsonObject.optString("msgid");

                            // msgid 在 请求的列表中，才返回 true，继续执行，并且移除自身

                            if (mRequestIds.contains(resultMsgId)) {
                                mRequestIds.remove(resultMsgId);
                                return true;
                            }

                        } catch (JSONException e) {
                            Logger.w(TAG, "doRequest, on filter error: " + e);
                        }

                        return false;

                    }
                })
                // 规定时间内还没有收到 有效的请求，就按照超时处理
                .timeout(ttlSeconds, TimeUnit.SECONDS)
                // 这里把返回的结果转为 Response
                .map(new Func1<String, Response>() {
                    @Override
                    public Response call(String s) {


                        return null;
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


    public static void receiveFeedback(String pContent) {
        mPushFeedbackSub.onNext(pContent);
    }


//    try
//    {
//        Response<ResponseBody> response = call.execute();
//        if (response.isSuccessful()) {
//            String resultStr = response.body().string();
//            R resultModel;
//            if (String.class.equals(pResultClass)) {
//                resultModel = (R) resultStr;
//            } else {
//                Gson gson = new GsonBuilder().create();
//                resultModel = gson.fromJson(resultStr, pResultClass);
//            }
//
//            return resultModel;
//        }
//
//        ResponseBody body = response.body();
//        if (body == null || TextUtils.isEmpty(body.string())) {
//            throw new AdhocHttpException(response.message(), response.code());
//        } else {
//            throw new AdhocHttpException(body.string(), response.code());
//        }
//
//    } catch(RuntimeException |
//    IOException e)
//
//    {
//        throw new AdhocHttpException(e.getMessage(), AhdocHttpConstants.ADHOC_HTTP_ERROR);
//    }
}
