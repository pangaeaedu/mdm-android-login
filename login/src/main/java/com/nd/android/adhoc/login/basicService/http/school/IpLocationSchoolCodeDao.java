package com.nd.android.adhoc.login.basicService.http.school;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nd.android.adhoc.basic.net.dao.AdhocHttpDao;
import com.nd.android.adhoc.login.basicService.http.school.resp.IpSchoolCodeResp;
import com.nd.android.adhoc.login.basicService.http.school.resp.LocationSchoolCodeResp;

import java.util.HashMap;
import java.util.Map;

public class IpLocationSchoolCodeDao extends AdhocHttpDao {
    public IpLocationSchoolCodeDao(String pBaseUrl) {
        super(pBaseUrl);
    }

    public IpSchoolCodeResp getSchoolCodeByIp(String pIp, String pDeviceToken) throws Exception {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("ip", pIp);
            map.put("device_token", pDeviceToken);

            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);
            return postAction().post("/v1.1/enroll/ipinfo", IpSchoolCodeResp.class,
                    content, null);
        } catch (Exception pE) {
            Log.e("yhq", "IpLocationSchoolCodeDao error happpen:" + postAction().getBaseUrl()
                    + "v1.1/enroll/ipinfo" + " " + "Msg:" + pE.getMessage());
            throw new Exception(pE.getMessage());
        }
    }

    public LocationSchoolCodeResp getSchoolCodeByLocation(String pLat, String pLgn,
                                                          String pScope) throws Exception {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("lat", pLat);
            map.put("lgn", pLgn);
            map.put("scope", pScope);

            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);
            return postAction().post("/v1.1/enroll/geoinfo/", LocationSchoolCodeResp.class,
                    content, null);
        } catch (Exception pE) {
            Log.e("yhq", "IpLocationSchoolCodeDao error happpen:" + postAction().getBaseUrl()
                    + "v1.1/enroll/geoinfo/" + " " + "Msg:" + pE.getMessage());
            throw new Exception(pE.getMessage());
        }
    }
}
