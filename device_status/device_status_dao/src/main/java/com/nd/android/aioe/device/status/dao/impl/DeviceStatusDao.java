package com.nd.android.aioe.device.status.dao.impl;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.net.dao.AdhocHttpDao;
import com.nd.android.adhoc.basic.net.exception.AdhocHttpException;
import com.nd.android.aioe.device.status.dao.api.IDeviceStatusDao;
import com.nd.android.aioe.device.status.dao.impl.constant.DeviceType;

import java.util.HashMap;
import java.util.Map;

 class DeviceStatusDao extends AdhocHttpDao implements IDeviceStatusDao {

    private static final String TAG = "DeviceStatus";

     DeviceStatusDao(@NonNull String pBaseUrl) {
        super(pBaseUrl);
    }

    @Override
    public <T> T getDeviceStatus(@NonNull Class<T> pClass, @NonNull String pDeviceID, @NonNull String pSerialNum) throws Exception {
        return getDeviceStatus(pClass, pDeviceID, pSerialNum, 0);
    }

    @Override
    public <T> T getDeviceStatus(@NonNull Class<T> pClass, @NonNull String pDeviceID, @NonNull String pSerialNum, int pNeedGroup) throws Exception {
        return getDeviceStatus(pClass,pDeviceID, pSerialNum, pNeedGroup, DeviceType.getValue());
    }

    @Override
    public <T> T getDeviceStatus(@NonNull Class<T> pClass, @NonNull String pDeviceID, @NonNull String pSerialNum, int pNeedGroup, int pDeviceType) throws Exception {

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("device_token", pDeviceID);
            map.put("serial_no", pSerialNum);
//            map.put("login_auto", pAutoLogin);
            map.put("type", pDeviceType);
            map.put("need_group", pNeedGroup);

            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);

            return postAction().post("/v1.1/enroll/getDeviceStatus/", pClass,
                    content, null);
        } catch (Exception pE) {
            Logger.e(TAG, "DeviceStatusDao, getDeviceStatus error:" + pE.getMessage());
            throw new AdhocHttpException(pE.getMessage());
        }
    }
}
