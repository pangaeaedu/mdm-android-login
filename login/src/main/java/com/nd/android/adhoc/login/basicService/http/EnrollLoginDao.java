package com.nd.android.adhoc.login.basicService.http;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.net.constant.AhdocHttpConstants;
import com.nd.android.adhoc.basic.net.dao.AdhocHttpDao;
import com.nd.android.adhoc.basic.net.exception.AdhocHttpException;
import com.nd.android.adhoc.login.basicService.data.http.ActivateUserResponse;
import com.nd.android.adhoc.login.basicService.data.http.BindPushIDResponse;
import com.nd.android.adhoc.login.basicService.data.http.ConfirmDeviceIDResponse;
import com.nd.android.adhoc.login.basicService.data.http.GetActivateUserResultResponse;
import com.nd.android.adhoc.login.basicService.data.http.GetUserInfoResponse;
import com.nd.android.adhoc.login.basicService.data.http.LoginUserResponse;
import com.nd.android.adhoc.login.basicService.data.http.QueryDeviceStatusResponse;
import com.nd.android.adhoc.login.enumConst.ActivateUserType;
import com.nd.android.adhoc.login.enumConst.DeviceType;
import com.nd.android.adhoc.login.exception.GetUserInfoServerException;
import com.nd.android.adhoc.login.exception.LoginUserServerException;
import com.nd.android.adhoc.login.processOptimization.utils.LoginArgumentUtils;
import com.nd.android.adhoc.loginapi.exception.ActivateUserServerException;
import com.nd.android.adhoc.loginapi.exception.BindPushIDServerException;
import com.nd.android.adhoc.loginapi.exception.ConfirmIDServerException;
import com.nd.android.adhoc.loginapi.exception.QueryActivateUserResultException;
import com.nd.android.adhoc.loginapi.exception.QueryDeviceStatusServerException;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EnrollLoginDao extends AdhocHttpDao {
    public EnrollLoginDao(String pBaseUrl) {
        super(pBaseUrl);
    }

    public GetUserInfoResponse getUserInfo(String pDeviceID) throws Exception {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("device_token", pDeviceID);
            map.put("type", DeviceType.getDeviceType().getValue());

            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);

            return postAction().post("/v1.1/enroll/getUserInfo/", GetUserInfoResponse.class,
                    content, null);
        } catch (Exception pE) {
            Logger.e("yhq", "EnrollLoginDao error happpen:getUserInfo:" + pE.getMessage());
            throw new GetUserInfoServerException(pE.getMessage());
        }
    }

    @Deprecated
    public QueryDeviceStatusResponse queryDeviceStatus(String pDeviceID, String pSerialNum)
            throws Exception {
//        try {
//            Map<String, Object> map = new HashMap<>();
//            map.put("device_token", pDeviceID);
//            map.put("serial_no", pSerialNum);
//            map.put("type", DeviceType.getDeviceType().getValue());
//
//            Gson gson = new GsonBuilder().create();
//            String content = gson.toJson(map);
//
//            return postAction().post("/v1.1/enroll/getDeviceStatus/", QueryDeviceStatusResponse.class,
//                    content, null);
//        }catch (Exception pE){
//            Log.e("yhq", "EnrollLoginDao error happpen:"+ postAction().getBaseUrl()
//                    +"/v1.1/enroll/getDeviceStatus/"+" " + "Msg:"+pE.getMessage());
//            throw new QueryDeviceStatusServerException(pE.getMessage());
//        }
        return queryDeviceStatus(pDeviceID, pSerialNum, 0);
    }

    public QueryDeviceStatusResponse queryDeviceStatus(String pDeviceID, String pSerialNum, int
            pAutoLogin) throws Exception {
//        try {
//            Map<String, Object> map = new HashMap<>();
//            map.put("device_token", pDeviceID);
//            map.put("serial_no", pSerialNum);
//            map.put("login_auto", pAutoLogin);
//            map.put("type", DeviceType.getDeviceType().getValue());
//
//            Gson gson = new GsonBuilder().create();
//            String content = gson.toJson(map);
//
//            return postAction().post("/v1.1/enroll/getDeviceStatus/", QueryDeviceStatusResponse.class,
//                    content, null);
//        }catch (Exception pE){
//            Log.e("yhq", "EnrollLoginDao error happpen:"+ postAction().getBaseUrl()
//                    +"/v1.1/enroll/getDeviceStatus/"+" " + "Msg:"+pE.getMessage());
//            throw new QueryDeviceStatusServerException(pE.getMessage());
//        }
        return queryDeviceStatus(pDeviceID, pSerialNum, pAutoLogin, 0);
    }

    public QueryDeviceStatusResponse queryDeviceStatus(String pDeviceID, String pSerialNum, int
            pAutoLogin, int pNeedGroup) throws Exception {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("device_token", pDeviceID);
            map.put("serial_no", pSerialNum);
            map.put("login_auto", pAutoLogin);
            map.put("type", DeviceType.getDeviceType().getValue());
            map.put("need_group", pNeedGroup);

            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);

            return postAction().post("/v1.1/enroll/getDeviceStatus/", QueryDeviceStatusResponse.class,
                    content, null);
        } catch (Exception pE) {
            Logger.e("yhq", "EnrollLoginDao error happpen:queryDeviceStatus:" + pE.getMessage());
            throw new QueryDeviceStatusServerException(pE.getMessage());
        }
    }

    public LoginUserResponse loginUser(String pEncryptUsername, String pEncryptPassword)
            throws Exception {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("username", pEncryptUsername);
            map.put("passwd", pEncryptPassword);

            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);

            return postAction().post("/v1.1/enroll/login/", LoginUserResponse.class,
                    content, null);
        } catch (Exception pE) {
            Logger.e("yhq", "EnrollLoginDao error happpen:loginUser" + pE.getMessage());
            throw new LoginUserServerException(pE.getMessage());
        }

    }

    public GetActivateUserResultResponse getActivateResult(String pDeviceID, String pRequestID)
            throws Exception {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("device_token", pDeviceID);
            map.put("requestid", pRequestID);
            map.put("type", DeviceType.getDeviceType().getValue());

            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);

            return postAction().post("/v1.1/enroll/getActivateResult/", GetActivateUserResultResponse.class,
                    content, null);
        } catch (Exception pE) {
            Logger.e("yhq", "EnrollLoginDao error happpen:getActivateResult:" + pE.getMessage());
            throw new QueryActivateUserResultException(pE.getMessage());
        }

    }

    public ActivateUserResponse activateUser(String pDeviceID, String pSerialNo,
                                             ActivateUserType pUserType, String pLoginToken)
            throws Exception {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("device_token", pDeviceID);
            map.put("type", DeviceType.getDeviceType().getValue());

            Map<String, String> header = null;
            header = new HashMap<>();
            header.put("channel", pUserType.getValue());
            if (pUserType == ActivateUserType.Uc) {
                header.put("Authorization", pLoginToken);
            }

            map.put("serial_no", pSerialNo);

            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);

            return postAction().post("/v1.1/enroll/activate/", ActivateUserResponse.class,
                    content, header);
        } catch (Exception pE) {
            Logger.e("yhq", "EnrollLoginDao error happpen:activateUser:" + pE.getMessage());
            throw new ActivateUserServerException(pE.getMessage());
        }

    }

    public ActivateUserResponse activateUser(String pDeviceID, String pSerialNo,
                                             String pSchoolGroupCode,
                                             ActivateUserType pUserType, String pLoginToken,
                                             int pRealType)
            throws Exception {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("device_token", pDeviceID);
            map.put("type", DeviceType.getDeviceType().getValue());

            Map<String, String> header = null;
            header = new HashMap<>();
            header.put("channel", pUserType.getValue());
            if (pUserType == ActivateUserType.Uc) {
                header.put("Authorization", pLoginToken);
            }

            if (!TextUtils.isEmpty(pSchoolGroupCode)) {
                map.put("groupcode", pSchoolGroupCode);
            }

            map.put("realtype", pRealType);
            map.put("serial_no", pSerialNo);

            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);

            return postAction().post("/v1.1/enroll/activate/", ActivateUserResponse.class,
                    content, header);
        } catch (Exception pE) {
            Logger.e("yhq", "EnrollLoginDao error happpen:activateUser:" + pE.getMessage());
            throw new ActivateUserServerException(pE.getMessage());
        }

    }

    public void requestPolicySet(String pDeviceToken, long pTime, JSONObject pData) throws
            AdhocHttpException {
        try {
            JSONObject object = new JSONObject();
            object.put("device_token", pDeviceToken);
            object.put("crtime", pTime);
            object.put("data", pData);
            object.put("type", 1);
            String content = object.toString();
            postAction().post("/v1.1/enroll/policyset/",
                    String.class, content, null);
        } catch (Exception e) {
            Logger.e("yhq", "EnrollLoginDao error happpen:requestPolicySet:" + e.getMessage());
            throw new AdhocHttpException("", AhdocHttpConstants.ADHOC_HTTP_ERROR);
        }
    }

    public BindPushIDResponse bindDeviceIDToPushID(String pDeviceID, String pPushID) throws Exception {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("device_token", pDeviceID);
            map.put("type", DeviceType.getDeviceType().getValue());
            map.put("pushid", pPushID);

            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);

            return postAction().post("/v1.1/enroll/pushid/", BindPushIDResponse.class, content, null);
        } catch (Exception e) {
            Logger.e("yhq", "EnrollLoginDao error happpen:bindDeviceIDToPushID:" + e.getMessage());
            throw new BindPushIDServerException(e.getMessage());
        }
    }

    /*
    "hardware":{
      "build_sn": "08002800A8C5"  //设备唯一标识，選填
      "cpu_sn": "08002800A8C5"  //设备唯一标识，選填
      "imei": "08002800A8C5"  //设备唯一标识，選填
      "wifi_mac": "08002800A8C5"  //设备唯一标识，選填
      "btooth_mac": "08002800A8C5"  //设备唯一标识，選填
      "android_id":"xxxxxxxx"  //之前的serial_no
      "serial_no": "08002800A8C5"  //设备唯一标识，選填
    }
   "devicetype":1  //设备类型
   "devicetoken":"xxxxxxxx" //新的devicetoken
    */
