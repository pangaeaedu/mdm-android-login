package com.nd.android.adhoc.communicate.constant;

public enum AdhocPushMsgType {

    Command(0),
    Feedback(1);

    private int mValue = 0;

    AdhocPushMsgType(int pValue){
        mValue = pValue;
    }

    public int getValue(){
        return mValue;
    }

}
