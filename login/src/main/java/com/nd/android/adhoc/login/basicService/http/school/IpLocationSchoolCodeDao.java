package com.nd.android.adhoc.login.basicService.http.school;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nd.android.adhoc.basic.net.dao.AdhocHttpDao;
import com.nd.android.adhoc.login.basicService.http.school.resp.IpLocationSchoolCodeResp;

import java.util.HashMap;
import java.util.Map;

public class IpLocationSchoolCodeDao extends AdhocHttpDao {
    public IpLocationSchoolCodeDao(String pBaseUrl) {
        super(pBaseUrl);
    }

    public IpLocationSchoolCodeResp getSchoolCodeByIp(String pIp) throws Exception {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("ip", pIp);

            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);
            return postAction().post("/v2/group/ipinfo/", IpLocationSchoolCodeResp.class,
                    content, null);
        } catch (Exception pE) {
            Log.e("yhq", "IpLocationSchoolCodeDao error happpen:" + postAction().getBaseUrl()
                    + "v2/group/ipinfo/" + " " + "Msg:" + pE.getMessage());
            throw new Exception(pE.getMessage());
        }
    }

    public IpLocationSchoolCodeResp getSchoolCodeByLocation(String pLat, String pLgn,
                                                            int pScope) throws Exception {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("lat", pLat);
            map.put("lgn", pLgn);
            map.put("scope", pScope);

            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);
            return postAction().post("/v2/group/geoinfo/", IpLocationSchoolCodeResp.class,
                    content, null);
        } catch (Exception pE) {
            Log.e("yhq", "IpLocationSchoolCodeDao error happpen:" + postAction().getBaseUrl()
                    + "v2/group/geoinfo/" + " " + "Msg:" + pE.getMessage());
            throw new Exception(pE.getMessage());
        }
    }
}
