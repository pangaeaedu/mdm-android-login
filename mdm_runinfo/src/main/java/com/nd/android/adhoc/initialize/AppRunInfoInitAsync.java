package com.nd.android.adhoc.initialize;

import com.nd.android.adhoc.RunningAppWatchManager;
import com.nd.android.adhoc.basic.frame.api.initialization.AdhocAppInitAsyncAbs;
import com.nd.sdp.android.serviceloader.annotation.Service;

/**
 * Created by linsj on 2018/12/8.
 */
@Service(AdhocAppInitAsyncAbs.class)
public class AppRunInfoInitAsync extends AdhocAppInitAsyncAbs {

    @Override
    public void doInitAsync() {
        RunningAppWatchManager.getInstance().init();
    }
}
