package com.nd.android.aioe.device.activate.dao.impl;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.net.dao.AdhocHttpDao;
import com.nd.android.aioe.device.activate.dao.api.IDeviceActivateDao;
import com.nd.android.aioe.device.activate.dao.api.constant.ActivateChannel;

import java.util.HashMap;
import java.util.Map;


/**
 * activate
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
 * getActivateResult
 * {
 * "errcode":0   ////激活成功，-1=激活失败
 * "msgcode" :"002" //失败原因,010=激活进行中，020=mdm异常，030=自动激活失效（如果失效表示不再自动登录，输入用户名和密码登录），040=超过设备运行被激活的次数（目前一台设备只运行同时被激活一次，所以表示该设备已经被用），050=超过用户允许激活的设备数（目前一个用户只运行同时被注册一次，所以表示该用户已经被用），060=组不存在，080=普米设备租户还未创建
 * "status":1 //成功会带上状态，4=使用中表示激活成功
 * "nick_name":"xxxxx" //成功会带上用户名
 * "requestid":"xcxxxxxxxxxxxxxxxxxxxxxxxxxxx"   //唯一标识，等于sessionid
 * "user_id":"xxxxxx"  //uc用户id
 * }
 * getActivateResult
 * {
 * "errcode":0   ////激活成功，-1=激活失败
 * "msgcode" :"002" //失败原因,010=激活进行中，020=mdm异常，030=自动激活失效（如果失效表示不再自动登录，输入用户名和密码登录），040=超过设备运行被激活的次数（目前一台设备只运行同时被激活一次，所以表示该设备已经被用），050=超过用户允许激活的设备数（目前一个用户只运行同时被注册一次，所以表示该用户已经被用），060=组不存在，080=普米设备租户还未创建
 * "status":1 //成功会带上状态，4=使用中表示激活成功
 * "nick_name":"xxxxx" //成功会带上用户名
 * "requestid":"xcxxxxxxxxxxxxxxxxxxxxxxxxxxx"   //唯一标识，等于sessionid
 * "user_id":"xxxxxx"  //uc用户id
 * }
 */


/** getActivateResult
 * {
 *     "errcode":0   ////激活成功，-1=激活失败
 *     "msgcode" :"002" //失败原因,010=激活进行中，020=mdm异常，030=自动激活失效（如果失效表示不再自动登录，输入用户名和密码登录），040=超过设备运行被激活的次数（目前一台设备只运行同时被激活一次，所以表示该设备已经被用），050=超过用户允许激活的设备数（目前一个用户只运行同时被注册一次，所以表示该用户已经被用），060=组不存在，080=普米设备租户还未创建
 *     "status":1 //成功会带上状态，4=使用中表示激活成功
 *      "nick_name":"xxxxx" //成功会带上用户名
 *     "requestid":"xcxxxxxxxxxxxxxxxxxxxxxxxxxxx"   //唯一标识，等于sessionid
 *     "user_id":"xxxxxx"  //uc用户id
 * }
 */


/** login
 *  {
 *     "result":"success",   //表示成功
 *     "username":"",
 *     "nickname":"",
 *     "cdmloginToken":""
 * }
 */

//@Route(path = IDeviceActivateDao.ROUTE_PATH)
class DeviceActivateDaoImpl extends AdhocHttpDao implements IDeviceActivateDao {

    private static final String TAG = "DeviceStatus";

    DeviceActivateDaoImpl(@NonNull String pBaseUrl) {
        super(pBaseUrl);
    }

    @Override
    public <T> T activate(@NonNull Class<T> pClass,
                          @NonNull String pDeviceID,
                          int pDeviceType,
                          @NonNull String pSerialNo,
                          @NonNull String pDeviceSerialNo,
                          @NonNull ActivateChannel pChannel,
                          @NonNull String pLoginToken,
                          String pOrgId) throws AdhocException {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("device_token", pDeviceID);
            map.put("type", pDeviceType);

            Map<String, String> header;
            header = new HashMap<>();
            header.put("channel", pChannel.getValue());
            if (pChannel == ActivateChannel.Uc) {
                header.put("Authorization", pLoginToken);
            }

            map.put("serial_no", pSerialNo);
            map.put("device_num", pDeviceSerialNo);
            map.put("org_id", pOrgId);
            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);

            return postAction().post("/v1.1/enroll/activate/", pClass,
                    content, header);
        } catch (Exception pE) {
            Logger.e(TAG, "DeviceActivateDao activate1 error: " + pE.getMessage());
            throw new AdhocException(pE.getMessage());
        }
    }

    @Override
    public <T> T activate(@NonNull Class<T> pClass,
                          @NonNull String pDeviceID,
                          int pDeviceType,
                          @NonNull String pSerialNo,
                          @NonNull String pSchoolGroupCode,
                          @NonNull String pDeviceSerialNo,
                          @NonNull ActivateChannel pChannel,
                          @NonNull String pLoginToken,
                          int pRealType,
                          String pOrgId) throws AdhocException {

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("device_token", pDeviceID);
            map.put("type", pDeviceType);

            Map<String, String> header;
            header = new HashMap<>();
            header.put("channel", pChannel.getValue());
            if (pChannel == ActivateChannel.Uc) {
                header.put("Authorization", pLoginToken);
            }

            if (!TextUtils.isEmpty(pSchoolGroupCode)) {
                map.put("groupcode", pSchoolGroupCode);
            }

            map.put("realtype", pRealType);
            map.put("serial_no", pSerialNo);
            map.put("device_num", pDeviceSerialNo);
            map.put("org_id", pOrgId);
            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);

            return postAction().post("/v1.1/enroll/activate/", pClass,
                    content, header);
        } catch (Exception pE) {
            Logger.e(TAG, "DeviceActivateDao activate2 error: " + pE.getMessage());
            throw new AdhocException(pE.getMessage());
        }
    }

    @Override
    public <T> T login(@NonNull Class<T> pClass, @NonNull String pUsername, @NonNull String pPassword) throws Exception {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("username", pUsername);
            map.put("passwd", pPassword);

            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);

            return postAction().post("/v1.1/enroll/login/", pClass,
                    content, null);
        } catch (Exception pE) {
            Logger.e(TAG, "DeviceActivateDao, login error: " + pE.getMessage());
            throw new AdhocException(pE.getMessage());
        }

    }


    @Override
    public <T> T getActivateResult(@NonNull Class<T> pClass,
                                   @NonNull String pDeviceID,
                                   int pDeviceType,
                                   @NonNull String pRequestID) throws AdhocException {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("device_token", pDeviceID);
            map.put("requestid", pRequestID);
            map.put("type", pDeviceType);

            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);

            return postAction().post("/v1.1/enroll/getActivateResult/", pClass,
                    content, null);
        } catch (Exception pE) {
            Logger.e(TAG, "DeviceActivateDao, getActivateResult error: " + pE.getMessage());
            throw new AdhocException(pE.getMessage());
        }

    }
}
