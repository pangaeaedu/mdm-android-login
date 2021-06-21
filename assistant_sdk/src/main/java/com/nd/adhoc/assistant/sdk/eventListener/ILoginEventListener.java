package com.nd.adhoc.assistant.sdk.eventListener;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.nd.android.adhoc.basic.frame.api.user.IAdhocLoginInfo;

public interface ILoginEventListener {
    @MainThread
    void onLogin(@NonNull IAdhocLoginInfo pLoginInfo);
}
