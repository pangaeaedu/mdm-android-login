package com.nd.android.adhoc.login.basicService.http;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nd.android.adhoc.basic.net.dao.AdhocHttpDao;
import com.nd.android.adhoc.login.basicService.data.http.MdmOrgNode;
import com.nd.android.adhoc.login.basicService.data.http.RetrieveOrgNodeResponse;
import com.nd.android.adhoc.login.basicService.data.http.SearchSchoolNode;
import com.nd.android.adhoc.login.basicService.data.http.SearchSchoolNodeResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchoolGroupCodeDao extends AdhocHttpDao {
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
            Log.e("yhq", "EnrollLoginDao error happpen:"+ postAction().getBaseUrl()
                    +"/v1.1/enroll/getUserInfo/"+" " + "Msg:"+pE.getMessage());
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
            Log.e("yhq", "EnrollLoginDao error happpen:"+ postAction().getBaseUrl()
                    +"/v1.1/enroll/getUserInfo/"+" " + "Msg:"+pE.getMessage());
            throw pE;
        }
    }
}
