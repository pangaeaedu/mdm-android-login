package com.nd.android.aioe.group.info.dao.impl;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.net.dao.AdhocHttpDao;
import com.nd.android.aioe.group.info.dao.api.IGroupCheckDao;
import com.nd.android.aioe.group.info.dao.api.bean.CheckGroupExitResult;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

class GroupCheckDaoImpl extends AdhocHttpDao implements IGroupCheckDao {

    private static final String TAG = "GroupSearchDaoImpl";

    public GroupCheckDaoImpl(String pBaseUrl) {
        super(pBaseUrl);
    }


    @Override
    public CheckGroupExitResult checkGroupExit(@NonNull String pGroupcode) throws AdhocException {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("groupcode", pGroupcode);

//            StringBuilder sb = new StringBuilder("/v2/group/exist?groupcode=").append(groupcode);
            return getAction().get("/v2/group/exist", CheckGroupExitResult.class, params, null);
        } catch (Exception e) {
            Logger.e(TAG, "checkGroupExit error: " + e.getMessage());
            throw AdhocException.newException(e);
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
    @Override
    public CheckGroupExitResult checkSchoolExit(@NonNull String groupcode, @NonNull String pLat, @NonNull String pLgn, int maptype, int pScope) throws AdhocException {
        try {
            JSONObject postContent = new JSONObject();
            postContent.put("groupcode", groupcode);

            JSONObject geoinfo = new JSONObject();
            geoinfo.put("lat", pLat);
            geoinfo.put("lgn", pLgn);
            geoinfo.put("maptype", maptype);
            geoinfo.put("scope", pScope);

            postContent.put("geoinfo", geoinfo);

            return postAction().post("/v2/group/exist", CheckGroupExitResult.class, postContent.toString(), null);

        } catch (Exception e) {
            Logger.e(TAG, "checkSchoolExit error: " + e.getMessage());
            throw AdhocException.newException(e);
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
    @Override
    public CheckGroupExitResult checkSchoolExit(@NonNull String groupcode, @NonNull String pIp) throws AdhocException {
        try {
            JSONObject postContent = new JSONObject();
            postContent.put("groupcode", groupcode);
            postContent.put("ip", pIp);

            return postAction().post("/v2/group/exist", CheckGroupExitResult.class, postContent.toString(), null);

        } catch (Exception e) {
            Logger.e(TAG, "checkSchoolExit error: " + e.getMessage());
            throw AdhocException.newException(e);
        }
    }

}
