package com.nd.adhoc.assistant.sdk.eventListener;

import android.support.annotation.MainThread;

public interface ILogoutEventListener {
    @MainThread
    void onLogout();
}
