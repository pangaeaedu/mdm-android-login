package com.nd.android.adhoc.login.basicService.http;

import com.nd.android.adhoc.login.basicService.data.http.ActivateHttpResult;
import com.nd.android.mdm.biz.env.IMdmEnvModule;
import com.nd.android.mdm.biz.env.MdmEvnFactory;

public class HttpServiceImpl implements IHttpService {
    private String mBaseUrl = "";

    public HttpServiceImpl() {
        IMdmEnvModule module = MdmEvnFactory.getInstance().getCurEnvironment();
        mBaseUrl = module.getUrl();
    }

    @Override
    public void requestPolicy(String pDeviceToken) throws Exception {
        LoginDao dao = new LoginDao(mBaseUrl);
        dao.requestPolicySet(pDeviceToken);
    }

    @Override
    public IBindResult bindDevice(String pDeviceToken, String pPushID, String pSerialNum)
            throws Exception {
        LoginDao dao = new LoginDao(mBaseUrl);
        return dao.bindDevice(pDeviceToken, pPushID, pSerialNum);
    }

    @Override
    public ActivateHttpResult activateUser(String pUserToken, String pDeviceToken) throws Exception {
       LoginDao dao = new LoginDao("http://192.168.254.23:8090");
        ActivateHttpResult result = dao.activateUser(pUserToken, pDeviceToken);
        if(result.result.equalsIgnoreCase("success")){
            return result;
        }

        throw new Exception("activate user failed");
    }

}
