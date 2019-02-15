package com.nd.android.adhoc.login.basicService.http;

import android.text.TextUtils;

import com.nd.android.adhoc.basic.net.exception.AdhocHttpException;
import com.nd.android.adhoc.login.basicService.data.http.ActivateUserResponse;
import com.nd.android.adhoc.login.basicService.data.http.BindPushIDResponse;
import com.nd.android.adhoc.login.basicService.data.http.ConfirmDeviceIDResponse;
import com.nd.android.adhoc.login.basicService.data.http.GetActivateUserResultResponse;
import com.nd.android.adhoc.login.basicService.data.http.GetOldTokenResult;
import com.nd.android.adhoc.login.basicService.data.http.LoginUserResponse;
import com.nd.android.adhoc.login.basicService.data.http.QueryDeviceStatusResponse;
import com.nd.android.adhoc.login.enumConst.ActivateUserType;
import com.nd.android.adhoc.login.exception.LoginUserServerException;
import com.nd.android.adhoc.loginapi.exception.ActivateUserServerException;
import com.nd.android.adhoc.loginapi.exception.ConfirmIDServerException;
import com.nd.android.adhoc.loginapi.exception.QueryDeviceStatusServerException;

import org.json.JSONObject;

public class HttpServiceImpl implements IHttpService {
    public HttpServiceImpl() {
    }

    @Override
    public void requestPolicy(String pDeviceToken, long pTime, JSONObject pData) throws Exception {
        LoginDao dao = new LoginDao(getBaseUrl());
        dao.requestPolicySet(pDeviceToken, pTime, pData);
    }

    @Deprecated
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

    @Deprecated
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
    public BindPushIDResponse bindDeviceIDToPushID(String pDeviceID, String pPushID) throws Exception {
        EnrollLoginDao dao = new EnrollLoginDao(getBaseUrl());
        BindPushIDResponse result = dao.bindDeviceIDToPushID(pDeviceID, pPushID);

        if(!result.isSuccess()){
            throw new Exception("Bind Device Failed");
        }

        return result;
    }

    public ActivateUserResponse activateUser(String pDeviceID, String pSerialNo, ActivateUserType pUserType,
                                             String pLoginToken) throws Exception {
        EnrollLoginDao dao = new EnrollLoginDao(getBaseUrl());
        ActivateUserResponse response = dao.activateUser(pDeviceID, pSerialNo, pUserType,
                pLoginToken);

        if (!response.isSuccess()) {
            throw new ActivateUserServerException("active user failed");
        }

        return response;
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
    public LoginUserResponse login(String pEncryptUserName, String pEncryptPassword) throws Exception {
        try {
            EnrollLoginDao dao = new EnrollLoginDao(getBaseUrl());
            LoginUserResponse result = dao.loginUser(pEncryptUserName, pEncryptPassword);
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
    public GetActivateUserResultResponse queryActivateResult(String pDeviceID)
            throws Exception {
        EnrollLoginDao dao = new EnrollLoginDao(getBaseUrl());
        GetActivateUserResultResponse result = dao.getActivateResult(pDeviceID);

        if (!result.isSuccess()) {
            throw new Exception("query activate result not success");
        }

        return result;
    }

    @Override
    public ConfirmDeviceIDResponse confirmDeviceID(String pBuildSn, String pCpuSn, String pIMEI,
                                                   String pWifiMac, String pBlueToothMac, String pSerialNo,
                                                   String pAndroidID, String pDeviceToken) throws Exception {
        try {
            EnrollLoginDao dao = new EnrollLoginDao(getBaseUrl());
            ConfirmDeviceIDResponse result = dao.confirmDeviceID(pBuildSn, pCpuSn, pIMEI, pWifiMac,
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
    public QueryDeviceStatusResponse getDeviceStatus(String pDeviceID, String pSerialNum) throws Exception {
        try {
            EnrollLoginDao dao = new EnrollLoginDao(getBaseUrl());
            QueryDeviceStatusResponse result = dao.queryDeviceStatus(pDeviceID, pSerialNum);
            if (!result.isSuccess()) {
                throw new QueryDeviceStatusServerException("get device status not success");
            }
            return result;
        } catch (AdhocHttpException e) {
            throw new QueryDeviceStatusServerException(e.getErrorCode(), e.getMessage());
        }
    }

    private String getBaseUrl(){
//        IMdmEnvModule module = MdmEvnFactory.getInstance().getCurEnvironment();
//        return module.getUrl();
        return "http://192.168.254.23:8090";
    }

}
