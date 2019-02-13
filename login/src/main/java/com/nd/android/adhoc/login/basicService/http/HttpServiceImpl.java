package com.nd.android.adhoc.login.basicService.http;

import android.text.TextUtils;

import com.nd.android.adhoc.basic.net.exception.AdhocHttpException;
import com.nd.android.adhoc.login.basicService.data.http.ActivateHttpResult;
import com.nd.android.adhoc.login.basicService.data.http.GetDeviceStatusResult;
import com.nd.android.adhoc.login.basicService.data.http.GetOldTokenResult;
import com.nd.android.adhoc.login.basicService.data.http.GetTokenResult;
import com.nd.android.adhoc.login.basicService.data.http.LoginUserResult;
import com.nd.android.adhoc.login.exception.LoginUserServerException;
import com.nd.android.adhoc.loginapi.exception.ConfirmIDServerException;
import com.nd.android.adhoc.loginapi.exception.QueryDeviceStatusServerException;
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
    public IBindResult bindDevice(String pDeviceToken, String pPushID, String
            pSerialNum)
            throws Exception {
        LoginDao dao = new LoginDao(getBaseUrl());
        IBindResult result = dao.bindDevice(pDeviceToken, pPushID, pSerialNum);
        if(!result.isSuccess()){
            throw new Exception("Bind Device Failed");
        }

        return result;
    }

    @Override
    public IBindResult bindDeviceWithChannelType(String pDeviceToken, String pPushID,
                                                 String pSerialNum, int pPushChannelType) throws Exception {
        LoginDao dao = new LoginDao(getBaseUrl());
        IBindResult result = dao.bindDeviceWithPushChannelType(pDeviceToken, pPushID,
                pSerialNum, pPushChannelType);
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

    @Override
    public GetOldTokenResult getOldDeviceToken(String pBuildSn, String pCpuSn, String pIMEI, String pWifiMac,
                                    String pBlueToothMac, String pSerialNo, String pDeviceToken)
            throws Exception {
        LoginDao dao = new LoginDao(getBaseUrl());
        GetOldTokenResult result = dao.getOldToken(pBuildSn, pCpuSn, pIMEI, pWifiMac,
                pBlueToothMac, pSerialNo, pDeviceToken);

        return result;

    }

    @Override
    public LoginUserResult login(String pEncryptUserName, String pEncryptPassword) throws Exception {
        try {
            LoginDao dao = new LoginDao(getBaseUrl());
            LoginUserResult result = dao.loginUser(pEncryptUserName, pEncryptPassword);
            if(!result.isSuccess()){
                throw new LoginUserServerException(result.result);
            }

            if(TextUtils.isEmpty(result.loginToken)){
                throw new LoginUserServerException("login token is empty");
            }

            return result;
        }catch (AdhocHttpException e){
            throw new LoginUserServerException(e.getErrorCode(), e.getMessage());
        }

    }

    @Override
    public IQueryActivateResult queryActivateResult(String pDeviceID) throws Exception {
        return null;
    }

    @Override
    public GetTokenResult confirmDeviceID(String pBuildSn, String pCpuSn, String pIMEI,
                                          String pWifiMac, String pBlueToothMac, String pSerialNo,
                                          String pAndroidID, String pDeviceToken) throws Exception {
        try {
            LoginDao dao = new LoginDao(getBaseUrl());
            GetTokenResult result = dao.confirmDeviceID(pBuildSn, pCpuSn, pIMEI, pWifiMac,
                    pBlueToothMac, pSerialNo, pAndroidID, pDeviceToken);

            if(!result.isSuccess()){
                throw new ConfirmIDServerException("confirm id not success");
            }

            return result;
        }catch (AdhocHttpException e){
            throw new ConfirmIDServerException(e.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public GetDeviceStatusResult getDeviceStatus(String pDeviceID, String pSerialNum) throws Exception {
        try {
            LoginDao dao = new LoginDao(getBaseUrl());
            GetDeviceStatusResult result = dao.getDeviceStatus(pDeviceID, pSerialNum);
            if (!result.isSuccess()) {
                throw new QueryDeviceStatusServerException("get device status not success");
            }
            return result;
        } catch (AdhocHttpException e) {
            throw new QueryDeviceStatusServerException(e.getErrorCode(), e.getMessage());
        }
    }

    private String getBaseUrl(){
        IMdmEnvModule module = MdmEvnFactory.getInstance().getCurEnvironment();
        return module.getUrl();
    }

}
