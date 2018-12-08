package com.nd.android.mdm.monitor;


import android.content.pm.PackageInfo;
import android.graphics.Bitmap;


import com.nd.android.mdm.monitor.info.AdhocBatteryInfo;
import com.nd.android.mdm.monitor.info.AdhocCpuInfo;
import com.nd.android.mdm.monitor.info.AdhocMemoryInfo;
import com.nd.android.mdm.monitor.info.AdhocNetworkInfo;
import com.nd.android.mdm.monitor.info.AdhocSDCardInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/4/20.
 */

public interface IMonitor {

    String BATTERY = "battery";
    String CPU_RATE = "cpu_rate";
    String SD_TOTAL = "sd_total";
    String SD_FREE = "sd_free";
    String SD_USED = "sd_used";
    String MEMORY_USED = "memory_used";
    String MEMORY_TOTAL = "memory_total";
    String MEMORY_FREE = "memory_free";
    String SYSTEM_SPACE_TOTAL = "system_space_total";
    String SYSTEM_SPACE_FREE = "system_space_free";
    String RSSI = "rssi";
    String SSID = "ssid";
    String LINK_SPEED = "link_speed";
    String CHARGE = "charge";
    String UPLOAD = "upload";
    String DOWNLOAD = "download";
    String USED_SYSTEM_SPACE_RATE = "used_system_space_rate";
    String USED_MEMORY_RATE = "used_memory_rate";

    AdhocCpuInfo getCpuInfo();

    AdhocBatteryInfo getBatteryInfo();

    AdhocNetworkInfo getNetworkInfo();

    AdhocMemoryInfo getMemoryInfo();

    AdhocSDCardInfo getSDCardInfo();

    List<PackageInfo> getApplications(int type);

    Bitmap screenShot();

    Object getValue(String index);
}
