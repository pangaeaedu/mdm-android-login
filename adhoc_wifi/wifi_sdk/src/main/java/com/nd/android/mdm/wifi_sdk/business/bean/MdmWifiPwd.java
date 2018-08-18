package com.nd.android.mdm.wifi_sdk.business.bean;

import com.nd.android.mdm.wifi_sdk.sdk.dataService.db.entity.MdmWifiEntityHelper;
import com.nd.android.mdm.wifi_sdk.sdk.dataService.db.entity.intfc.IMdmWifiPwdEntity;

import java.io.Serializable;

/**
 * wifi 密码
 * <p>
 * Created by HuangYK on 2018/3/14.
 */

public class MdmWifiPwd implements Serializable {

    private IMdmWifiPwdEntity mWifiPwdEntity;

    public MdmWifiPwd() {
        mWifiPwdEntity = MdmWifiEntityHelper.newWifiPwdEntity();
    }

    public MdmWifiPwd(IMdmWifiPwdEntity wifiPwdEntity) {
        mWifiPwdEntity = wifiPwdEntity;
    }


    public String getSsid() {
        return mWifiPwdEntity.getSsid();
    }

    public void setSsid(String ssid) {
        mWifiPwdEntity.setSsid(ssid);
    }

    public String getPwd() {
        return mWifiPwdEntity.getPwd();
    }

    public void setPwd(String pwd) {
        mWifiPwdEntity.setPwd(pwd);
    }

}
