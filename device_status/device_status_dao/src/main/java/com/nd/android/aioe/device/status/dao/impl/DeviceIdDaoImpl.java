package com.nd.android.aioe.device.status.dao.impl;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.net.dao.AdhocHttpDao;
import com.nd.android.aioe.device.status.dao.api.IDeviceIdDao;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

class DeviceIdDaoImpl extends AdhocHttpDao implements IDeviceIdDao {

    private static final String TAG = "DeviceStatus";

    DeviceIdDaoImpl(String pBaseUrl) {
        super(pBaseUrl);
    }

    @Override
    public <T> T confirmDeviceID(@NonNull Class<T> pClass, @NonNull Map<String, Object> pHardwareMap, String pDeviceId, int pDeviceType) throws AdhocException {
        Map<String, Object> data = new HashMap<>();
        data.put("hardware", pHardwareMap);
        data.put("type", pDeviceType);  //设备类型，1代表android
        data.put("device_token", pDeviceId);

        try {
            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(data);

            return postAction().post("/v1.1/enroll/getDeviceToken/", pClass,
                    content, null);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(TAG, "DeviceIdDao, confirmDeviceID error: " + e.getMessage());
            throw new AdhocException(e.getMessage());
        }
    }

    @Override
    public <T> T bindDeviceIDWithPushID(@NonNull Class<T> pClass, @NonNull String pDeviceID, int pDeviceType, @NonNull String pPushID) throws AdhocException {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("device_token", pDeviceID);
            map.put("type", pDeviceType);
            map.put("pushid", pPushID);

            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);

            return postAction().post("/v1.1/enroll/pushid/", pClass, content, null);
        } catch (Exception e) {
            Logger.e(TAG, "DeviceIdDao, bindDeviceIDWithPushID error: " + e.getMessage());
            throw new AdhocException(e.getMessage());
        }
    }


    @Override
    public boolean submitHardwareInfo(@NonNull String pDeviceId, String pWifiMac, String pLanMac) throws Exception {
        try {

            Map<String, Object> hardwareInfos = new HashMap<>();

            if (!TextUtils.isEmpty(pWifiMac)) {
                hardwareInfos.put("wifi_mac", pWifiMac);
            } else {
                Logger.w(TAG, "submitHardwareInfo, wifi mac empty");
            }

            if (!TextUtils.isEmpty(pLanMac)) {
                hardwareInfos.put("lan_mac", pLanMac);
            } else {
                Logger.w(TAG, "submitHardwareInfo, lan mac not found");
            }

            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(hardwareInfos);

            String response = postAction().post("/v1.1/enroll/dtoken/completion",
                    String.class, content, null);

            if (TextUtils.isEmpty(response)) {
                return false;
            }

            JSONObject jsonObject = new JSONObject(response);
//            JSONObject object = JSON.parseObject(response);
            int errorcode = jsonObject.optInt("errcode", -1);
            return errorcode == 0;

        } catch (Exception e) {
            Logger.e(TAG, "DeviceIdDao, submitHardwareInfo error: " + e.getMessage());
            throw new AdhocException(e.getMessage());
        }
    }
}
