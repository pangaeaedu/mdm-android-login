package com.nd.adhoc.assistant.sdk;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nd.adhoc.assistant.sdk.config.AssistantSpConfig;
import com.nd.adhoc.assistant.sdk.eventListener.ILoginEventListener;
import com.nd.adhoc.assistant.sdk.eventListener.ILogoutEventListener;
import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.frame.api.user.IAdhocLoginInfo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AssistantBasicServiceFactory {
    private static final AssistantBasicServiceFactory ourInstance = new AssistantBasicServiceFactory();

    private AssistantSpConfig mSpConfig = null;

    private List<ILoginEventListener> mLoginListeners = new CopyOnWriteArrayList<>();
    private List<ILogoutEventListener> mLogoutListeners = new CopyOnWriteArrayList<>();

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

    public void addLogoutListener(ILogoutEventListener pListener){
        mLogoutListeners.add(pListener);
    }

    public void removeLogoutListener(ILogoutEventListener pListener){
        mLogoutListeners.remove(pListener);
    }

    public void addLoginListener(ILoginEventListener pListener){
        mLoginListeners.add(pListener);
    }

    public void removeLoginListener(ILoginEventListener pListener){
        mLoginListeners.remove(pListener);
    }

    void onLogin(@NonNull IAdhocLoginInfo pLoginInfo){
        for (ILoginEventListener listener : mLoginListeners) {
            listener.onLogin(pLoginInfo);
        }
    }

    void onLogout(){
        for (ILogoutEventListener listener : mLogoutListeners) {
            listener.onLogout();
        }
    }
}
