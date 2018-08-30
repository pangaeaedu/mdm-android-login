package com.nd.android.adhoc.login.basicService.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nd.android.adhoc.basic.net.constant.AhdocHttpConstants;
import com.nd.android.adhoc.basic.net.dao.AdhocHttpDao;
import com.nd.android.adhoc.basic.net.exception.AdhocHttpException;
import com.nd.android.adhoc.login.basicService.data.http.ActivateHttpRequest;
import com.nd.android.adhoc.login.basicService.data.http.ActivateHttpResult;
import com.nd.android.adhoc.login.basicService.data.http.BindResult;
import com.nd.android.smartcan.network.Method;
import com.nd.smartcan.core.security.SecurityDelegate;

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
//        Map<String, Object> map = new HashMap();
//        map.put("device_token", pDeviceToken);
//        map.put("crtime", pTime);
//        map.put("data", pData);
        try {
//            Gson gson = new GsonBuilder().create();
//            String content = gson.toJson(map);
            JSONObject object = new JSONObject();
            object.put("device_token",pDeviceToken);
            object.put("crtime", pTime);
            object.put("data", pData);
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
        map.put("pushid", pPushID);

        try {
            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);

            return postAction().post("/v1.1/registe/pushid/", BindResult.class, content, null);
        } catch (RuntimeException e) {

            throw new AdhocHttpException("", AhdocHttpConstants.ADHOC_HTTP_ERROR);
        }

    }

    public ActivateHttpResult activateUser(String pUserToken, String pDeviceToken)
                            throws Exception {
        ActivateHttpRequest request = new ActivateHttpRequest();
        request.user_token = pUserToken;
        request.device_token = pDeviceToken;

        String uctoken = SecurityDelegate.getInstance().calculateMACContent(Method.POST, "uc.mdm",
                "/device_token=" + pDeviceToken, false);

        JSONObject object = null;
        object = new JSONObject(uctoken);
        request.uc.access_token = object.optString("access_token");
        request.uc.mac = object.optString("mac");
        request.uc.nonce = object.optString("nonce");

        return postAction().post("/v1.1/registe/activate/", ActivateHttpResult.class, request);
    }
}
