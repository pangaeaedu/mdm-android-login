package com.nd.adhoc.assistant.sdk.deviceInfo;

public class UserLoginConfig {

    private int mAutoLogin = 0;
    private int mNeedGroup = 0;
    private String mGroupCode = "";
    private int mActivateRealType = 0;


    public UserLoginConfig(int pAutoLogin, int pNeedGroup, int pActivateRealType){
        mAutoLogin = pAutoLogin;
        mNeedGroup = pNeedGroup;
        mActivateRealType = pActivateRealType;
    }

    public UserLoginConfig(int pAutoLogin, int pNeedGroup, int pActivateRealType, String pGroupCode){
        mAutoLogin = pAutoLogin;
        mNeedGroup = pNeedGroup;
        mActivateRealType = pActivateRealType;
        mGroupCode = pGroupCode;
    }

    public int getAutoLogin(){
        return mAutoLogin;
    }

    public boolean isAutoLogin(){
        return getAutoLogin() == 1;
    }

    public int getActivateRealType(){
        return mActivateRealType;
    }

    public int getNeedGroup(){
        return mNeedGroup;
    }

    public String getGroupCode(){
        return mGroupCode;
    }
}
