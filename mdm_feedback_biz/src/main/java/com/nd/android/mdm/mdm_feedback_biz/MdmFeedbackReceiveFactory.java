package com.nd.android.mdm.mdm_feedback_biz;

import android.support.annotation.NonNull;
import android.support.v4.util.ArraySet;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.util.AdhocDataCheckUtils;

import org.json.JSONObject;

import java.util.Set;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public final class MdmFeedbackReceiveFactory {

    private static Set<IFeedbackOperator> sFeedbackOperatorSet = new ArraySet<>();

    public static void addCmdOperator(@NonNull IFeedbackOperator pOperator) {
        sFeedbackOperatorSet.add(pOperator);
    }

    static void doFeedbackReceived(@NonNull final String pCmdMsg) {
        if (AdhocDataCheckUtils.isCollectionEmpty(sFeedbackOperatorSet)
                || TextUtils.isEmpty(pCmdMsg)) {
            return;
        }

        startAction(new Action0() {
            @Override
            public void call() {
                try {
                    JSONObject object = new JSONObject(pCmdMsg);
                    String cmd = object.optString("cmd");

                    for (IFeedbackOperator operator : sFeedbackOperatorSet) {
                        if(operator.getCmdName().equalsIgnoreCase(cmd)){
                            operator.operate(pCmdMsg);
                        }
                    }
                } catch (Exception pE) {
                    pE.printStackTrace();
                }
            }
        });
    }

    private static void startAction(final Action0 action) {

        Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {

                        action.call();
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Void aVoid) {

                    }
                });
    }
}
