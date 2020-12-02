package com.nd.android.adhoc.login.basicService.http;

import android.text.TextUtils;
import android.util.Log;

import com.nd.android.adhoc.basic.net.dao.AdhocHttpDao;
import com.nd.android.adhoc.login.basicService.data.http.EnrollUserInfo;
import com.nd.android.adhoc.login.basicService.data.http.EnrollUserInfoResult;

import java.util.HashMap;
import java.util.Map;

public class SetAssetCodeDao extends AdhocHttpDao {

    public SetAssetCodeDao(String pBaseUrl) {
        super(pBaseUrl);
    }

    public EnrollUserInfoResult setAssetCode(String strDeviceToken, String strAssetCode) throws Exception{
        if(TextUtils.isEmpty(strDeviceToken) || TextUtils.isEmpty(strAssetCode)){
            return null;
        }
        try {
            Map<String, String> header = null;
            header = new HashMap<>();
            header.put("Accept", "application/json");

            String url = "/v1.1/enroll/userInfo";

            EnrollUserInfoResult response = postAction().post(url, EnrollUserInfoResult.class, new EnrollUserInfo(strDeviceToken, 1, strAssetCode),
                    header);
            return response;
        }catch (Exception pE){
            Log.e("lsj", "SchoolGroupCodeDao error happpen setAssetCode:"+pE.getMessage());
            throw pE;
        }
    }
}
