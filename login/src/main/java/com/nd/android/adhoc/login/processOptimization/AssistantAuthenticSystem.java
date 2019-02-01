package com.nd.android.adhoc.login.processOptimization;

public class AssistantAuthenticSystem {
    private static final AssistantAuthenticSystem ourInstance = new AssistantAuthenticSystem();

    public static AssistantAuthenticSystem getInstance() {
        return ourInstance;
    }

    private IDeviceInitiator mDeviceInitiator = null;
    private IUserAuthenticator mUserAuthenticator = null;
    private IDeviceStatusListener mPostLoginProcessor = null;
    private AssistantAuthenticSystem() {
    }

    public IDeviceInitiator getDeviceInitiator(){
        if(mDeviceInitiator == null){
            synchronized (this){
                if(mDeviceInitiator == null){
                    mDeviceInitiator = new DeviceInitiator(getPostLoginProcessor());
                }
            }
        }

        return mDeviceInitiator;
    }

    public IUserAuthenticator getUserAuthenticator(){
        if(mUserAuthenticator == null){
            synchronized (this){
                if(mUserAuthenticator == null){
                    mUserAuthenticator = new UserAuthenticator(getPostLoginProcessor());
                }
            }
        }

        return mUserAuthenticator;
    }

    private IDeviceStatusListener getPostLoginProcessor(){
        if(mPostLoginProcessor == null){
            synchronized (this){
                if(mPostLoginProcessor == null){
                    mPostLoginProcessor = new DeviceStatusListenerImpl();
                }
            }
        }

        return mPostLoginProcessor;
    }
}
