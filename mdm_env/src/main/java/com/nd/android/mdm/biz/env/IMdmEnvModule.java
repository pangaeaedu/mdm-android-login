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
}
