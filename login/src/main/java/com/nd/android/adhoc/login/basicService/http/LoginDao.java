package com.nd.android.adhoc.login.basicService.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nd.android.adhoc.basic.net.constant.AhdocHttpConstants;
import com.nd.android.adhoc.basic.net.dao.AdhocHttpDao;
import com.nd.android.adhoc.basic.net.exception.AdhocHttpException;
import com.nd.android.adhoc.login.basicService.data.http.ActivateHttpResult;
import com.nd.android.adhoc.login.basicService.data.http.ActivateUserResult;
import com.nd.android.adhoc.login.basicService.data.http.BindResult;
import com.nd.android.adhoc.login.basicService.data.http.GetDeviceStatusResult;
import com.nd.android.adhoc.login.basicService.data.http.GetOldTokenResult;
import com.nd.android.adhoc.login.basicService.data.http.GetTokenResult;
import com.nd.android.adhoc.login.basicService.data.http.LoginUserResult;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

//http://wiki.sdp.nd/
// index.php?title=Mdm#.5BPOST.5D.2Fv1.1.2Fregiste.2Factivate.2F_.E8.AE.BE.E5.A4.87.E8.AF.B7.E6.B1.82.E6.BF.80.E6.B4.BB
public class LoginDao extends AdhocHttpDao {

