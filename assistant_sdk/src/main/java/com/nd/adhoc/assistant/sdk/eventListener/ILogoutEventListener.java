package com.nd.adhoc.assistant.sdk.eventListener;

import androidx.annotation.MainThread;

public interface ILogoutEventListener {
    @MainThread
    void onLogout();
}
