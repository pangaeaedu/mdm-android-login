package com.nd.android.adhoc.login;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nd.android.adhoc.loginapi.ICdmUserIDListener;
import com.nd.android.adhoc.loginapi.IUserInfoApi;
import com.nd.android.adhoc.router_api.facade.annotation.Route;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/*
请注意，这个类是给CDM用的，不是给MDM用的。
 */
@Route(path = LoginRoutePathConstants.PATH_USER_INFO)
public class UserInfoApiImpl implements IUserInfoApi {
    private List<ICdmUserIDListener> mCdmUserIDListeners = new CopyOnWriteArrayList<>();

    @Override
    public String getCmdUserID() {
//        return getConfig().getUserID();
        return "";
    }

    @Override
    public void onCmdUserUpdate(String pUserID) {
//        getConfig().saveUserID(pUserID);

        for (ICdmUserIDListener listener : mCdmUserIDListeners) {
            listener.onCdmUserIDUpdate(pUserID);
        }
    }

    @Override
    public void addCmdUserIDListener(ICdmUserIDListener pListener) {
        if(pListener == null || mCdmUserIDListeners.contains(pListener)){
            return;
        }

        mCdmUserIDListeners.add(pListener);
    }

    @Override
    public void removeCmdUserIDListener(ICdmUserIDListener pListener) {
        if(pListener == null || !mCdmUserIDListeners.contains(pListener)){
            return;
        }

        mCdmUserIDListeners.remove(pListener);
    }

    @Override
    public void init(@NonNull Context pContext) {

    }

//    private AssistantSpConfig getConfig(){
//        return AssistantBasicServiceFactory.getInstance().getSpConfig();
//    }
}
