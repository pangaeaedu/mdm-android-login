package com.nd.android.mdm.wifi_sdk.business.basic.constant;

import androidx.annotation.NonNull;
import android.text.TextUtils;

/**
 * 厂商信息定义类
 * <p>
 * Created by HuangYK on 2018/3/14.
 */
public enum MdmVendorInfo {


    APPLE("apple", "logo_apple"),
    ARUBA("aruba", "logo_aruba"),
    ASUS("asus", "logo_asus"),
    BLINK("blink", "logo_blink"),
    CISCO("cisco", "logo_cisco"),
    DLINK("dlink", "logo_dlink"),
    FASTCOM("fastcom", "logo_fastcom"),
    H3C("h3c", "logo_h3c"),
    HTC("htc", "logo_htc"),
    HUAWEI("huawei", "logo_huawei"),
    INTEL("intel", "logo_intel"),
    IPCOM("ipcom", "logo_ipcom"),
    LE("le", "logo_le"),
    LENOVO("lenovo", "logo_lenovo"),
    LG("lg", "logo_lg"),
    MEIZU("meizu", "logo_meizu"),
    MERCURY("mercury", "logo_mercury"),
    NETGEAR("netgear", "logo_netgear"),
    OPPO("oppo", "logo_oppo"),
    PHICOMM("phicomm", "logo_phicomm"),
    RUCKUS("ruckus", "logo_ruckus"),
    RUIJIE("ruijie", "logo_ruijie"),
    SAMSUNG("samsung", "logo_samsung"),
    SONY("sony", "logo_sony"),
    STARNET("starnet", "logo_startnet"),
    SUNDRAY("sundray", "logo_sundray"),
    TENDA("tenda", "logo_tenda"),
    TOTOLINK("totolink", "logo_totolink"),
    TPLINK("tp-link", "logo_tplink"),
    UBIQUITI("ubiquiti", "logo_ubiquiti"),
    UTT("utt", "logo_utt"),
    VIVO("vivo", "logo_vivo"),
    WAVLINK("wavlink", "logo_wavlink"),
    WAYOS("wayos", "logo_wayos"),
    XIAOMI("xiaomi", "logo_xiaomi"),
    ZTE("zte", "logo_zte"),
    ZUK("zuk", "logo_zuk"),

    UNKNOWN("unknow", "assist_icon_logo");


    private String mName;
    private String mLogoResName;


    MdmVendorInfo(String name, String logoResName) {
        mName = name;
        mLogoResName = logoResName;
    }

    /**
     * getTypeByString
     * 根据字符串获取枚举值
     *
     * @param name 名称
     * @return MdmVendorInfo
     */
    @NonNull
    public static MdmVendorInfo getIconByName(String name) {
        if (TextUtils.isEmpty(name)) {
            return UNKNOWN;
        }
        MdmVendorInfo[] array = MdmVendorInfo.values();
        for (MdmVendorInfo flag : array) {
            if (flag.mName.equals(name)) {
                return flag;
            }
        }
        return UNKNOWN;
    }

    public String getLogoResName() {
        return mLogoResName;
    }
}
