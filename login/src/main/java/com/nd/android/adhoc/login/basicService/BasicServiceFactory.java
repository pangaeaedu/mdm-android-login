package com.nd.android.adhoc.login.basicService;

import com.nd.android.adhoc.login.basicService.config.LoginSpConfig;

public class BasicServiceFactory {
    private static final BasicServiceFactory ourInstance = new BasicServiceFactory();

    private IHttpService mHttpService = null;
    private LoginSpConfig mSpConfig = null;

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

    public LoginSpConfig getConfig(){
        if(mSpConfig == null){
            synchronized (this){
                if(mSpConfig == null){
                    mSpConfig = new LoginSpConfig("");
                }
            }
        }

        return mSpConfig;
    }

    public void clear(){
        if(mHttpService != null){
            mHttpService.clear();
            mHttpService = null;
        }
    }
}
