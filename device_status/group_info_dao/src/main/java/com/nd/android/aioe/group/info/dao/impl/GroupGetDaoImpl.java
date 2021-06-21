package com.nd.android.aioe.group.info.dao.impl;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.net.dao.AdhocHttpDao;
import com.nd.android.aioe.group.info.dao.api.IGroupGetDao;
import com.nd.android.aioe.group.info.dao.api.bean.GroupPathsResult;
import com.nd.android.aioe.group.info.dao.api.bean.GroupRequestResult;
import com.nd.android.aioe.group.info.dao.api.bean.IpLocationGroupResult;
import com.nd.android.aioe.group.info.dao.api.bean.SearchGroupPathResult;
import com.nd.android.aioe.group.info.dao.api.bean.SearchSubGroupNodeResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupGetDaoImpl extends AdhocHttpDao implements IGroupGetDao {

    private static final String TAG = "GroupSearchDaoImpl";

    public GroupGetDaoImpl(String pBaseUrl) {
        super(pBaseUrl);
    }

    @Override
    public SearchSubGroupNodeResult searchSubGroupNodes(@NonNull String pGroupCode, int pOffset, int pLimit) throws AdhocException {

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("groupcode", pGroupCode);
            params.put("offset", pOffset);
            params.put("limit", pLimit);

            return getAction().get("/v2/group/school", SearchSubGroupNodeResult.class, params, null);
        } catch (Exception e) {
            Logger.e(TAG, "searchSubGroupNodes error: " + e.getMessage());
            throw AdhocException.newException(e);
        }
    }

    @Override
    public List<SearchGroupPathResult> searchGroupPath(@NonNull String pGroupId, int pPageSize) throws AdhocException {
        try {

            Map<String, Object> params = new HashMap<>();
            params.put("schoolid", pGroupId);
            params.put("top", pPageSize);

//            String path = "/v1/group/grouppath?schoolid="+pSchoolID+"&top="+pPageSize;
            GroupRequestResult response = getAction().get("/v1/group/grouppath",
                    GroupRequestResult.class, params, null);

            String array = response.getResult();

            return new Gson().fromJson(array, new TypeToken<List<SearchGroupPathResult>>() {
            }.getType());
        } catch (Exception e) {
            Logger.e(TAG, "searchGroupPath error: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public GroupPathsResult getAllGroupPaths(@NonNull String pDeviceId) throws AdhocException {

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("devicetoken", pDeviceId);

            // 从服务端取一次最新的
            return getAction().get("/v2/device/grouppath", GroupPathsResult.class, params);
        } catch (Exception e) {
            Logger.e(TAG, "getAllGroupPaths error: " + e.getMessage());
            throw AdhocException.newException(e);
        }
    }


    @Override
    public IpLocationGroupResult getSchoolCodeByIp(@NonNull String pRootCode, @NonNull String pIp) throws AdhocException {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("ip", pIp);
            map.put("groupcode", pRootCode);

            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);
            return postAction().post("/v2/group/ipinfo/", IpLocationGroupResult.class,
                    content, null);
        } catch (Exception e) {
            Logger.e(TAG, "getSchoolCodeByIp error: " + e.getMessage());
            throw AdhocException.newException(e);
        }
    }

    @Override
    public IpLocationGroupResult getSchoolCodeByLocation(@NonNull String pRootCode, @NonNull String pLat, @NonNull String pLgn, int pMapType,
                                                         int pScope) throws AdhocException {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("lat", pLat);
            map.put("lgn", pLgn);
            map.put("scope", pScope);
            map.put("maptype", pMapType);

            map.put("groupcode", pRootCode);

            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);
            return postAction().post("/v2/group/geoinfo/", IpLocationGroupResult.class,
                    content, null);
        } catch (Exception e) {
            Logger.e(TAG, "getSchoolCodeByLocation error: " + e.getMessage());
            throw AdhocException.newException(e);
        }
    }

}
