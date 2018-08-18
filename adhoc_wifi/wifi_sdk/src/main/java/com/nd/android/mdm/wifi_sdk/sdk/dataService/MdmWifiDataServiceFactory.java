package com.nd.android.mdm.wifi_sdk.sdk.dataService;

import com.nd.android.mdm.wifi_sdk.sdk.dataService.db.service.MdmWifiDbServiceHelper;
import com.nd.android.mdm.wifi_sdk.sdk.dataService.db.service.intfc.IMdmWifiPwdDbService;
import com.nd.android.mdm.wifi_sdk.sdk.dataService.db.service.intfc.IMdmWifiVendorDbService;

/**
 * wifi 数据服务 工厂类
 * <p>
 * Created by HuangYK on 2018/3/13.
 */
public final class MdmWifiDataServiceFactory {

    private volatile static MdmWifiDataServiceFactory sInstance = null;

    private IMdmWifiPwdDbService mMdmWifiPwdDbService;
    private IMdmWifiVendorDbService mMdmWifiVendorDbService;

    private final byte[] mPwdLock = new byte[0];
    private final byte[] mVendorLock = new byte[0];


    public static MdmWifiDataServiceFactory getInstance() {
        if (sInstance == null) {
            synchronized (MdmWifiDataServiceFactory.class) {
                if (sInstance == null) {
                    sInstance = new MdmWifiDataServiceFactory();
                }
            }
        }
        return sInstance;
    }

    /**
     * 获取 wifi 密码 数据库服务对象
     *
     * @return IMdmWifiPwdDbService
     */
    public IMdmWifiPwdDbService getMdmWifiPwdDbService() {
        if (mMdmWifiPwdDbService == null) {
            synchronized (mPwdLock) {
                if (mMdmWifiPwdDbService == null) {
                    mMdmWifiPwdDbService =
                            MdmWifiDbServiceHelper.newMdmWifiPwdDbService();
                }
            }
        }
        return mMdmWifiPwdDbService;
    }

    /**
     * 获取 wifi 厂商信息 数据库服务对象对象
     *
     * @return IMdmWifiPwdDbService
     */
    public IMdmWifiVendorDbService getMdmWifiVendorDbService() {
        if (mMdmWifiVendorDbService == null) {
            synchronized (mVendorLock) {
                if (mMdmWifiVendorDbService == null) {
                    mMdmWifiVendorDbService =
                            MdmWifiDbServiceHelper.newMdmWifiVendorDbService();
                }
            }
        }
        return mMdmWifiVendorDbService;
    }


}
