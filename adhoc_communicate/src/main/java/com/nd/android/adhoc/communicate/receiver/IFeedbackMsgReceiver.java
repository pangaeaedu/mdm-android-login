package com.nd.android.adhoc.communicate.receiver;

import android.support.annotation.NonNull;

public interface IFeedbackMsgReceiver {
    void onCmdReceived(@NonNull String pCmdMsg);
}
