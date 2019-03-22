package com.nd.android.adhoc.login.basicService.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.net.constant.AhdocHttpConstants;
import com.nd.android.adhoc.basic.net.dao.AdhocHttpDao;
import com.nd.android.adhoc.basic.net.exception.AdhocHttpException;
import com.nd.android.adhoc.login.basicService.data.http.ActivateHttpResult;
import com.nd.android.adhoc.login.basicService.data.http.BindResult;
import com.nd.android.adhoc.login.basicService.data.http.GetOldTokenResult;
import com.nd.android.adhoc.login.enumConst.DeviceType;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

//http://wiki.sdp.nd/
// index.php?title=Mdm#.5BPOST.5D.2Fv1.1.2Fregiste.2Factivate.2F_.E8.AE.BE.E5.A4.87.E8.AF.B7.E6.B1.82.E6.BF.80.E6.B4.BB
public class LoginDao extends AdhocHttpDao {

    private static final String TAG = "LoginDao";

    public LoginDao(String pBaseUrl) {
        super(pBaseUrl);
    }

    @Deprecated
    public void requestPolicySet(String pDeviceToken, long pTime, JSONObject pData) throws
            AdhocHttpException {
        try {
            JSONObject object = new JSONObject();
            object.put("device_token",pDeviceToken);
            object.put("crtime", pTime);
            object.put("data", pData);
            object.put("type", DeviceType.Android.getValue());
            String content = object.toString();
            String result = postAction().post("/v1.1/registe/policyset/",
                    String.class, content, null);
        } catch (Exception e) {
            throw new AdhocHttpException("", AhdocHttpConstants.ADHOC_HTTP_ERROR);
        }
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

            Logger.d(TAG,"bind device id request:"+content);
            BindResult result = postAction().post("/v1.1/registe/pushid/", BindResult.class,
                    content, null);
            Logger.d(TAG,"bind device result:"+result.isSuccess());
            return result;
        } catch (RuntimeException e) {
            Logger.d(TAG,"bind device id result:"+e.getMessage());
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


    @Deprecated
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
