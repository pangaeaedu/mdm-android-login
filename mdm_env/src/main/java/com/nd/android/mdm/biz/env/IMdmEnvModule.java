package com.nd.android.mdm.biz.env;

/**
 * Created by HuangYK on 2018/3/1.
 */

public interface IMdmEnvModule {

    String getName();

    String getOrg();

    String getUrl();

    String getPushIp();

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
}
