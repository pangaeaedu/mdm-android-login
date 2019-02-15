package com.nd.android.adhoc.login.basicService.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nd.android.adhoc.basic.net.constant.AhdocHttpConstants;
import com.nd.android.adhoc.basic.net.dao.AdhocHttpDao;
import com.nd.android.adhoc.basic.net.exception.AdhocHttpException;
import com.nd.android.adhoc.login.basicService.data.http.ActivateUserResponse;
import com.nd.android.adhoc.login.basicService.data.http.BindPushIDResponse;
import com.nd.android.adhoc.login.basicService.data.http.ConfirmDeviceIDResponse;
import com.nd.android.adhoc.login.basicService.data.http.GetActivateUserResultResponse;
import com.nd.android.adhoc.login.basicService.data.http.LoginUserResponse;
import com.nd.android.adhoc.login.basicService.data.http.QueryDeviceStatusResponse;
import com.nd.android.adhoc.login.enumConst.ActivateUserType;
import com.nd.android.adhoc.login.enumConst.DeviceType;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EnrollLoginDao extends AdhocHttpDao {
    public EnrollLoginDao(String pBaseUrl) {
        super(pBaseUrl);
    }


    public QueryDeviceStatusResponse queryDeviceStatus(String pDeviceID, String pSerialNum) throws AdhocHttpException {
        Map<String, Object> map = new HashMap<>();
        map.put("device_token", pDeviceID);
        map.put("serial_no", pSerialNum);
        map.put("type", DeviceType.Android.getValue());

        Gson gson = new GsonBuilder().create();
        String content = gson.toJson(map);

        return postAction().post("/v1.1/enroll/getDeviceStatus/", QueryDeviceStatusResponse.class,
                content, null);
    }


    public LoginUserResponse loginUser(String pEncryptUsername, String pEncryptPassword)
            throws AdhocHttpException {
        Map<String, Object> map = new HashMap<>();
        map.put("username", pEncryptUsername);
        map.put("passwd", pEncryptPassword);

        Gson gson = new GsonBuilder().create();
        String content = gson.toJson(map);

        return postAction().post("/v1/ucUser/login/", LoginUserResponse.class,
                content, null);
    }

    public GetActivateUserResultResponse getActivateResult(String pDeviceID, String pRequestID)
            throws
            AdhocHttpException{
        Map<String, Object> map = new HashMap<>();
        map.put("device_token", pDeviceID);
        map.put("requestid", pRequestID);
        map.put("type", DeviceType.Android.getValue());

        Gson gson = new GsonBuilder().create();
        String content = gson.toJson(map);

        return postAction().post("/v1.1/enroll/getActivateResult/", GetActivateUserResultResponse.class,
                content, null);
    }

    public ActivateUserResponse activateUser(String pDeviceID, String pSerialNo,
                                             ActivateUserType pUserType, String pLoginToken) throws AdhocHttpException {
        Map<String, Object> map = new HashMap<>();
        map.put("device_token", pDeviceID);
        map.put("type", DeviceType.Android.getValue());

        Map<String, String> header = null;
        header = new HashMap<>();
        header.put("channel", pUserType.getValue());
        if (pUserType == ActivateUserType.Uc) {
            header.put("Authorization", pLoginToken);

        } else {
            map.put("serial_no", pSerialNo);
        }

        Gson gson = new GsonBuilder().create();
        String content = gson.toJson(map);

        return postAction().post("/v1.1/enroll/activate/", ActivateUserResponse.class,
                content, header);
    }

    public void requestPolicySet(String pDeviceToken, long pTime, JSONObject pData) throws
            AdhocHttpException {
        try {
            JSONObject object = new JSONObject();
            object.put("device_token",pDeviceToken);
            object.put("crtime", pTime);
            object.put("data", pData);
            object.put("type", 1);
            String content = object.toString();
            String result = postAction().post("/v1.1/enroll/policyset/",
                    String.class, content, null);
        } catch (Exception e) {
            throw new AdhocHttpException("", AhdocHttpConstants.ADHOC_HTTP_ERROR);
        }
    }

    public BindPushIDResponse bindDeviceIDToPushID(String pDeviceID, String pPushID) throws AdhocHttpException {
        Map<String, Object> map = new HashMap<>();
        map.put("device_token", pDeviceID);
        map.put("type", DeviceType.Android.getValue());
        map.put("pushid", pPushID);

        try {
            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);

            return postAction().post("/v1.1/enroll/pushid/", BindPushIDResponse.class, content, null);
        } catch (RuntimeException e) {

            throw new AdhocHttpException("", AhdocHttpConstants.ADHOC_HTTP_ERROR);
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
    public ConfirmDeviceIDResponse confirmDeviceID(String  pBuildSn, String pCpuSn, String pIMEI, String pWifiMac,
                                                   String pBlueToothMac, String pSerialNo, String pAndroidID,
                                                   String pDeviceID) throws Exception{
        Map<String, Object> mapHardware = new HashMap<>();
        mapHardware.put("build_sn", pBuildSn);
        mapHardware.put("cpu_sn", pCpuSn);
        mapHardware.put("imei", pIMEI);
        mapHardware.put("wifi_mac", pWifiMac);
        mapHardware.put("btooth_mac", pBlueToothMac);
        mapHardware.put("serial_no", pSerialNo);
        mapHardware.put("android_id", pAndroidID);

        Map<String, Object> data = new HashMap<>();
        data.put("hardware", mapHardware);
        data.put("type", DeviceType.Android.getValue());  //设备类型，1代表android
        data.put("device_token", pDeviceID);

        try {
            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(data);

            return postAction().post("/v1.1/enroll/getDeviceToken/", ConfirmDeviceIDResponse.class,
                    content, null);
        } catch (RuntimeException e) {
            throw new AdhocHttpException("", AhdocHttpConstants.ADHOC_HTTP_ERROR);
        }
    }
}
