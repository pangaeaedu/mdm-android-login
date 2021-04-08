package com.nd.android.adhoc.login.processOptimization;

public class AssistantAuthenticSystem {
    private static final AssistantAuthenticSystem ourInstance = new AssistantAuthenticSystem();

    public static AssistantAuthenticSystem getInstance() {
        return ourInstance;
    }

////    private IDeviceInitiator mDeviceInitiator = null;
//    private IUserAuthenticator mUserAuthenticator = null;
//    private IDeviceStatusListener mDeviceStatusListener = null;

    private AssistantAuthenticSystem() {
    }

//    public IDeviceInitiator getDeviceInitiator(){
//        if(mDeviceInitiator == null){
//            synchronized (this){
//                if(mDeviceInitiator == null){
//                    mDeviceInitiator = new DeviceInitiator(getDeviceStatusListener());
//                }
//            }
//        }
//
//        return mDeviceInitiator;
//    }

//    public IUserAuthenticator getUserAuthenticator(){
//        if(mUserAuthenticator == null){
//            synchronized (this){
//                if(mUserAuthenticator == null){
//                    mUserAuthenticator = new UserAuthenticator(getDeviceStatusListener());
//                }
//            }
//        }
//
//        return mUserAuthenticator;
//    }

//    private IDeviceStatusListener getDeviceStatusListener(){
////        if(mDeviceStatusListener == null){
////            synchronized (this){
////                if(mDeviceStatusListener == null){
////                    mDeviceStatusListener = new DeviceStatusListenerImpl();
////                }
////            }
////        }
//
//        return mDeviceStatusListener;
//    }
}
