package com.nd.adhoc.assistant.sdk;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nd.adhoc.assistant.sdk.config.AssistantSpConfig;
import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.frame.api.user.IAdhocLoginInfo;
import com.nd.android.adhoc.basic.frame.api.user.IAdhocLoginListener;
import com.nd.android.adhoc.basic.frame.api.user.IAdhocLogoutListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AssistantBasicServiceFactory {
    private static final AssistantBasicServiceFactory ourInstance = new AssistantBasicServiceFactory();

    private AssistantSpConfig mSpConfig = null;

    private List<IAdhocLoginListener> mLoginListeners = new CopyOnWriteArrayList<>();
    private List<IAdhocLogoutListener> mLogoutListeners = new CopyOnWriteArrayList<>();

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

    public void addLogoutListener(IAdhocLogoutListener pListener){
        mLogoutListeners.add(pListener);
    }

    public void removeLogoutListener(IAdhocLogoutListener pListener){
        mLogoutListeners.remove(pListener);
    }

    void onLogin(@NonNull IAdhocLoginInfo pLoginInfo){
        for (IAdhocLoginListener listener : mLoginListeners) {
            listener.onLogin(pLoginInfo);
        }
    }

    void onLogout(){
        for (IAdhocLogoutListener listener : mLogoutListeners) {
            listener.onLogout();
        }
    }
}
