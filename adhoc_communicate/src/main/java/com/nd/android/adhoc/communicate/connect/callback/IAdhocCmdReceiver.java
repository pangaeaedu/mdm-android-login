package com.nd.android.adhoc.communicate.connect.callback;

import android.support.annotation.NonNull;

/**
 * Created by HuangYK on 2019/6/17.
 */

public interface IAdhocCmdReceiver {

    void onCmdReceived(@NonNull String pCmdContent);
}
