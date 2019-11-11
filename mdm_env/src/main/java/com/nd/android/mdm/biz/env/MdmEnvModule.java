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

    @SerializedName("apm_server")
    String mApmServer;

    @SerializedName("exception_report_host_name")
    String mExceptionReportHostName;

    @SerializedName("cs_base_url")
    String mCsBaseUrl;

    @SerializedName("cs_base_down_url")
    String mCSBaseDownUrl;

    @SerializedName("uc_new_version_base_url")
    String mUcNewVersionBaseUrl;

    @SerializedName("uc_protocol_update_url")
    String mUcProtocolUpdateUrl;

    @SerializedName("uc_app_id")
    String mUcAppID;

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

    @Override
    public String getApmServer() {
        return mApmServer;
    }

    @Override
    public String getExceptionReportHostName() {
        return mExceptionReportHostName;
    }

    @Override
    public String getCsBaseUrl() {
        return mCsBaseUrl;
    }

    @Override
    public String getCsBaseDownUrl() {
        return mCSBaseDownUrl;
    }

    @Override
    public String getUcNewVersionBaseUrl() {
        return mUcNewVersionBaseUrl;
    }

    @Override
    public String getUcProtocolUpdateUrl() {
        return mUcProtocolUpdateUrl;
    }

    @Override
    public String getUcAppID() {
        return null;
    }

}
