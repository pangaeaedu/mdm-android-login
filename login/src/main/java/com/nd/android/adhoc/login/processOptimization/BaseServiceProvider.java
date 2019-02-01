package com.nd.android.adhoc.login.processOptimization;

import com.nd.adhoc.assistant.sdk.AssistantBasicServiceFactory;
import com.nd.adhoc.assistant.sdk.config.AssistantSpConfig;
import com.nd.android.adhoc.login.basicService.BasicServiceFactory;
import com.nd.android.adhoc.login.basicService.http.IHttpService;

public abstract class BaseServiceProvider {

    protected IHttpService getHttpService() {
        return BasicServiceFactory.getInstance().getHttpService();
    }

    protected AssistantSpConfig getConfig() {
        return AssistantBasicServiceFactory.getInstance().getSpConfig();
    }
}
