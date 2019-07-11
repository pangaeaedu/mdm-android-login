package com.nd.android.mdm.biz.env;

/**
 * Created by HuangYK on 2018/6/21.
 */
public class MdmEnvModuleDefault extends MdmEnvModule {

    public MdmEnvModuleDefault() {
        mName = "Test";
        mOrg = "mdm_pre";
        mPushIp = "test.ndmdm.site";
        mPushPort = 59000;
        mUrl = "http://drms.debug.web.nd";
        mApmServer = "http://qc-report-collection.debug.web.nd/v1";
        mExceptionReportHostName = "DEBUG";
    }
}
