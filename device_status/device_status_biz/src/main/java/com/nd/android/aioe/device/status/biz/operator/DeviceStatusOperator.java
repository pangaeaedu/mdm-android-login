package com.nd.android.aioe.device.status.biz.operator;

import android.content.Context;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.util.system.AdhocDeviceUtil;
import com.nd.android.adhoc.control.define.IControl_IMEI;
import com.nd.android.aioe.device.info.util.DeviceHelper;
import com.nd.android.aioe.device.info.util.DeviceIDSPUtils;
import com.nd.android.aioe.device.status.biz.bean.GetDeviceStatusResult;
import com.nd.android.aioe.device.status.biz.util.DeviceStatusParamUtil;
import com.nd.android.aioe.device.status.dao.api.IDeviceIdDao;
import com.nd.android.aioe.device.status.dao.api.IDeviceStatusDao;
import com.nd.android.mdm.basic.ControlFactory;

import java.util.Map;

public class DeviceStatusOperator {

    private static final String TAG = "DeviceStatusOperator";

    public GetDeviceStatusResult getDeviceStatus() throws AdhocException {

        IDeviceIdDao deviceIdDao = getDeviceIdDao();
        if (deviceIdDao == null) {
            return null;
        }

        Context context = AdhocBasicConfig.getInstance().getAppContext();
        String buildSn = AdhocDeviceUtil.getBuildSN(context);
        String cpuSn = AdhocDeviceUtil.getCpuSN();
        String wifiMac = AdhocDeviceUtil.getWifiMac(context);

        String lanMac = AdhocDeviceUtil.getEthernetMac();

        String imei = AdhocDeviceUtil.getIMEI(context);
        String imei2 = null;

        IControl_IMEI control_imei = ControlFactory.getInstance().getControl(IControl_IMEI.class);
        if (control_imei != null) {
            imei = control_imei.getIMEI(0);
            imei2 = control_imei.getIMEI(1);
        }

        if (TextUtils.isEmpty(wifiMac) && TextUtils.isEmpty(lanMac)) {
            throw new AdhocException("get wifiMac and lanMac failed");
        }

        String blueToothMac = AdhocDeviceUtil.getBloothMac();
        String serialNo = DeviceHelper.getSerialNumberThroughControl();
        String androidID = AdhocDeviceUtil.getAndroidId(context);

        Map<String, Object> params = DeviceStatusParamUtil.genHardwareMap(buildSn,
                cpuSn, imei, wifiMac, lanMac, blueToothMac, serialNo, androidID, imei2);


        deviceIdDao.confirmDeviceID(params, getLocalDeviceId());


        return null;
    }

    /*
     * 1、先尝试从 第一版本、第二版本的 SP 缓存中获取 deviceId
     * 2、如果 步骤 1 获取出来的值不为空，优先用 第二版本的 deviceId，其次用 第一版本的 deviceId
     * 3、如果 步骤 1 获取出来的值为空，则尝试获取 SD 卡上缓存的 deviceId
     * 4、如果 步骤 3 获取出来的值不为空，则使用该 deviceId
     * 5、如果 步骤 3 获取出来的值为空，则用新规则计算一个新的 deviceId
     */
    private String getLocalDeviceId() {
        // 先获取旧版本的 ID
        String secondVersionID = DeviceIDSPUtils.loadDeviceIDFromSp_V2();
        String firstVersionID = DeviceIDSPUtils.loadDeviceIDFromSp_V1();

        // 优先判断第 二 版本的ID，不为空，直接返回
        if (!TextUtils.isEmpty(secondVersionID)) {
            Logger.i(TAG, "getLocalDeviceId, use second version");
            return secondVersionID;
        }

        // 其次判断第 一 版本的ID，，不为空，直接返回
        if (!TextUtils.isEmpty(firstVersionID)) {
            Logger.i(TAG, "getLocalDeviceId, use first version ");
            return firstVersionID;
        }

        // 之前版本不存在，用最新的
        Context context = AdhocBasicConfig.getInstance().getAppContext();
        // 尝试 从 SD 卡上获取
        String sdCardDeviceID = DeviceIDSPUtils.loadDeviceIDFromSdCard(context);

        if (!TextUtils.isEmpty(sdCardDeviceID)) {
            Logger.i(TAG, "getLocalDeviceId, use sdcard data");
            return sdCardDeviceID;
        }

        Logger.d("yhq", "generate device id" );
        return DeviceIDSPUtils.generateDeviceID();
    }


    private IDeviceIdDao getDeviceIdDao() {


        return null;
    }


    private IDeviceStatusDao getDeviceStatusDao() {

        return null;
    }

}
