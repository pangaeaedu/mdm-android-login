package com.nd.android.mdm.appusage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.WindowManager;

import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.ui.activity.AdhocRequestActivity;
import com.nd.android.adhoc.basic.util.thread.AdhocRxJavaUtil;
import com.nd.android.mdm.runinfo.R;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by HuangYK on 2020/7/14.
 */
public class AppUsageRequestActivity extends Activity {
    private static final String TAG = "AppUsageRequestActivity";


    private static AdhocRequestActivity.IResultCallback sCallback;

    private Subscription mTimerSub;

    private static AtomicBoolean sRequested = new AtomicBoolean(false);

    public static void startRequest(@NonNull Context context, @NonNull AdhocRequestActivity.IResultCallback callback) {
        Logger.d(TAG, "startRequest");
        Intent intent = new Intent(context, AppUsageRequestActivity.class);
        sCallback = callback;

        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);

        Logger.d(TAG, "after startActivity");
    }

    @Override
    public void onCreate(Bundle bundle) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        super.onCreate(bundle);
        Logger.d(TAG, "onCreate");
        setContentView(R.layout.adhoc_activity_request);

        AdhocRxJavaUtil.doUnsubscribe(mTimerSub);
        mTimerSub = rx.Observable.timer(20, TimeUnit.SECONDS).subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Long aLong) {
                notifyFailed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 有权限，直接通知回去
        if (AppUsagePermissionUtil.checkAppUsagePermission()) {
            if (sCallback != null) {
                sCallback.onCallback(0, RESULT_OK, null);
            }
            finish();
            return;
        }

        // 没有权限，如果本次是需要去申请的，就去打开界面
        if (sRequested.compareAndSet(false, true)) {
            try {
                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            } catch (Exception e) {
                Logger.e(TAG, "start ACTION_USAGE_ACCESS_SETTINGS permission request activity error: " + e);
                notifyFailed();
            }
            return;
        }

        // 如果再次回来还没有权限，认为是权限申请失败
        notifyFailed();
    }

    private void notifyFailed() {
        AdhocRxJavaUtil.doUnsubscribe(mTimerSub);
        if (sCallback != null) {
            sCallback.onCallback(0, RESULT_CANCELED, null);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sRequested.set(false);
        sCallback = null;
        AdhocRxJavaUtil.doUnsubscribe(mTimerSub);
    }
}
