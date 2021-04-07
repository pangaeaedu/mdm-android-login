package com.nd.android.adhoc.login.basicService.http;

import android.text.TextUtils;

import com.nd.android.adhoc.login.basicService.data.http.ActivateUserResponse;
import com.nd.android.adhoc.login.basicService.data.http.BindPushIDResponse;
import com.nd.android.adhoc.login.basicService.data.http.ConfirmDeviceIDResponse;
import com.nd.android.adhoc.login.basicService.data.http.EnrollUserInfoResult;
import com.nd.android.adhoc.login.basicService.data.http.GetActivateUserResultResponse;
import com.nd.android.adhoc.login.basicService.data.http.GetOldTokenResult;
import com.nd.android.adhoc.login.basicService.data.http.GetUserInfoResponse;
import com.nd.android.adhoc.login.basicService.data.http.LoginUserResponse;
import com.nd.android.adhoc.login.basicService.data.http.QueryDeviceStatusResponse;
import com.nd.android.adhoc.login.enumConst.ActivateUserType;
import com.nd.android.adhoc.login.exception.LoginUserServerException;
import com.nd.android.adhoc.loginapi.exception.ActivateUserServerException;
import com.nd.android.adhoc.loginapi.exception.ConfirmIDServerException;
import com.nd.android.adhoc.loginapi.exception.DeviceTokenNotFoundException;
import com.nd.android.adhoc.loginapi.exception.QueryDeviceStatusServerException;
import com.nd.android.mdm.biz.env.IMdmEnvModule;
import com.nd.android.mdm.biz.env.MdmEvnFactory;

import java.util.Map;

