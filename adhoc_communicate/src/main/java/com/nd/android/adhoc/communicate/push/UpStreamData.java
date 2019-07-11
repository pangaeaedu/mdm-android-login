package com.nd.android.adhoc.communicate.push;

public class UpStreamData {
    private long mSendTime = 0;
    private String mMsgID = "";
    private long mTTLSeconds = 0;
    private String mContentType = "";
    private String mContent = "";

    public UpStreamData(long pSendTime, String pMsgID, long pTTLSeconds, String pContentType,
                        String pContent) {
        mSendTime = pSendTime;
        mMsgID = pMsgID;
        mTTLSeconds = pTTLSeconds;
        mContentType = pContentType;
        mContent = pContent;
    }

    public long getSendTime() {
        return mSendTime;
    }

    public void setSendTime(long pSendTime) {
        mSendTime = pSendTime;
    }

    public String getMsgID() {
        return mMsgID;
    }

    public void setMsgID(String pMsgID) {
        mMsgID = pMsgID;
    }

    public long getTTLSeconds() {
        return mTTLSeconds;
    }

    public void setTTLSeconds(long pTTLSeconds) {
        mTTLSeconds = pTTLSeconds;
    }

    public String getContentType() {
        return mContentType;
    }

    public void setContentType(String pContentType) {
        mContentType = pContentType;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String pContent) {
        mContent = pContent;
    }
}
