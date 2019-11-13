package com.nd.android.adhoc.login.basicService.http;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nd.android.adhoc.basic.net.dao.AdhocHttpDao;
import com.nd.android.adhoc.login.basicService.data.http.GroupPageNode;
import com.nd.android.adhoc.login.basicService.data.http.GroupPageResponse;
import com.nd.android.adhoc.login.basicService.data.http.MdmOrgNode;
import com.nd.android.adhoc.login.basicService.data.http.ResponseDataConvertUtils;
import com.nd.android.adhoc.login.basicService.data.http.SearchSchoolNode;
import com.nd.android.adhoc.login.basicService.data.http.SearchSchoolNodeResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchoolGroupCodeDao extends AdhocHttpDao {
    private final static int PageSize = 200;

    public SchoolGroupCodeDao(String pBaseUrl) {
        super(pBaseUrl);
    }

    public List<MdmOrgNode> getSubNodes(String pGroupID) throws Exception{
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
            Log.e("yhq", "SchoolGroupCodeDao error happpen:"+ postAction().getBaseUrl()
                    +"/v1/group/groupPage?groupCode"+" " + "Msg:"+pE.getMessage());
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
            Log.e("yhq", "SchoolGroupCodeDao error happpen:"+ postAction().getBaseUrl()
                    +"/v1/group/grouppath?schoolid="+" " + "Msg:"+pE.getMessage());
            throw pE;
        }
    }
}
