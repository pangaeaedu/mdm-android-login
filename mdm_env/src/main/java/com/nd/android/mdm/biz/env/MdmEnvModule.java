package com.nd.android.mdm.biz.env;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HuangYK on 2018/2/28.
 */
class MdmEnvModule implements IMdmEnvModule{

    @SerializedName("name")
    String mName;

    @SerializedName("org")
    String mOrg;

    @SerializedName("url")
    String mUrl;

    @SerializedName("puship")
    String mPushIp;

    @SerializedName("pushport")
    int mPushPort;

    @SerializedName("downloadservicename")
    String mDownloadServiceName;

    public MdmEnvModule() {
    }

    public String getName() {
        return mName;
    }

    public String getOrg() {
        return mOrg;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getPushIp() {
        return mPushIp;
    }

    public int getPushPort() {
        return mPushPort;
    }

    @Override
    public String getDownloadServiceName() {
        return mDownloadServiceName;
    }


}
