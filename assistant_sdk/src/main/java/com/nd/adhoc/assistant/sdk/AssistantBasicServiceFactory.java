package com.nd.adhoc.assistant.sdk;

import android.content.Context;

import com.nd.adhoc.assistant.sdk.config.AssistantSpConfig;
import com.nd.android.adhoc.basic.common.AdhocBasicConfig;

public class AssistantBasicServiceFactory {
    private static final AssistantBasicServiceFactory ourInstance = new AssistantBasicServiceFactory();

    private AssistantSpConfig mSpConfig = null;

    public static AssistantBasicServiceFactory getInstance() {
        return ourInstance;
    }

    private AssistantBasicServiceFactory() {
    }

    public AssistantSpConfig getSpConfig(){
        Context ctx = AdhocBasicConfig.getInstance().getAppContext();
        if(mSpConfig == null){
            synchronized (this){
                if(mSpConfig == null){
                    mSpConfig = new AssistantSpConfig(ctx, "assistant_data");
                }
            }
        }

        return mSpConfig;
    }
}
