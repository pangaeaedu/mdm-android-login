package com.nd.android.adhoc.login.basicService.http;

import com.nd.android.adhoc.login.basicService.data.http.ActivateHttpResult;
import com.nd.android.mdm.biz.env.IMdmEnvModule;
import com.nd.android.mdm.biz.env.MdmEvnFactory;

import org.json.JSONObject;

public class HttpServiceImpl implements IHttpService {
    public HttpServiceImpl() {
    }

    @Override
    public void requestPolicy(String pDeviceToken, long pTime, JSONObject pData) throws Exception {
        LoginDao dao = new LoginDao(getBaseUrl());
        dao.requestPolicySet(pDeviceToken, pTime, pData);
    }

    @Override
    public IBindResult bindDevice(String pDeviceToken, String pPushID, String pSerialNum)
            throws Exception {
        LoginDao dao = new LoginDao(getBaseUrl());
        IBindResult result = dao.bindDevice(pDeviceToken, pPushID, pSerialNum);
        if(!result.isSuccess()){
            throw new Exception("Bind Device Failed");
        }

        return result;
    }

    @Override
    public ActivateHttpResult activateUser(String pUCAccessToken, String pDeviceToken) throws Exception {
       LoginDao dao = new LoginDao(getBaseUrl());
        ActivateHttpResult result = dao.activateUser(pUCAccessToken, pDeviceToken);
        if(result.result.equalsIgnoreCase("success")){
            return result;
        }

        throw new Exception("activate user failed");
    }

    private String getBaseUrl(){
        IMdmEnvModule module = MdmEvnFactory.getInstance().getCurEnvironment();
        return module.getUrl();
    }

}
