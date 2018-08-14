package com.nd.android.adhoc.login.basicService.http;

import com.nd.android.adhoc.basic.net.dao.AdhocHttpDao;
import com.nd.android.adhoc.basic.net.exception.AdhocHttpException;
import com.nd.android.adhoc.login.basicService.data.http.ActivateHttpRequest;
import com.nd.android.adhoc.login.basicService.data.http.ActivateHttpResult;
import com.nd.android.adhoc.login.basicService.data.http.BindResult;
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

    public void requestPolicySet(String pDeviceToken) throws AdhocHttpException {
        Map<String, Object> map = new HashMap();
        map.put("device_token", pDeviceToken);
        String result = postAction().post("/v1.1/registe/policyset/", String.class, map);
    }


    public BindResult bindDevice(String pDeviceToken, String pSerialNum,
                                 String pPushID) throws AdhocHttpException {
        Map<String, Object> map = new HashMap<>();
        map.put("device_token", pDeviceToken);
        map.put("serial_num", pSerialNum);
        map.put("pushid", pPushID);


        return postAction().post("/v1.1/registe/pushid/", BindResult.class, map);
    }

    public ActivateHttpResult activateUser(String pUserToken, String pDeviceToken)
                            throws Exception {
        ActivateHttpRequest request = new ActivateHttpRequest();
        request.mUserToken = pUserToken;
        request.mDeviceToken = pDeviceToken;

        String macJson = SecurityDelegate.getInstance()
                .calculateMACContent(1, "uc.im", "/v1.1/registe/activate/", true);

        JSONObject object = null;
        object = new JSONObject(macJson);
        request.mParams.mAccessToken = object.optString("access_token");
        request.mParams.mMac = object.optString("mac");
        request.mParams.mNonce = object.optString("nonce");

        return postAction().post("/v1.1/registe/activate/", ActivateHttpResult.class, request);
    }
}
