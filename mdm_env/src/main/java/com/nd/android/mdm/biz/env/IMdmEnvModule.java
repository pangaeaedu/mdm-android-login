package com.nd.android.mdm.biz.env;

/**
 * Created by HuangYK on 2018/3/1.
 */

public interface IMdmEnvModule {

    String getName();

    String getOrg();

    String getUrl();

    String getPushIp();

    String getPushAppId();

    String getPushAppKey();

    String getPushLbs();

    int getPushPort();

    String getDownloadServiceName();

    String getApmServer();

    String getExceptionReportHostName();

    String getCsBaseUrl();

    String getCsBaseDownUrl();

    String getUcNewVersionBaseUrl();

    String getUcProtocolUpdateUrl();

    String getUcAppID();

    String getUcOrgCode();

    String getP2PBaseUrl();

    //0为默认 1为P2P
    int getDownloadType();
}
