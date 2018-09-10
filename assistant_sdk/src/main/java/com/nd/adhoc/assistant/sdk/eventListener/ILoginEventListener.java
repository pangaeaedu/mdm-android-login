package com.nd.adhoc.assistant.sdk.eventListener;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.frame.api.user.IAdhocLoginInfo;

public interface ILoginEventListener {
    @MainThread
    void onLogin(@NonNull IAdhocLoginInfo pLoginInfo);
}