    public LoginDao(String pBaseUrl) {
        super(pBaseUrl);
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
            String result = postAction().post("/v1.1/registe/policyset/",
                    String.class, content, null);
        } catch (Exception e) {
            throw new AdhocHttpException("", AhdocHttpConstants.ADHOC_HTTP_ERROR);
        }
    }

    public GetDeviceStatusResult getDeviceStatus(String pDeviceID, String pSerialNum) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("device_token", pDeviceID);
        map.put("serial_num", pSerialNum);

        Gson gson = new GsonBuilder().create();
        String content = gson.toJson(map);

        return postAction().post("/v1.1/registe/getDeviceStatus/", GetDeviceStatusResult.class,
                content, null);
    }

    public BindResult bindDevice(String pDeviceToken,String pPushID,
                                 String pSerialNum) throws AdhocHttpException {
        Map<String, Object> map = new HashMap<>();
        map.put("device_token", pDeviceToken);
        map.put("serial_num", pSerialNum);
        map.put("type", 1);
        map.put("pushid", pPushID);

        try {
            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);

            return postAction().post("/v1.1/registe/pushid/", BindResult.class, content, null);
        } catch (RuntimeException e) {
            throw new AdhocHttpException("", AhdocHttpConstants.ADHOC_HTTP_ERROR);
        }
    }

    public BindResult bindDeviceWithPushChannelType(String pDeviceToken,String pPushID,
                                 String pSerialNum, int pPushChannelType) throws AdhocHttpException {
        Map<String, Object> map = new HashMap<>();
        map.put("device_token", pDeviceToken);
        map.put("serial_num", pSerialNum);
        map.put("type", pPushChannelType);
        map.put("pushid", pPushID);

        try {
            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);

            return postAction().post("/v1.1/registe/pushid/", BindResult.class, content, null);
        } catch (RuntimeException e) {

            throw new AdhocHttpException("", AhdocHttpConstants.ADHOC_HTTP_ERROR);
        }
    }

    public BindResult bindDevice(String pOldToken, String pDeviceToken,String pPushID,
                                 String pSerialNum) throws AdhocHttpException {
        Map<String, Object> map = new HashMap<>();
        map.put("old_device_token", pOldToken);
        map.put("device_token", pDeviceToken);
        map.put("serial_num", pSerialNum);
        map.put("type", 1);
        map.put("pushid", pPushID);

        try {
            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);

            return postAction().post("/v1.1/registe/pushid/", BindResult.class, content, null);
        } catch (RuntimeException e) {

            throw new AdhocHttpException("", AhdocHttpConstants.ADHOC_HTTP_ERROR);
        }
    }

    public LoginUserResult loginUser(String pEncryptUsername, String pEncryptPassword)
            throws AdhocHttpException {
        Map<String, Object> map = new HashMap<>();
        map.put("username", pEncryptUsername);
        map.put("passwd", pEncryptPassword);

        Gson gson = new GsonBuilder().create();
        String content = gson.toJson(map);

        return postAction().post("/v1/ucUser/login/", LoginUserResult.class,
                content, null);
    }

    @Deprecated
    public ActivateHttpResult activateUser(String pUserToken, String pDeviceToken)
                            throws Exception {
//        ActivateHttpRequest request = new ActivateHttpRequest();
//        request.user_token = pUserToken;
//        request.device_token = pDeviceToken;
//
//        String uctoken = SecurityDelegate.getInstance().calculateMACContent(Method.POST, "uc.mdm",
//                "/device_token=" + pDeviceToken, false);
//
//        JSONObject object = null;
//        object = new JSONObject(uctoken);
//        request.uc.access_token = object.optString("access_token");
//        request.uc.mac = object.optString("mac");
//        request.uc.nonce = object.optString("nonce");
//
//        return postAction().post("/v1.1/registe/activate/", ActivateHttpResult.class, request);
        return null;
    }

    public ActivateUserResult activateUser(String pUserToken, String pDeviceID,
                                           int pChannelType, String pLoginToken) throws AdhocHttpException{
        Map<String, Object> map = new HashMap<>();
        map.put("user_token", pUserToken);
        map.put("device_token", pDeviceID);
        map.put("type", pChannelType);

        Map<String, String> header = new HashMap<>();
        map.put("Authorization", pLoginToken);

        Gson gson = new GsonBuilder().create();
        String content = gson.toJson(map);

        return postAction().post("/v1.1/registe/activate/", ActivateUserResult.class,
                content, header);
    }

    @Deprecated
    public ActivateHttpResult activateUserWithPushChannelType(String pUserToken, String
            pDeviceToken, int pPushChannelType)
            throws Exception {
//        ActivateHttpRequest request = new ActivateHttpRequest();
//        request.user_token = pUserToken;
//        request.device_token = pDeviceToken;
//        request.type = pPushChannelType;
//
//        String uctoken = SecurityDelegate.getInstance().calculateMACContent(Method.POST, "uc.mdm",
//                "/device_token=" + pDeviceToken, false);
//
//        JSONObject object = null;
//        object = new JSONObject(uctoken);
//        request.uc.access_token = object.optString("access_token");
//        request.uc.mac = object.optString("mac");
//        request.uc.nonce = object.optString("nonce");
//
//        return postAction().post("/v1.1/registe/activate/", ActivateHttpResult.class, request);
        return null;
    }

    /*
     "hardware":{
       "build_sn": "08002800A8C5"  //设备唯一标识，選填
       "cpu_sn": "08002800A8C5"  //设备唯一标识，選填
       "imei": "08002800A8C5"  //设备唯一标识，選填
       "wifi_mac": "08002800A8C5"  //设备唯一标识，選填
       "btooth_mac": "08002800A8C5"  //设备唯一标识，選填
       "android_id":"xxxxxxxx"
       "serial_no": "08002800A8C5"  //设备唯一标识，選填
     }
    "device_token":"xxxxxxxx" //新的devicetoken
     */
    public GetTokenResult confirmDeviceID(String  pBuildSn, String pCpuSn, String pIMEI, String pWifiMac,
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
        data.put("device_token", pDeviceID);

        try {
            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(data);

            return postAction().post("/v1.1/registe/getDeviceToken/", GetTokenResult.class,
                    content, null);
        } catch (RuntimeException e) {
            throw new AdhocHttpException("", AhdocHttpConstants.ADHOC_HTTP_ERROR);
        }
    }

    public GetOldTokenResult getOldToken(String  pBuildSn, String pCpuSn, String pIMEI, String pWifiMac,
                                         String pBlueToothMac, String pSerialNo,
                                         String pDeviceToken)
            throws Exception {

        Map<String, Object> map = new HashMap<>();
        map.put("build_sn", pBuildSn);
        map.put("cpu_sn", pCpuSn);
        map.put("imei", pIMEI);
        map.put("wifi_mac", pWifiMac);
        map.put("btooth_mac", pBlueToothMac);
        map.put("serial_no", pSerialNo);
        map.put("device_token", pDeviceToken);

        try {
            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);

            return postAction().post("/v1.1/registe/getOldDeviceToken/", GetOldTokenResult.class,
                    content, null);
        } catch (RuntimeException e) {
            throw new AdhocHttpException("", AhdocHttpConstants.ADHOC_HTTP_ERROR);
        }

    }
}
