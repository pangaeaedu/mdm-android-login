package com.nd.android.mdm.biz.env;


import androidx.annotation.NonNull;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.smartcan.content.CsManager;
import com.nd.smartcan.content.base.CsBaseManager;
import com.nd.uc.account.NdUc;
import com.nd.uc.account.OtherParamsBuilder;
import com.nd.uc.account.interfaces.ICurrentUser;

public class EnvUtils {
    private static final String TAG = "EnvUtils";

    public static void initSdpEnv(@NonNull IMdmEnvModule pEnvModule) {
        setCsEnv(pEnvModule);
        setUcEnv(pEnvModule);
    }

    public static void setCsEnv(@NonNull IMdmEnvModule pEnvModule) {
        Logger.d(TAG, "setCsEnv name:" + pEnvModule.getName()
                + " csBaseUrl:" + pEnvModule.getCsBaseUrl() + " csBaseDownUrl:" + pEnvModule.getCsBaseDownUrl());

        CsManager.setIsDirectBoot(AdhocBasicConfig.getInstance().isEncrypStorageModel());
        CsBaseManager.setIsDirectBoot(AdhocBasicConfig.getInstance().isEncrypStorageModel());

        CsManager.setContentBaseUrl(pEnvModule.getCsBaseUrl());
        CsBaseManager.setContentBaseUrl(pEnvModule.getCsBaseUrl());

        CsManager.setContentDownBaseUrl(pEnvModule.getCsBaseDownUrl());
        CsBaseManager.setDownloadBaseUrl(pEnvModule.getCsBaseDownUrl());
    }

    public static void setUcEnv(@NonNull IMdmEnvModule pEnvModule) {
        Logger.i(TAG, "setUcEnv");

        Logger.d(TAG, "setUcEnv appid:" + pEnvModule.getUcAppID()
                + " newversionUrl:" + pEnvModule.getUcNewVersionBaseUrl()
                + " protocolUrl:" + pEnvModule.getUcNewVersionBaseUrl()
                + " orgcode:" + pEnvModule.getUcOrgCode());
        NdUc.buildConfiguration().withAppId(pEnvModule.getUcAppID()). //设置appId
                withParamsMap(OtherParamsBuilder.create().withDirectBoot(AdhocBasicConfig.getInstance().isEncrypStorageModel()).build()).//设置自启动加密模式
                withAccountType(ICurrentUser.ACCOUNT_TYPE_ORG). //设置帐户类型
                withBaseUrl(pEnvModule.getUcNewVersionBaseUrl()). //设置BaseUrl
                withContext(AdhocBasicConfig.getInstance().getAppContext()).
                withAgreementBaseUrl(pEnvModule.getUcProtocolUpdateUrl()).
                configDegradeStrategy(10, 300).
                build().init();
    }

    public static void setUcEnv(int pIndex) {
        switch (pIndex) {
            case 3:
                setGlobalEnv();
                break;
            case 4:
                setEgyptEnv();
                break;
            case 5:
                setEgyptDrillEnv();
                break;
            case 6:
                setShanxiEnv();
                break;
            case 0:
            case 1:
                setDevAndTestEnv();
                break;
            case 2:
            default:
                setChinaEnv();
                break;
        }
    }

    private static void setShanxiEnv() {
        CsManager.setContentBaseUrl("https://sdpcs.sneduyun.com.cn/v0.1/");
        CsManager.setContentDownBaseUrl("https://sdpcs.sneduyun.com.cn/v0.1/");
    }

    private static void setChinaEnv() {
        CsManager.setContentBaseUrl("https://cs.101.com/v0.1/");
        CsManager.setContentDownBaseUrl("https://cs.101.com/v0.1/");
    }

    private static void setDevAndTestEnv() {
        CsManager.setContentBaseUrl("https://betacs.101.com/v0.1/");
        CsManager.setContentDownBaseUrl("https://betacs.101.com/v0.1/");
    }

    private static void setGlobalEnv() {
        CsManager.setContentBaseUrl("https://awscs.101.com/v0.1/");
        CsManager.setContentDownBaseUrl("https://awscs.101.com/v0.1/");
    }

    private static void setEgyptEnv() {
        //UC初始化
//        UCManager.getInstance().setBaseUrl("http://101uccenter-mdm.test.moe.gov.eg/v0.93/");
//        UCManager.getInstance().setCSSessionUrl("http://cscommon-mdm.test.moe.gov.eg/v0.1/");
//        UCManager.getInstance().setCaptchaBaseUrl("http://uc-captcha-mdm.test.moe.gov.eg/v0.1/");
//        UCManager.getInstance().setCSBaseUrl("http://egcs-mdm.test.moe.gov.eg/v0.1/");
        //CS初始化
        CsManager.setContentBaseUrl("http://egcs.mdm.egypt.sdp/v0.1/");
        CsBaseManager.setContentBaseUrl("http://egcs-mdm.test.moe.gov.eg/v0.1/");
        CsManager.setContentDownBaseUrl("http://egcs.mdm.egypt.sdp/v0.1/");
        CsBaseManager.setDownloadBaseUrl("http://egcs-mdm.test.moe.gov.eg/v0.1/");
//        Monet.get(getContext()).setKeyGenerator(RedirectKeyGenerator.create().register("pic_fansway", "egcs.mdm.egypt.sdp".split(",")));
    }

    private static void setEgyptDrillEnv() {
        //UC初始化
//        UCManager.getInstance().setBaseUrl("http://101uccenter-mdm.test.moe.gov.eg/v0.93/");
//        UCManager.getInstance().setCSSessionUrl("http://cscommon-mdm.test.moe.gov.eg/v0.1/");
//        UCManager.getInstance().setCaptchaBaseUrl("http://uc-captcha-mdm.test.moe.gov.eg/v0.1/");
//        UCManager.getInstance().setCSBaseUrl("http://egcs-mdm.test.moe.gov.eg/v0.1/");
        //CS初始化
        CsManager.setContentBaseUrl("https://egcs-mdm.beta.101.com/v0.1/");
        CsBaseManager.setContentBaseUrl("https://egcs-mdm.beta.101.com/v0.1/");
        CsManager.setContentDownBaseUrl("https://egcs-mdm.beta.101.com/v0.1/");
        CsBaseManager.setDownloadBaseUrl("https://egcs-mdm.beta.101.com/v0.1/");
//        Monet.get(getContext()).setKeyGenerator(RedirectKeyGenerator.create().register("pic_fansway", "egcs.mdm.egypt.sdp".split(",")));
    }

}
