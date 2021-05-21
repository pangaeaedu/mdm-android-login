package com.nd.adhoc.assistant.sdk;

import android.app.Activity;
import androidx.annotation.NonNull;

import com.nd.android.adhoc.basic.frame.factory.AdhocFrameFactory;
import com.nd.android.adhoc.router_api.facade.Postcard;
import com.nd.android.adhoc.router_api.facade.callback.NavCallback;

/**
 * Created by Administrator on 2018/8/30 0030.
 */

public class ActivityJumpUtils {

    public static void enterLoginUI(final Activity pSource) {
        AdhocFrameFactory.getInstance().getAdhocRouter().build("/login/login_activity")
                .navigation(pSource, new NavCallback() {

                    @Override
                    public void onInterrupt(@NonNull Postcard postcard) {
                        super.onInterrupt(postcard);
//                        Logger.w(TAG, "onInterrupt");
                    }

                    @Override
                    public void onLost(@NonNull Postcard postcard) {
                        super.onLost(postcard);
//                        Logger.e(TAG, "onLost");
                    }

                    @Override
                    public void onArrival(@NonNull Postcard postcard) {
                        if(pSource != null && !pSource.isFinishing()) {
                            pSource.finish();
                        }
                    }
                });
    }
}
