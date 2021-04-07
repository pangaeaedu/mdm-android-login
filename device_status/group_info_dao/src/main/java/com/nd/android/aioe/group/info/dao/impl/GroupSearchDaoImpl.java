package com.nd.android.aioe.group.info.dao.impl;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.net.dao.AdhocHttpDao;
import com.nd.android.aioe.group.info.dao.api.IGroupSearchDao;
import com.nd.android.aioe.group.info.dao.api.bean.CheckGroupExitResult;
import com.nd.android.aioe.group.info.dao.api.bean.GroupPathsResult;
import com.nd.android.aioe.group.info.dao.api.bean.GroupRequestResult;
import com.nd.android.aioe.group.info.dao.api.bean.SearchGroupPathResult;
import com.nd.android.aioe.group.info.dao.api.bean.SearchSubGroupNodeResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupSearchDaoImpl extends AdhocHttpDao implements IGroupSearchDao {

    private static final String TAG = "GroupSearchDaoImpl";

    public GroupSearchDaoImpl(String pBaseUrl) {
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

    //通过GroupCode查找学校
    public CheckGroupExitResult checkGroupExit(String pGroupcode) throws AdhocException {
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
}
