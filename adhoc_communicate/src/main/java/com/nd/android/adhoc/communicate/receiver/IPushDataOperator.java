package com.nd.android.adhoc.communicate.receiver;

import androidx.annotation.NonNull;

/**
 * Created by HuangYK on 2019/6/17.
 */

public interface IPushDataOperator {

    boolean isPushMsgTypeMatche(int pPushMsgType);


    void onPushDataArrived(@NonNull String pData);
}
