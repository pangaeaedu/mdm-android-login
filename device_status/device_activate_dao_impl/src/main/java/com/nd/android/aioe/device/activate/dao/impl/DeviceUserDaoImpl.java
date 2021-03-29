package com.nd.android.aioe.device.activate.dao.impl;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.net.dao.AdhocHttpDao;
import com.nd.android.aioe.device.activate.dao.api.IDeviceUserDao;

import java.util.HashMap;
import java.util.Map;

/**
 * getUserInfo
 *
 * {
 * "serial_no": "08002800A8C5"  //序列号，非必填，查询用户账号
 * "device_token":"08002800A8C5"  //MDM分配给设备的唯一标识，必填
 * "type":2  //   1=android,2=ios,3=pc,12=iot，5002=android机器人，必填
 * "realtype":11,  //11=不需要用户登陆(pc)，12=egypt/saas化，不填使用默认激活方式，非必填
 * "groupcode":"0090",  //通过获取组织树得到的groupcode,设备激活后设备放入这个节点下，不填放入默认组，0080=机器人组，非必填
 * "device_num":"" //设备唯一识别码
 * "root_groupcode":"00",  //根组织code，非必填，多租户情况下，不选择组的时候一定必须带上这个，这样保证激活到对应租户下的default组
 * "org_id":"xxxxxx" //组织id，非必填，普米设备使用
 * }
 */

//@Route(path = IDeviceUserDao.ROUTE_PATH)
class DeviceUserDaoImpl extends AdhocHttpDao implements IDeviceUserDao {

    DeviceUserDaoImpl(String pBaseUrl) {
        super(pBaseUrl);
    }

    @Override
    public <T> T getUserInfo(@NonNull Class<T> pClass, @NonNull String pDeviceID, int pDeviceType) throws AdhocException {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("device_token", pDeviceID);
            map.put("type", pDeviceType);

            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);

            return postAction().post("/v1.1/enroll/getUserInfo/", pClass,
                    content, null);
        } catch (Exception pE) {
            Logger.e("DeviceStatus", "DeviceUserDaoImpl, getUserInfo error: " + pE.getMessage());
            throw new AdhocException(pE.getMessage());
        }

    }
}
