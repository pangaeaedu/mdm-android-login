package com.nd.android.adhoc.login.basicService.http;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.net.dao.AdhocHttpDao;

import java.util.HashMap;
import java.util.Map;

public class UserTypeDao extends AdhocHttpDao {
    public UserTypeDao(String pBaseUrl) {
        super(pBaseUrl);
    }


    public String getUserType(String pUsername, String pEncryptPassword) throws Exception{
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("username", pUsername);
            map.put("passwd", pEncryptPassword);

            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);

            Map<String, Object> response = postAction().post("/v1.1/enroll/usertype/", Map.class,
                    content, null);
            String type = (String) response.get("user_type");
            return type;
        }catch (Exception pE){
            Logger.e("yhq", "EnrollLoginDao error happpen getUserType:"+pE.getMessage());
            throw pE;
        }
    }
}
