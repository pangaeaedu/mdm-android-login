package com.nd.android.mdm.wifi_sdk.business.bean;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.RouteInfo;
import android.os.Build;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.util.net.AdhocNetworkIpUtil;
import com.nd.android.adhoc.basic.util.system.AdhocDeviceUtil;
import com.nd.android.adhoc.firewall.Util;

import java.net.InetAddress;
import java.util.List;


public class EthernetConnInfo implements INetworkConnInfo {
    @Override
    public int getChannel() {
        return 0;
    }

    @Override
    public String getSsid() {
        return null;
    }

    @Override
    public int getRssi() {
        return 0;
    }

    @Override
    public String getApMac() {
        return null;
    }

    @Override
    public String getMac() {
        return AdhocDeviceUtil.getEthernetMac();
    }

    @Override
    public int getSpeed() {
        return 0;
    }

    @Override
    public String getIp() {
        return AdhocNetworkIpUtil.getCurrentIp(getContext());
    }

    @Override
    public String getDns() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return "8.8.8.8";
        }

        List<String> dnsList = Util.getDefaultDNS(getContext());
        if (dnsList.isEmpty()) {
            return "";
        }

        return dnsList.get(0);
    }

    @Override
    public String getGateway() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context
                    .CONNECTIVITY_SERVICE);
            if (cm == null) {
                return "";
            }
            Network an = cm.getActiveNetwork();
            if (an != null) {
                LinkProperties lp = cm.getLinkProperties(an);
                if (lp != null) {
                    List<RouteInfo> routeInfos = lp.getRoutes();
                    if (routeInfos == null || routeInfos.isEmpty()) {
                        return "";
                    }

                    InetAddress gateway = routeInfos.get(routeInfos.size() - 1).getGateway();
                    return gateway.getHostAddress();
                }
            }
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return "";
        }

        return Util.jni_getprop("dhcp.eth0.gateway");
    }

    @Override
    public int getSignalLevel() {
        return 0;
    }

    @Override
    public MdmWifiVendor getVendor() {
        return null;
    }

    @Override
    public MdmWifiPwd getWifiPwd() {
        return null;
    }

    @Override
    public String getCapabilities() {
        return null;
    }

    private Context getContext() {
        return AdhocBasicConfig.getInstance().getAppContext();
    }
}