//    public ConfirmDeviceIDResponse confirmDeviceID(String  pBuildSn, String pCpuSn, String pIMEI, String pWifiMac,
//                                                      String pBlueToothMac, String pSerialNo, String pAndroidID,
//                                                      String pDeviceID) throws Exception{
//        Map<String, Object> mapHardware = new HashMap<>();
//
//        if(!TextUtils.isEmpty(pBuildSn)) {
//            mapHardware.put("build_sn", pBuildSn);
//        }
//
//        if(!TextUtils.isEmpty(pCpuSn)){
//            mapHardware.put("cpu_sn", pCpuSn);
//        }
//
//        if(!TextUtils.isEmpty(pIMEI)) {
//            mapHardware.put("imei", pIMEI);
//        }
//
//        if(!TextUtils.isEmpty(pWifiMac)) {
//            mapHardware.put("wifi_mac", pWifiMac);
//        }
//
//        if(!TextUtils.isEmpty(pBlueToothMac)) {
//            mapHardware.put("btooth_mac", pBlueToothMac);
//        }
//
//        if(!TextUtils.isEmpty(pSerialNo)){
//            mapHardware.put("serial_no", pSerialNo);
//        }
//
//        if(!TextUtils.isEmpty(pAndroidID)) {
//            mapHardware.put("android_id", pAndroidID);
//        }
//
//        Map<String, Object> data = new HashMap<>();
//        data.put("hardware", mapHardware);
//        data.put("type", DeviceType.getDeviceType().getValue());  //设备类型，1代表android
//        data.put("device_token", pDeviceID);
//
//        try {
//            Gson gson = new GsonBuilder().create();
//            String content = gson.toJson(data);
//
//            return postAction().post("/v1.1/enroll/getDeviceToken/", ConfirmDeviceIDResponse.class,
//                    content, null);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Logger.e("yhq", "EnrollLoginDao error happpen:"+ postAction().getBaseUrl()
//                    +"/v1.1/enroll/getDeviceToken/"+" " + "Msg:"+e.getMessage());
//            throw new ConfirmIDServerException(e.getMessage());
//        }
//    }

    public ConfirmDeviceIDResponse confirmDeviceID(String pBuildSn, String pCpuSn, String pIMEI,
                                                   String pWifiMac, String pLanMac, String pBlueToothMac,
                                                   String pSerialNo, String pAndroidID, String pDeviceID) throws Exception {
        Map<String, Object> mapHardware = LoginArgumentUtils.genHardwareMap(pBuildSn, pCpuSn, pIMEI, pWifiMac,
                pLanMac, pBlueToothMac, pSerialNo, pAndroidID);

        Map<String, Object> data = new HashMap<>();
        data.put("hardware", mapHardware);
        data.put("type", DeviceType.getDeviceType().getValue());  //设备类型，1代表android
        data.put("device_token", pDeviceID);

        try {
            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(data);

            return postAction().post("/v1.1/enroll/getDeviceToken/", ConfirmDeviceIDResponse.class,
                    content, null);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("yhq", "EnrollLoginDao error happpen:confirmDeviceID:" + e.getMessage());
            throw new ConfirmIDServerException(e.getMessage());
        }
    }

    public ConfirmDeviceIDResponse confirmDeviceID(Map<String, Object> pHardwareMap,
                                                   String pDeviceID) throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("hardware", pHardwareMap);
        data.put("type", DeviceType.getDeviceType().getValue());  //设备类型，1代表android
        data.put("device_token", pDeviceID);

        try {
            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(data);

            return postAction().post("/v1.1/enroll/getDeviceToken/", ConfirmDeviceIDResponse.class,
                    content, null);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("yhq", "EnrollLoginDao error happpen:confirmDeviceID:" + e.getMessage());
            throw new ConfirmIDServerException(e.getMessage());
        }
    }

    /*

{
    "device_token": "08002800xxxxA8C5"
    "lan_mac": "xxxxxxx" ,
    "wifi_mac": "xxxxxxx" ,
     ...
    "xxxxx":"xxxxxx" //更多硬件信息
}
     */
    public Boolean reportHardwareInfo(String pDeviceID, Map<String, Object> pInfos) throws
            Exception {
        try {
            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(pInfos);

            String response = postAction().post("/v1.1/enroll/dtoken/completion",
                    String.class, content, null);

            if (TextUtils.isEmpty(response)) {
                return false;
            }

            com.alibaba.fastjson.JSONObject object = JSON.parseObject(response);
            int errorcode = object.getInteger("errcode");
            if (errorcode == 0) {
                return true;
            }

            return false;
        } catch (Exception e) {
            Logger.e("yhq", "EnrollLoginDao error happpen:reportHardwareInfo:" + e.getMessage());
            throw new BindPushIDServerException(e.getMessage());
        }
    }

}
