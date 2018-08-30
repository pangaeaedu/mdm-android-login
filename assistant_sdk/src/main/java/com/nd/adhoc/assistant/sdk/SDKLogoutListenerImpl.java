package com.nd.adhoc.assistant.sdk;

import com.nd.android.adhoc.basic.frame.api.user.IAdhocLogoutListener;
import com.nd.sdp.android.serviceloader.annotation.Service;

@Service(IAdhocLogoutListener.class)
public class SDKLogoutListenerImpl implements IAdhocLogoutListener {
    @Override
    public void onLogout() {
        AssistantBasicServiceFactory.getInstance().onLogout();
    }
}
