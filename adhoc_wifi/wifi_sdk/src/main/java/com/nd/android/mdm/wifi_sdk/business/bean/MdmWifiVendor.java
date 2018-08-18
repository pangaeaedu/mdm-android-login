package com.nd.android.mdm.wifi_sdk.business.bean;

import com.nd.android.mdm.wifi_sdk.sdk.dataService.db.entity.MdmWifiEntityHelper;
import com.nd.android.mdm.wifi_sdk.sdk.dataService.db.entity.intfc.IMdmWifiVendorEntity;

import java.io.Serializable;

/**
 * 厂商信息
 * <p>
 * Created by HuangYK on 2018/3/13.
 */
public class MdmWifiVendor implements Serializable {


    private IMdmWifiVendorEntity mWifiVendorEntity;

    /**
     * logo资源名称
     */
    private String mLogoName;


    public MdmWifiVendor() {
        mWifiVendorEntity = MdmWifiEntityHelper.newWifiVendorEntity();
    }

    public MdmWifiVendor(IMdmWifiVendorEntity wifiVendorEntity) {
        mWifiVendorEntity = wifiVendorEntity;
    }

    public String getMacPrefix() {
        return mWifiVendorEntity.getMacPrefix();
    }

    public void setMacPrefix(String macPrefix) {
        mWifiVendorEntity.setMacPrefix(macPrefix);
    }

    public String getVendorName() {
        return mWifiVendorEntity.getVendorName();
    }

    public void setVendorName(String vendorName) {
        mWifiVendorEntity.setVendorName(vendorName);
    }

    public String getLogoName() {
        return mLogoName;
    }

    public void setLogoName(String logoName) {
        mLogoName = logoName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MdmWifiVendor that = (MdmWifiVendor) o;

        return mWifiVendorEntity.equals(that.mWifiVendorEntity);
    }

    @Override
    public int hashCode() {
        return mWifiVendorEntity.hashCode();
    }
}
