package com.nd.android.adhoc.login.basicService.http.school;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.net.dao.AdhocHttpDao;
import com.nd.android.adhoc.login.basicService.http.school.bean.CheckSchoolExitResult;
import com.nd.android.adhoc.login.basicService.http.school.resp.IpLocationSchoolCodeResp;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class IpLocationSchoolCodeDao extends AdhocHttpDao {

    private static final String TAG = "IpLocationSchoolCodeDao";

    public IpLocationSchoolCodeDao(String pBaseUrl) {
        super(pBaseUrl);
    }

    public IpLocationSchoolCodeResp getSchoolCodeByIp(String pRootCode, String pIp) throws Exception {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("ip", pIp);
            map.put("groupcpde", pRootCode);

            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);
            return postAction().post("/v2/group/ipinfo/", IpLocationSchoolCodeResp.class,
                    content, null);
        } catch (Exception pE) {
            Log.e(TAG, "IpLocationSchoolCodeDao error happpen:" + postAction().getBaseUrl()
                    + "v2/group/ipinfo/" + " " + "Msg:" + pE.getMessage());
            throw new Exception(pE.getMessage());
        }
    }

    public IpLocationSchoolCodeResp getSchoolCodeByLocation(String pRootCode, @NonNull String pLat, @NonNull String pLgn, int pMapType,
                                                            int pScope) throws Exception {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("lat", pLat);
            map.put("lgn", pLgn);
            map.put("scope", pScope);
            map.put("maptype", pMapType);

            map.put("groupcpde", pRootCode);

            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);
            return postAction().post("/v2/group/geoinfo/", IpLocationSchoolCodeResp.class,
                    content, null);
        } catch (Exception pE) {
            Log.e(TAG, "IpLocationSchoolCodeDao error happpen:" + postAction().getBaseUrl()
                    + "v2/group/geoinfo/" + " " + "Msg:" + pE.getMessage());
            throw new Exception(pE.getMessage());
        }
    }

    /**
     * 通过 groupcode、坐标 确定 学校是否存在
     *
     * @param groupcode groupcode
     * @param pLat      纬度
     * @param pLgn      经度
     * @param pScope    半径（米）
     * @return CheckSchoolExitResult
     * @throws Exception 请求异常
     */
    public CheckSchoolExitResult checkSchoolExit(@NonNull String groupcode, @NonNull String pLat, @NonNull String pLgn, int maptype, int pScope) throws Exception {
        try {
            JSONObject postContent = new JSONObject();
            postContent.put("groupcode", groupcode);

            JSONObject geoinfo = new JSONObject();
            geoinfo.put("lat", pLat);
            geoinfo.put("lgn", pLgn);
            geoinfo.put("maptype", maptype);
            geoinfo.put("scope", pScope);

            postContent.put("geoinfo", geoinfo);

            return postAction().post("/v2/group/exist", CheckSchoolExitResult.class, postContent.toString(), null);

        } catch (Exception e) {
            Logger.e(TAG, "checkSchoolExit error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 通过 groupcode、ip 确定 学校是否存在
     *
     * @param groupcode groupcode
     * @param pIp       ip 地址
     * @return CheckSchoolExitResult
     * @throws Exception 请求异常
     */
    public CheckSchoolExitResult checkSchoolExit(@NonNull String groupcode, @NonNull String pIp) throws Exception {
        try {
            JSONObject postContent = new JSONObject();
            postContent.put("groupcode", groupcode);
            postContent.put("ip", pIp);

            return postAction().post("/v2/group/exist", CheckSchoolExitResult.class, postContent.toString(), null);

        } catch (Exception e) {
            Logger.e(TAG, "checkSchoolExit error: " + e.getMessage());
            throw e;
        }
    }

}
