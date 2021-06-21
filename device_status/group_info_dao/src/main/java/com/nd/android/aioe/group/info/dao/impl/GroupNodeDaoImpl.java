package com.nd.android.aioe.group.info.dao.impl;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.common.util.AdhocDataCheckUtils;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.net.dao.AdhocHttpDao;
import com.nd.android.aioe.group.info.dao.api.IGroupNodeDao;
import com.nd.android.aioe.group.info.dao.api.bean.GroupNodeInfo;
import com.nd.android.aioe.group.info.dao.api.bean.GroupRequestResult;
import com.nd.android.aioe.group.info.dao.api.bean.GroupPageNodeInfo;
import com.nd.android.aioe.group.info.dao.api.bean.GroupPageResult;
import com.nd.android.aioe.group.info.dao.util.GroupDataConvertUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class GroupNodeDaoImpl extends AdhocHttpDao implements IGroupNodeDao {
    private final static int PAGE_SIZE = 200;

    private static final String TAG = "GroupNodeDaoImpl";

    public GroupNodeDaoImpl(String pBaseUrl) {
        super(pBaseUrl);
    }

    @Override
    public List<GroupNodeInfo> getSubNodes(@NonNull String pGroupId) throws AdhocException {
        try {
            Map<String, String> header = null;
            header = new HashMap<>();
            header.put("Accept", "application/json");

            GroupRequestResult response = getAction().get("/v1/group/subgroup?id=" + pGroupId,
                    GroupRequestResult.class, null, header);
            String array = response.getResult();
            GroupNodeInfo[] nodes = new Gson().fromJson(array, GroupNodeInfo[].class);
            return Arrays.asList(nodes);
        } catch (Exception e) {
            Logger.e(TAG, "getSubNodes error: " + e.getMessage());
            throw AdhocException.newException(e);
        }
    }

    @Override
    public List<GroupNodeInfo> getAllSubNodes(@NonNull String pGroupId) throws AdhocException {

        List<GroupNodeInfo> resultNodes = new ArrayList<>();

        int offset = 0;
        while (true) {
            GroupPageResult response = getSubNodesByPage(pGroupId, offset, PAGE_SIZE);
            if (response == null || AdhocDataCheckUtils.isCollectionEmpty(response.getAaData())) {
                return resultNodes;
            }

            for (GroupPageNodeInfo node : response.getAaData()) {
                GroupNodeInfo data = GroupDataConvertUtil.convertFrom(node);
                resultNodes.add(data);
            }

            offset += response.getAaData().size();

            if (response.getAaData().size() < PAGE_SIZE) {
                return resultNodes;
            }
        }
    }


    /**
     * /v1/group/groupPage?groupCode={groupCode}&groupName={groupName}&offset={offset}&limit={limit}&isson={isson} 获取用户分组（修改）
     * <p>
     * groupCode 树节点编码
     * groupName 模糊查询groupName
     * offset    默认0
     * limit     默认10，最多100
     * isson     默认所有子孙节点（包括本身节点），1=只取当前节点的子节点
     */
    @Override
    public GroupPageResult getSubNodesByPage(@NonNull String pGroupID, int pOffset, int pLimit) throws AdhocException {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("groupCode", pGroupID);
            params.put("offset", pOffset);
            params.put("limit", pLimit);
            params.put("isson", 1);

//            String url = "/v1/group/groupPage?groupCode=" + pGroupID + "&offset=" + pOffset
//                    + "&limit=" + pLimit + "&isson=1";

//            RetrieveOrgNodeResponse response = getAction().get(url, RetrieveOrgNodeResponse.class, null, header);
//            String array = response.getResult();
//            MdmOrgNode[] nodes = new Gson().fromJson(array,MdmOrgNode[].class);
//            return Arrays.asList(nodes);

            return getAction().get("/v1/group/groupPage", GroupPageResult.class, params, null);
        } catch (Exception e) {
            Logger.e(TAG, "getSubNodesByPage error：" + e.getMessage());
            throw AdhocException.newException(e);
        }
    }
}
