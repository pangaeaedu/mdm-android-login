package com.nd.android.mdm.runinfo.sdk.http.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nd.android.adhoc.basic.net.constant.AhdocHttpConstants;
import com.nd.android.adhoc.basic.net.dao.AdhocHttpDao;
import com.nd.android.adhoc.basic.net.exception.AdhocHttpException;
import com.nd.android.mdm.biz.env.MdmEvnFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by HuangYK on 2018/12/7.
 */

public class AppRunInfoDao extends AdhocHttpDao {

    public AppRunInfoDao() {
        super(MdmEvnFactory.getInstance().getCurEnvironment().getUrl());
//        MdmEvnFactory.getInstance().getCurEnvironment().getUrl() + "/v1/device/cmdresult/"
    }


    public String postAppRunInfo(String pInfoJson, String pDeviceToken) throws AdhocHttpException {


        Map<String, Object> map = new HashMap<>();
        map.put("info", pInfoJson);
        map.put("deviceToken", pDeviceToken);

        try {
            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);

            return postAction().post("/v1/report/app/openinfo", String.class, content, null);
        } catch (RuntimeException e) {
            throw new AdhocHttpException(e.getMessage(), AhdocHttpConstants.ADHOC_HTTP_ERROR);
        }

    }
}
