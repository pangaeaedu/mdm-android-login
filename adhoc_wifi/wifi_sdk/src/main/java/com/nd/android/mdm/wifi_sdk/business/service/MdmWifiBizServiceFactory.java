package com.nd.android.mdm.wifi_sdk.business.service;

import com.nd.android.mdm.wifi_sdk.business.service.intfc.IMdmWifiChannelBizService;
import com.nd.android.mdm.wifi_sdk.business.service.intfc.IMdmWifiOperateBizService;
import com.nd.android.mdm.wifi_sdk.business.service.intfc.IMdmWifiPwdBizService;
import com.nd.android.mdm.wifi_sdk.business.service.intfc.IMdmWifiVendorBizService;

/**
 * wifi 模块 业务层工厂类
 * <p>
 * Created by HuangYK on 2018/3/13.
 */
public final class MdmWifiBizServiceFactory {

    private volatile static MdmWifiBizServiceFactory sInstance = null;

    private IMdmWifiChannelBizService mMdmWifiChannelBizService;

    private IMdmWifiVendorBizService mMdmWifiVendorBizService;

    private IMdmWifiPwdBizService mMdmWifiPwdBizService;

    private IMdmWifiOperateBizService mMdmWifiOperateBizService;

    public static MdmWifiBizServiceFactory getInstance() {
        if (sInstance == null) {
            synchronized (MdmWifiBizServiceFactory.class) {
                if (sInstance == null) {
                    sInstance = new MdmWifiBizServiceFactory();
                }
            }
        }
        return sInstance;
    }

    public IMdmWifiChannelBizService getMdmWifiChannelBizService() {
        if (mMdmWifiChannelBizService == null) {
            mMdmWifiChannelBizService = new MdmWifiChannelBizServiceImpl();
        }
        return mMdmWifiChannelBizService;
    }


    public IMdmWifiVendorBizService getMdmWifiVendorBizService() {
        if (mMdmWifiVendorBizService == null) {
            mMdmWifiVendorBizService = new MdmWifiVendorBizServiceImpl();
        }
        return mMdmWifiVendorBizService;
    }

    public IMdmWifiPwdBizService getMdmWifiPwdBizService() {
        if (mMdmWifiPwdBizService == null) {
            mMdmWifiPwdBizService = new MdmWifiPwdBizServiceImpl();
        }
        return mMdmWifiPwdBizService;
    }

    public IMdmWifiOperateBizService getMdmWifiOperateBizService() {
        if (mMdmWifiOperateBizService == null) {
            mMdmWifiOperateBizService = new MdmWifiOperateBizServiceImpl();
        }
        return mMdmWifiOperateBizService;
    }
}
