package com.nd.android.adhoc.loginapi;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Deprecated
public class UserInfoManager {
    private static final UserInfoManager ourInstance = new UserInfoManager();

    public static UserInfoManager getInstance() {
        return ourInstance;
    }

    private List<ICdmUserIDListener> mCdmUserIDListeners = new CopyOnWriteArrayList<>();

    private UserInfoManager() {
    }

    public void onCmdUserIDUpdate(String pUserID){
        for (ICdmUserIDListener listener : mCdmUserIDListeners) {
            listener.onCdmUserIDUpdate(pUserID);
        }
    }

    public void addUserIDListener(ICdmUserIDListener pListener){
        if(pListener == null || mCdmUserIDListeners.contains(pListener)){
            return;
        }

        mCdmUserIDListeners.add(pListener);
    }

    public void removeUserIDListener(ICdmUserIDListener pListener){
        if(pListener == null || !mCdmUserIDListeners.contains(pListener)){
            return;
        }

        mCdmUserIDListeners.remove(pListener);
    }

}
