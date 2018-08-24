package com.nd.android.adhoc.login.basicService;

import com.nd.android.adhoc.login.basicService.data.push.UserActivateResult;
import com.nd.android.adhoc.login.basicService.http.HttpServiceImpl;
import com.nd.android.adhoc.login.basicService.http.IHttpService;
import com.nd.android.adhoc.login.eventListener.IUserActivateListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BasicServiceFactory {
    private static final BasicServiceFactory ourInstance = new BasicServiceFactory();

    private IHttpService mHttpService = null;

    private List<IUserActivateListener> mActivateListeners = new CopyOnWriteArrayList<>();

    public static BasicServiceFactory getInstance() {
        return ourInstance;
    }

    private BasicServiceFactory() {
    }

    public IHttpService getHttpService(){
        if(mHttpService == null){
            synchronized (this){
                if(mHttpService == null){
                    mHttpService = new HttpServiceImpl();
                }
            }
        }

        return mHttpService;
    }

    public void addActivateListener(IUserActivateListener pListener){
        mActivateListeners.add(pListener);
    }

    public void removeActivateListener(IUserActivateListener pListener){
        mActivateListeners.remove(pListener);
    }

//    public LoginSpConfig getConfig(){
//        AdhocBasicConfig.getInstance().getAppContext();
//        if(mSpConfig == null){
//            synchronized (this){
//                if(mSpConfig == null){
//                    mSpConfig = new LoginSpConfig("");
//                }
//            }
//        }
//
//        return mSpConfig;
//    }

    public void notifyActivateResponse(UserActivateResult pResult){
        for (IUserActivateListener listener : mActivateListeners) {
            listener.onUserActivateResult(pResult);
        }
    }

    public void clear(){
        if(mHttpService != null){
            mHttpService = null;
        }

        mActivateListeners.clear();
    }
}
