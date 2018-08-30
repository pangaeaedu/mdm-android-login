package com.nd.adhoc.assistant.sdk;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.frame.api.user.IAdhocLoginInfo;
import com.nd.android.adhoc.basic.frame.api.user.IAdhocLoginListener;
import com.nd.sdp.android.serviceloader.annotation.Service;

@Service(IAdhocLoginListener.class)
public class SDKLoginListenerImpl implements IAdhocLoginListener {
    @Override
    public void onLogin(@NonNull IAdhocLoginInfo pLoginInfo) {
        AssistantBasicServiceFactory.getInstance().onLogin(pLoginInfo);
    }
}
