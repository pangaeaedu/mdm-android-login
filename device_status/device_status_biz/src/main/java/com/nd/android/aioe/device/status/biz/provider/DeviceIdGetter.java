package com.nd.android.aioe.device.status.biz.provider;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.util.system.AdhocDeviceUtil;
import com.nd.android.aioe.device.info.config.DeviceInfoSpConfig;
import com.nd.android.aioe.device.info.util.DeviceIDSPUtils;
import com.nd.android.aioe.device.info.util.DeviceInfoManager;
import com.nd.android.aioe.device.status.biz.api.ActivateConfig;
import com.nd.android.aioe.device.status.biz.api.listener.DeviceStatusErrorManager;
import com.nd.android.aioe.device.status.biz.api.listener.IDeviceStatusErrorListener;
import com.nd.android.aioe.device.status.biz.model.ConfirmDeviceIdModel;
import com.nd.android.aioe.device.status.biz.util.DeviceStatusParamUtil;
import com.nd.android.aioe.device.status.dao.api.IDeviceIdDao;
import com.nd.android.aioe.device.status.dao.impl.DeviceStatusDaoHelper;
import com.nd.android.aioe.device.status.dao.impl.constant.DeviceType;
import com.nd.android.mdm.biz.env.MdmEvnFactory;

import java.util.Map;

class DeviceIdGetter {

    private static final String TAG = "DeviceStatus";

    @NonNull
    @WorkerThread
    public static String getDeviceId() {

        String deviceId = getV3DeviceId();

        if (!TextUtils.isEmpty(deviceId)) {
            Logger.i(TAG, "getV3DeviceId success");

            return deviceId;
        }

        Logger.i(TAG, "V3 deviceid is empty, try get v2 v1 ");
        deviceId = getV2V1DeviceId();

        if(TextUtils.isEmpty(deviceId)){
            deviceId = getSdcardDeviceId();
        }

        if (TextUtils.isEmpty(deviceId)) {
            deviceId = DeviceIDSPUtils.generateDeviceID();
        }

        assert deviceId != null;

        ConfirmDeviceIdModel confirmDeviceIdModel = doConfirmDeviceID(deviceId);
        deviceId = confirmDeviceIdModel.getDeviceID();

        submitHardwareInfoIfNecessary(deviceId);

        refreshDeviceData(deviceId);

        return deviceId;
    }

    /**
     * 第一步，先从 SP 中获取 V3 版本的 DeviceId，如果有，就可以直接用了
     */
    private static String getV3DeviceId() {
        String deviceID = DeviceInfoSpConfig.getDeviceID();
        if (!TextUtils.isEmpty(deviceID)) {
            return deviceID;
        }

        deviceID = DeviceIDSPUtils.loadDeviceIDFromSp_V3();
        Context context = AdhocBasicConfig.getInstance().getAppContext();

        Logger.d(TAG, "DeviceIdGetter getV3DeviceId, v3 sp device id:" + deviceID);
        if (!TextUtils.isEmpty(deviceID)) {
            // TODO：这里有两个疑问，
            //  1：为什么要去 setDeviceId，然后通知出去？？
            //  2、为什么要起新线程去 做 checkDeviceId 的事情？
            DeviceInfoManager.getInstance().setDeviceID(deviceID);

            //这里是去
            DeviceIDSPUtils.startNewThreadToCheckDeviceIDIntegrity(context, deviceID);
            return deviceID;
        }

        return deviceID;
    }

    private static void refreshDeviceData(String deviceId) {
        // TODO：为什么确认完  DeviceId 要清除所有的数据？
        DeviceInfoSpConfig.clearData();
//        DeviceStatusCache.setDeviceStatus(DeviceStatus.Init);

        DeviceIDSPUtils.saveDeviceIDToSp(deviceId);
        DeviceInfoManager.getInstance().setDeviceID(deviceId);
        DeviceIDSPUtils.startNewThreadToCheckDeviceIDIntegrity(AdhocBasicConfig.getInstance().getAppContext(), deviceId);
    }

    private static String getV2V1DeviceId() {
        // 先获取旧版本的 ID
        String secondVersionID = DeviceIDSPUtils.loadDeviceIDFromSp_V2();
        String firstVersionID = DeviceIDSPUtils.loadDeviceIDFromSp_V1();

        // 优先判断第 二 版本的ID，不为空，直接返回
        if (!TextUtils.isEmpty(secondVersionID)) {
            Logger.i(TAG, "DeviceIdGetter getLocalDeviceId, use second version");
            return secondVersionID;
        }

        // 其次判断第 一 版本的ID，，不为空，直接返回
        if (!TextUtils.isEmpty(firstVersionID)) {
            Logger.i(TAG, "DeviceIdGetter getLocalDeviceId, use first version ");
            return firstVersionID;
        }

        return null;
    }

