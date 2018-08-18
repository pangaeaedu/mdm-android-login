package com.nd.android.mdm.wifi_sdk.sdk.dataService.db.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.nd.android.mdm.wifi_sdk.sdk.dataService.db.entity.intfc.IMdmWifiPwdEntity;

/**
 * wifi 帐号密码 实体类
 * <p>
 * Created by HuangYK on 2018/3/13.
 */
@DatabaseTable(tableName = "mdm_wifi_psw")
class MdmWifiPwdEntity implements IMdmWifiPwdEntity {

    @DatabaseField(canBeNull = false, id = true, columnName = FIELD_SSID)
    private String mSsid;

    @DatabaseField(canBeNull = false, columnName = FIELD_PWD)
    private String mPwd;

    @Override
    public String getSsid() {
        return mSsid;
    }

    @Override
    public void setSsid(String ssid) {
        mSsid = ssid;
    }

    @Override
    public String getPwd() {
        return mPwd;
    }

    @Override
    public void setPwd(String pwd) {
        mPwd = pwd;
    }

    @Override
    public String toString() {
        return "MdmWifiPwdEntity{" +
                "mSsid='" + mSsid + '\'' +
                ", mPwd='" + mPwd + '\'' +
                '}';
    }
}
