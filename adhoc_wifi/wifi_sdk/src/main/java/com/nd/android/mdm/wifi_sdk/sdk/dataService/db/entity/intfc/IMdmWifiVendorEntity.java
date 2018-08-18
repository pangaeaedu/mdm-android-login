package com.nd.android.mdm.wifi_sdk.sdk.dataService.db.entity.intfc;

import java.io.Serializable;

/**
 * Created by HuangYK on 2018/3/15.
 */
public interface IMdmWifiVendorEntity extends Serializable {

    String FIELD_MAC_PREFIX = "mac_prefix";

    String FIELD_VENDOR_NAME = "vendor_name";


    String getMacPrefix();

    void setMacPrefix(String macPrefix);

    String getVendorName();

    void setVendorName(String vendorName);
}
