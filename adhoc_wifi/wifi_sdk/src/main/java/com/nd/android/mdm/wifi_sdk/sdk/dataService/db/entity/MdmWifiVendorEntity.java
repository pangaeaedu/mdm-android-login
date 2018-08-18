package com.nd.android.mdm.wifi_sdk.sdk.dataService.db.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.nd.android.mdm.wifi_sdk.sdk.dataService.db.entity.intfc.IMdmWifiVendorEntity;

/**
 * 厂商信息 实体类
 * <p>
 * Created by HuangYK on 2018/3/13.
 */
@DatabaseTable(tableName = "mdm_wifi_vendor")
class MdmWifiVendorEntity implements IMdmWifiVendorEntity {


    @DatabaseField(canBeNull = false, id = true, columnName = FIELD_MAC_PREFIX)
    private String mMacPrefix;

    @DatabaseField(canBeNull = false, columnName = FIELD_VENDOR_NAME)
    private String mVendorName;


    @Override
    public String getMacPrefix() {
        return mMacPrefix;
    }

    @Override
    public void setMacPrefix(String macPrefix) {
        mMacPrefix = macPrefix;
    }

    @Override
    public String getVendorName() {
        return mVendorName;
    }

    @Override
    public void setVendorName(String vendorName) {
        mVendorName = vendorName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MdmWifiVendorEntity that = (MdmWifiVendorEntity) o;

        if (!mMacPrefix.equals(that.mMacPrefix)) return false;
        return mVendorName.equals(that.mVendorName);
    }

    @Override
    public int hashCode() {
        int result = mMacPrefix.hashCode();
        result = 31 * result + mVendorName.hashCode();
        return result;
    }
}
