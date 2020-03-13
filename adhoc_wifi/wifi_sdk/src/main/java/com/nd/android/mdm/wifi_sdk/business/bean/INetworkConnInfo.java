package com.nd.android.mdm.wifi_sdk.business.bean;

public interface INetworkConnInfo {

    int getChannel();

    String getSsid();

    int getRssi();

    String getApMac();

    String getMac();

    int getSpeed();

    String getIp();

    String getDns();

    String getGateway();

    int getSignalLevel();

    MdmWifiVendor getVendor();

    MdmWifiPwd getWifiPwd();

    String getCapabilities();

}