    private static String getSdcardDeviceId() {
        // 之前版本不存在，用最新的
        Context context = AdhocBasicConfig.getInstance().getAppContext();
        // 尝试 从 SD 卡上获取
        return DeviceIDSPUtils.loadDeviceIDFromSdCard(context);
    }


    /**
     * 这里会重试知道成功为止
     */
    @NonNull
    @WorkerThread
    private static ConfirmDeviceIdModel doConfirmDeviceID(@NonNull String pDeviceID){

        Map<String, Object> params;
        while (true) {
            try {
                params = DeviceStatusParamUtil.genHardwareMap();
                // 取成功了，就不再循环了
                break;
            } catch (Exception e) {
                Logger.e(TAG, "DeviceIdGetter doConfirmDeviceID failed, genHardwareMap error: " + e);
            }
            // 当 wifi mac 和 lan mac 的关键信息都没有的话，会通过异常走到这里，那么休眠一会再尝试
            // 有必要的话，之后这里可以开一个口，让外部能够判断是否继续循环尝试


            try {
                Thread.sleep(20 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        ConfirmDeviceIdModel result = null;

        int count = 0;
        while (result == null || !result.isSuccess()) {
            count++;
            try {
                Logger.i(TAG, "DeviceIdGetter doConfirmDeviceID device id round:" + count);

                result = getDeviceIdDao().confirmDeviceID(ConfirmDeviceIdModel.class, params, pDeviceID, DeviceType.getValue());

                if (result == null || !result.isSuccess()) {
                    // 没有成功，发送通知出去
                    DeviceStatusErrorManager.notifyError(IDeviceStatusErrorListener.ERROR_CODE_CONFIRM_DEVICE_ID_ERROR);
                }

            } catch (Exception e) {
                Logger.e(TAG, "DeviceIdGetter doConfirmDeviceID error: " + e);

                if (!ActivateConfig.getInstance().isAutoLogin()) {
                    // TODO： 这里改成注入的判断
//                    AdhocExitAppManager.exitApp(120 * 1000);
                    throw e;
                }
            }
        }

        Logger.i(TAG, "DeviceIdGetter doConfirmDeviceID completed");
        Logger.d(TAG, "DeviceIdGetter doConfirmDeviceID, result deviceId is:" + result.getDeviceID());
        return result;
    }

    private static void submitHardwareInfoIfNecessary(@NonNull String pDeviceId) {
        try {
            boolean isWifiMacReported = DeviceInfoSpConfig.isWifiMacReported();
            boolean isLanMacReported = DeviceInfoSpConfig.isLanMacReported();
            if (isWifiMacReported && isLanMacReported) {
                Logger.i(TAG, "DeviceIdGetter lan mac and wifi mac submitted");
                return;
            }

            Context context = AdhocBasicConfig.getInstance().getAppContext();

            String wifiMac = "";
            String lanMac = "";

            if (!isWifiMacReported) {
                wifiMac = AdhocDeviceUtil.getWifiMac(context);
                if (TextUtils.isEmpty(wifiMac)) {
                    Logger.w(TAG, "DeviceIdGetter wifi mac not found");
                }
            }

            if (!isLanMacReported) {
                lanMac = AdhocDeviceUtil.getEthernetMac();
                if (TextUtils.isEmpty(lanMac)) {
                    Logger.w(TAG, "DeviceIdGetter lan mac not found");
                }
            }

            if (TextUtils.isEmpty(wifiMac) && TextUtils.isEmpty(lanMac)) {
                Logger.w(TAG, "DeviceIdGetter lan mac and wifi mac not found");
                return;
            }

            boolean result = getDeviceIdDao().submitHardwareInfo(pDeviceId, wifiMac, lanMac);
            if (result) {
                if (!TextUtils.isEmpty(wifiMac)) {
                    Logger.w(TAG, "DeviceIdGetter wifi mac reported");
                    DeviceInfoSpConfig.setWifiMacReported(true);
                }

                if (!TextUtils.isEmpty(lanMac)) {
                    Logger.w(TAG, "DeviceIdGetter lan mac reported");
                    DeviceInfoSpConfig.setLanMacReported(true);
                }
            }
        } catch (Exception e) {
            Logger.e(TAG, "DeviceIdGetter, submitHardwareInfoIfNecessary error: " + e);
        }
    }


    @NonNull
    private static IDeviceIdDao getDeviceIdDao() {
        return DeviceStatusDaoHelper.getDeviceIdDao(MdmEvnFactory.getInstance().getCurEnvironment().getUrl());
    }

}
