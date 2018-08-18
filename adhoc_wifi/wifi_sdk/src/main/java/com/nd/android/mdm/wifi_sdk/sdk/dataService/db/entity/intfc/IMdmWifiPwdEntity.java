package com.nd.android.mdm.wifi_sdk.sdk.dataService.db.entity.intfc;

import java.io.Serializable;

/**
 * Created by HuangYK on 2018/3/14.
 */

public interface IMdmWifiPwdEntity extends Serializable {

    String FIELD_SSID = "ssid";

    String FIELD_PWD = "pwd";


    String getSsid();

    void setSsid(String ssid);

    String getPwd();

    void setPwd(String pwd);

}
