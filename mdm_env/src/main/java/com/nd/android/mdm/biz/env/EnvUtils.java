package com.nd.android.mdm.biz.env;


import com.nd.smartcan.accountclient.UCEnv;
import com.nd.smartcan.accountclient.UCManager;
import com.nd.smartcan.content.base.CsBaseManager;

public class EnvUtils {

    public static void setUcEnv(int pIndex){
        switch (pIndex) {
            case 3:
                UCManager.getInstance().setEnv(UCEnv.AWS);
                break;
            case 4:
                setEgyptEnv();
                break;
            case 0:
            case 1:
            case 2:
            default:
                UCManager.getInstance().setEnv(UCEnv.PreProduct);
                break;
        }
    }


    private static void setEgyptEnv() {
        //UC初始化
//        UCManager.getInstance().setBaseUrl("https://101uccenter-mdm.beta.101.com/v0.93/");
//        UCManager.getInstance().setCSSessionUrl("https://cscommon-mdm.beta.101.com/v0.1/");
//        UCManager.getInstance().setCaptchaBaseUrl("https://uc-captcha-mdm.beta.101.com/v0.1/");
//        UCManager.getInstance().setCSBaseUrl("https://egcs-mdm.beta.101.com/v0.1/");
        //CS初始化
//        CsManager.setContentBaseUrl("http://egcs.mdm.egypt.sdp/v0.1/");
        CsBaseManager.setContentBaseUrl("https://egcs-mdm.beta.101.com/v0.1/");
//        CsManager.setContentDownBaseUrl("http://egcs.mdm.egypt.sdp/v0.1/");
        CsBaseManager.setDownloadBaseUrl("https://egcs-mdm.beta.101.com/v0.1/");
//        Monet.get(getContext()).setKeyGenerator(RedirectKeyGenerator.create().register("pic_fansway", "egcs.mdm.egypt.sdp".split(",")));
    }

}
