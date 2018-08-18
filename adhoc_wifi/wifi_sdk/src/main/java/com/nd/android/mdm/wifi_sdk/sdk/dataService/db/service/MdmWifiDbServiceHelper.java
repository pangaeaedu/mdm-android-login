package com.nd.android.mdm.wifi_sdk.sdk.dataService.db.service;

import com.nd.android.mdm.wifi_sdk.sdk.constant.MdmWifiDbConstant;
import com.nd.android.mdm.wifi_sdk.sdk.dataService.db.service.intfc.IMdmWifiPwdDbService;
import com.nd.android.mdm.wifi_sdk.sdk.dataService.db.service.intfc.IMdmWifiVendorDbService;

/**
 * 数据库服务辅助类
 * <p>
 * Created by HuangYK on 2018/3/15.
 */
public class MdmWifiDbServiceHelper {

    public static IMdmWifiPwdDbService newMdmWifiPwdDbService() {
        return new MdmWifiPwdDbServiceImpl(MdmWifiDbConstant.MDM_WIFI_DB_NAME);
    }


    public static IMdmWifiVendorDbService newMdmWifiVendorDbService() {
        return new MdmWifiVendorDbServiceImpl(MdmWifiDbConstant.MDM_WIFI_VENDOR_DB_NAME);
    }

}