public class HttpServiceImpl {//implements IHttpService {
//    public HttpServiceImpl() {
//    }
//
//
//    @Override
//    public BindPushIDResponse bindDeviceIDToPushID(String pDeviceID, String pPushID) throws Exception {
//        EnrollLoginDao dao = new EnrollLoginDao(getBaseUrl());
//        BindPushIDResponse result = dao.bindDeviceIDToPushID(pDeviceID, pPushID);
//
//        if (!result.isSuccess()) {
//            if (result.isDeviceTokenNotFound()) {
//                throw new DeviceTokenNotFoundException();
//            }
//
//            throw new Exception("Bind Device Failed");
//        }
//
//        return result;
//    }
//
////    public ActivateUserResponse activateUser(String pDeviceID, String pSerialNo, ActivateUserType pUserType,
////                                             String pLoginToken) throws Exception {
////        EnrollLoginDao dao = new EnrollLoginDao(getBaseUrl());
////        ActivateUserResponse response = dao.activateUser(pDeviceID, pSerialNo, pUserType,
////                pLoginToken);
////
////        if (!response.isSuccess()) {
////            throw new ActivateUserServerException("active user failed");
////        }
////
////        return response;
////    }
//
//    public ActivateUserResponse activateUser(String pDeviceID, String pSerialNo, String pDeviceSerialNo, ActivateUserType pUserType,
//                                             String pLoginToken,String pOrgId) throws Exception {
//        EnrollLoginDao dao = new EnrollLoginDao(getBaseUrl());
//        ActivateUserResponse response = dao.activateUser(pDeviceID, pSerialNo, pDeviceSerialNo, pUserType,
//                pLoginToken,pOrgId);
//
//        if (!response.isSuccess()) {
//            throw new ActivateUserServerException("active user failed");
//        }
//
//        return response;
//    }
//
//    public ActivateUserResponse activateUser(String pDeviceID, String pSerialNo,
//                                             String pDeviceSerialNo,
//                                             String pSchoolGroupCode,
//                                             ActivateUserType pUserType,
//                                             String pLoginToken, int pRealType,String pOrgId) throws Exception {
//        EnrollLoginDao dao = new EnrollLoginDao(getBaseUrl());
//        ActivateUserResponse response = dao.activateUser(pDeviceID, pSerialNo,
//                pSchoolGroupCode,
//                pDeviceSerialNo,
//                pUserType,
//                pLoginToken, pRealType,pOrgId);
//
//        return response;
//    }
//
//
////    @Override
////    public GetOldTokenResult getOldDeviceToken(String pBuildSn, String pCpuSn, String pIMEI, String pWifiMac,
////                                               String pBlueToothMac, String pSerialNo, String pDeviceToken)
////            throws Exception {
////        LoginDao dao = new LoginDao(getBaseUrl());
////        GetOldTokenResult result = dao.getOldToken(pBuildSn, pCpuSn, pIMEI, pWifiMac,
////                pBlueToothMac, pSerialNo, pDeviceToken);
////
////        return result;
////
////    }
//
//    @Override
//    public LoginUserResponse login(String pEncryptUserName, String pEncryptPassword) throws Exception {
//        EnrollLoginDao dao = new EnrollLoginDao(getBaseUrl());
//        LoginUserResponse result = dao.loginUser(pEncryptUserName, pEncryptPassword);
//        if (!result.isSuccess()) {
//            throw new LoginUserServerException(result.result);
//        }
//
//        if (TextUtils.isEmpty(result.loginToken)) {
//            throw new LoginUserServerException("login token is empty");
//        }
//
//        return result;
//
//    }
//
//    @Override
//    public GetActivateUserResultResponse queryActivateResult(String pDeviceID, String pRequestID)
//            throws Exception {
//        EnrollLoginDao dao = new EnrollLoginDao(getBaseUrl());
//        GetActivateUserResultResponse result = dao.getActivateResult(pDeviceID, pRequestID);
//
//        return result;
//    }
//
////    @Override
////    public ConfirmDeviceIDResponse confirmDeviceID(String pBuildSn, String pCpuSn, String pIMEI,
////                                                   String pWifiMac, String pLanMac,
////                                                   String pBlueToothMac, String pSerialNo,
////                                                   String pAndroidID, String pDeviceToken) throws Exception {
////        EnrollLoginDao dao = new EnrollLoginDao(getBaseUrl());
////        ConfirmDeviceIDResponse result = dao.confirmDeviceID(pBuildSn, pCpuSn, pIMEI, pWifiMac,
////                pLanMac, pBlueToothMac, pSerialNo, pAndroidID, pDeviceToken);
////
////        if (!result.isSuccess()) {
////            throw new ConfirmIDServerException("confirm id not success");
////        }
////
////        return result;
////    }
//
//    @Override
//    public ConfirmDeviceIDResponse confirmDeviceID(Map<String, Object> pHardwareMap,
//                                                   String pDeviceID) throws Exception {
//        EnrollLoginDao dao = new EnrollLoginDao(getBaseUrl());
//        ConfirmDeviceIDResponse result = dao.confirmDeviceID(pHardwareMap, pDeviceID);
//
//        if (!result.isSuccess()) {
//            throw new ConfirmIDServerException("confirm id not success");
//        }
//
//        return result;
//    }
//
//    @Override
//    public QueryDeviceStatusResponse getDeviceStatus(String pDeviceID, String pSerialNum) throws Exception {
//        return getDeviceStatus(pDeviceID, pSerialNum, 0);
//    }
//
//    @Override
//    public QueryDeviceStatusResponse getDeviceStatus(String pDeviceID, String pSerialNum,
//                                                     int pAutoLogin) throws Exception {
//        EnrollLoginDao dao = new EnrollLoginDao(getBaseUrl());
//        QueryDeviceStatusResponse result = dao.queryDeviceStatus(pDeviceID, pSerialNum, pAutoLogin);
//        if (!result.isSuccess()) {
//            throw new QueryDeviceStatusServerException("get device status not success");
//        }
//
//        return result;
//    }
//
//    @Override
//    public QueryDeviceStatusResponse getDeviceStatus(String pDeviceID, String pSerialNum,
//                                                     int pAutoLogin, int pNeedGroup) throws
//            Exception {
//        EnrollLoginDao dao = new EnrollLoginDao(getBaseUrl());
//        QueryDeviceStatusResponse result = dao.queryDeviceStatus(pDeviceID, pSerialNum,
//                pAutoLogin, pNeedGroup);
//        if (!result.isSuccess()) {
//            throw new QueryDeviceStatusServerException("get device status not success");
//        }
//
//        return result;
//    }
//
//
//    @Override
//    public GetUserInfoResponse getUserInfo(String pDeviceID) throws Exception {
//        EnrollLoginDao dao = new EnrollLoginDao(getBaseUrl());
//        return dao.getUserInfo(pDeviceID);
//    }
//
//    @Override
//    public Boolean reportHardwareInfo(String pDeviceID, Map<String, Object> pInfo)
//            throws Exception {
//        EnrollLoginDao dao = new EnrollLoginDao(getBaseUrl());
//        return dao.reportHardwareInfo(pDeviceID, pInfo);
//    }
//
////    @Override
////    public EnrollUserInfoResult setAssetCode(String strDeviceToken, String strAssetCode) throws Exception {
////        return new SetAssetCodeDao(getBaseUrl()).setAssetCode(strDeviceToken, strAssetCode);
////    }
//
//    private String getBaseUrl() {
//        IMdmEnvModule module = MdmEvnFactory.getInstance().getCurEnvironment();
//        return module.getUrl();
//    }

}
