package com.nd.android.mdm.wifi_sdk.business.bean;

import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.util.system.AdhocDeviceUtil;

import java.io.Serializable;

/**
 * wifi 信息
 * <p>
 * Created by HuangYK on 2018/3/13.
 */
public class MdmWifiInfo implements Serializable {

    private static final String NORMAL_MAC = "00:00:00:00:00";


    /**
     * wifi 信道
     */
    private int mChannel;

    /**
     * wifi 名称
     */
    private String mSsid;

    /**
     * wifi 信号值
     */
    private int mRssi;

    /**
     * Access Point(无线访问节点)的 mac 地址
     */
    private String mApMac;

    /**
     * mac 地址
     */
    private String mMac;

    /**
     * 速度
     */
    private int mSpeed;

    /**
     * IP
     */
    private String mIp;

    /**
     * dns
     */
    private String mDns;

    /**
     * 网关
     */
    private String mGateway;

    /**
     *
     */
    private String mCapabilities;

    private int mSignalLevel;

    /**
     * 厂商信息
     */
    private MdmWifiVendor mVendor;

    /**
     * 密码信息
     */
    private MdmWifiPwd mWifiPwd;


    public MdmWifiInfo() {
        reset();
    }


    public void reset() {
        mChannel = -1;
        mSsid = "";
        mRssi = -999;
        mApMac = "";
        mMac = NORMAL_MAC;
        mSpeed = -1;
        mIp = "";
        mDns = "";
        mGateway = "";
        mSignalLevel = 0;
        mVendor = null;
        mWifiPwd = null;
    }

    public boolean isVaild(){
        return mChannel != -1
                && !TextUtils.isEmpty(mSsid)
                && mRssi != -999
                && !TextUtils.isEmpty(mApMac)
                && !NORMAL_MAC.equals(mMac)
                && mSpeed != -1;
    }

    public int getChannel() {
        return mChannel;
    }

    public void setChannel(int channel) {
        mChannel = channel;
    }

    public String getSsid() {
        return mSsid;
    }

    public void setSsid(String ssid) {
        mSsid = ssid;
    }

    public int getRssi() {
        return mRssi;
    }

    public void setRssi(int rssi) {
        mRssi = rssi;
    }

    public String getApMac() {
        return mApMac;
    }

    public void setApMac(String apMac) {
        mApMac = apMac;
    }

    public String getMac() {
        return AdhocDeviceUtil.getWifiMac(AdhocBasicConfig.getInstance().getAppContext());
    }

    public void setMac(String mac) {
        mMac = TextUtils.isDigitsOnly(mac) ? NORMAL_MAC : mac;
    }

    public int getSpeed() {
        return mSpeed;
    }

    public void setSpeed(int speed) {
        mSpeed = speed;
    }

    public String getIp() {
        return mIp;
    }

    public void setIp(String ip) {
        mIp = ip;
    }

    public String getDns() {
        return mDns;
    }

    public void setDns(String dns) {
        mDns = dns;
    }

    public String getGateway() {
        return mGateway;
    }

    public void setGateway(String gateway) {
        mGateway = gateway;
    }

    public int getSignalLevel() {
        return mSignalLevel;
    }

    public void setSignalLevel(int signalLevel) {
        mSignalLevel = signalLevel;
    }

    public MdmWifiVendor getVendor() {
        return mVendor;
    }

    public void setVendor(MdmWifiVendor vendor) {
        mVendor = vendor;
    }

    public MdmWifiPwd getWifiPwd() {
        return mWifiPwd;
    }

    public void setWifiPwd(MdmWifiPwd wifiPwd) {
        mWifiPwd = wifiPwd;
    }

    public String getCapabilities() {
        return mCapabilities;
    }

    public void setCapabilities(String capabilities) {
        mCapabilities = capabilities;
    }
}
