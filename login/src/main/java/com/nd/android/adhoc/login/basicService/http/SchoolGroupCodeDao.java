package com.nd.android.adhoc.login.basicService.http;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceHelper;
import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.net.dao.AdhocHttpDao;
import com.nd.android.adhoc.login.basicService.data.http.DeviceGroupPath;
import com.nd.android.adhoc.login.basicService.data.http.GroupPageNode;
import com.nd.android.adhoc.login.basicService.data.http.GroupPageResponse;
import com.nd.android.adhoc.login.basicService.data.http.MdmOrgNode;
import com.nd.android.adhoc.login.basicService.data.http.ResponseDataConvertUtils;
import com.nd.android.adhoc.login.basicService.data.http.RetrieveOrgNodeResponse;
import com.nd.android.adhoc.login.basicService.data.http.SearchSchoolNode;
import com.nd.android.adhoc.login.basicService.data.http.SearchSchoolNodeByGroupCode;
import com.nd.android.adhoc.login.basicService.data.http.SearchSchoolNodeResponse;
import com.nd.android.adhoc.login.basicService.data.http.SearchSubSchoolNodeResult;
import com.nd.android.mdm.biz.env.MdmEvnFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchoolGroupCodeDao extends AdhocHttpDao {
    private final static int PageSize = 200;

    public SchoolGroupCodeDao(String pBaseUrl) {
        super(pBaseUrl);
    }

    public List<MdmOrgNode> getSubNodes(String pGroupID) throws Exception{
        try {
            Map<String, String> header = null;
            header = new HashMap<>();
            header.put("Accept", "application/json");

            RetrieveOrgNodeResponse response = getAction().get("/v1/group/subgroup?id="+pGroupID,
                    RetrieveOrgNodeResponse.class, null, header);
            String array = response.getResult();
            MdmOrgNode[] nodes = new Gson().fromJson(array,MdmOrgNode[].class);
            return Arrays.asList(nodes);
        }catch (Exception pE){
            Logger.e("yhq", "EnrollLoginDao error happpen getSubNodes:"+pE.getMessage());
            throw pE;
        }
    }

    public List<MdmOrgNode> getSubNodesWithPageableInterface(String pGroupID) throws Exception{
        List<MdmOrgNode> resultNodes = new ArrayList<>();

        int offset = 0;
        while (true){
            GroupPageResponse response = getSubNodesByPage(pGroupID, offset, PageSize);
            if(response == null || response.getAaData() == null || response.getAaData().isEmpty()){
                return resultNodes;
            }

            for (GroupPageNode node : response.getAaData()) {
                MdmOrgNode data = ResponseDataConvertUtils.convertFrom(node);
                resultNodes.add(data);
            }

            offset+= response.getAaData().size();

            if(response.getAaData().size() < PageSize){
                return resultNodes;
            }
        }
    }

    public GroupPageResponse getSubNodesByPage(String pGroupID, int pOffset, int pLimit) throws Exception{
        try {
            Map<String, String> header = null;
            header = new HashMap<>();
            header.put("Accept", "application/json");

            String url = "/v1/group/groupPage?groupCode="+pGroupID+"&offset="+pOffset
                    +"&limit="+pLimit+"&isson=1";

//            RetrieveOrgNodeResponse response = getAction().get(url, RetrieveOrgNodeResponse.class, null, header);
//            String array = response.getResult();
//            MdmOrgNode[] nodes = new Gson().fromJson(array,MdmOrgNode[].class);
//            return Arrays.asList(nodes);

            GroupPageResponse response = getAction().get(url, GroupPageResponse.class, null,
                    header);
            return response;
        }catch (Exception pE){
            Logger.e("yhq", "SchoolGroupCodeDao error happpen getSubNodesByPage:"+pE.getMessage());
            throw pE;
        }
    }

    /**
     * 获取groupcode下的学校
     * @param strGroupCode 获取groupcode下的学校
     * @param args  可选参数：
     *              offset    分页偏移量，默认0
                    limit     每页的数量，默认1000，最大1000（受限push消息最大200k
     * @return
     * @throws Exception
     */
    public SearchSubSchoolNodeResult getSubNodesByGroupCode(String strGroupCode, int ...args) throws Exception{
        if(TextUtils.isEmpty(strGroupCode)){
            throw new AdhocException("no strGroupCode");
        }
        try {
            Map<String, String> header = null;
            header = new HashMap<>();
            header.put("Accept", "application/json");

            StringBuilder sb = new StringBuilder("/v2/group/school?groupcode=").append(strGroupCode);
            if(null != args && args.length > 0){
                sb.append("&offset=").append(args[0]);

                if(args.length > 1){
                    sb.append("&limit=").append(args[1]);
                }
            }

            SearchSubSchoolNodeResult response = getAction().get(sb.toString(),
                    SearchSubSchoolNodeResult.class, null, header);
            return response;
        }catch (Exception pE){
            Logger.e("lsj", "SchoolGroupCodeDao error happpen getSubNodesByGroupCode:"+pE.getMessage());
            throw pE;
        }
    }

    //根据会上沟通的结果，根据学校ID来搜索这个功能，不做分页，这个PageSize是给前端指定一次要拉多少纪录的
    public List<SearchSchoolNode> searchSchoolID(String pSchoolID, int pPageSize) throws Exception{
        try {
            Map<String, String> header = null;
            header = new HashMap<>();
            header.put("Accept", "application/json");

            String path = "/v1/group/grouppath?schoolid="+pSchoolID+"&top="+pPageSize;
            SearchSchoolNodeResponse response = getAction().get(path,
                    SearchSchoolNodeResponse.class, null, header);
            String array = response.getResult();

//            array = array.substring(1,array.length()-1);
//            JSONArray jsonArray= JSONArray.parseArray(array);
//            for(Object item:jsonArray){
//                JSONObject jsonObject=(JSONObject)item;
//                SearchSchoolNode x= JSONObject.parseObject(jsonObject.toString(), SearchSchoolNode.class);
//            }
            List<SearchSchoolNode> nodes = new Gson().fromJson(array, new TypeToken<List<SearchSchoolNode>>(){}
            .getType());
            return nodes;
        }catch (Exception pE){
            Logger.e("yhq", "SchoolGroupCodeDao error happpen searchSchoolID:"+pE.getMessage());
            throw pE;
        }
    }

    //通过GroupCode查找学校
    public SearchSchoolNodeByGroupCode searchByGroupCode(String groupcode) throws Exception{
        try {
            Map<String, String> header = null;
            header = new HashMap<>();
            header.put("Accept", "application/json");

            StringBuilder sb = new StringBuilder("/v2/group/exist?groupcode=").append(groupcode);
            SearchSchoolNodeByGroupCode result = getAction().get(sb.toString(),
                    SearchSchoolNodeByGroupCode.class, null, header);
            return result;
        }catch (Exception pE){
            Logger.e("lsj", "SchoolGroupCodeDao error happpen searchByGroupCode:"+pE.getMessage());
            throw pE;
        }
    }

    public DeviceGroupPath getDeviceGroupPaths() throws Exception{
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("devicetoken", DeviceHelper.getDeviceToken());

            // 从服务端取一次最新的
            return new AdhocHttpDao(MdmEvnFactory.getInstance().getCurEnvironment().getUrl())
                    .getAction().get("/v2/device/grouppath", DeviceGroupPath.class, params);
        }catch (Exception pE){
            Logger.e("yhq", "getDeviceGroupPaths error happpen getDeviceGroupPaths:"+pE.getMessage());
            throw pE;
        }
    }
}
