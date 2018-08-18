package com.nd.android.mdm.wifi_sdk.sdk.dataService.db.entity;

import com.nd.android.mdm.wifi_sdk.sdk.dataService.db.entity.intfc.IMdmWifiPwdEntity;
import com.nd.android.mdm.wifi_sdk.sdk.dataService.db.entity.intfc.IMdmWifiVendorEntity;

/**
 * 实体类辅助类
 * <p>
 * Created by HuangYK on 2018/3/14.
 */
public class MdmWifiEntityHelper {

    public static Class getWifiPwdEntityClass() {
        return MdmWifiPwdEntity.class;
    }

    public static Class getWifiVendorEntityClass() {
        return MdmWifiVendorEntity.class;
    }

    public static IMdmWifiPwdEntity newWifiPwdEntity() {
        return new MdmWifiPwdEntity();
    }

    public static IMdmWifiVendorEntity newWifiVendorEntity() {
        return new MdmWifiVendorEntity();
    }
}
