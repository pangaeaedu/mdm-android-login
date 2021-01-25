package com.nd.android.adhoc.login.basicService.http;

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

import org.json.JSONObject;

import java.util.Map;

public interface IHttpService {

    void requestPolicy(String pDeviceToken, long pTime, JSONObject pData) throws Exception;

    @Deprecated
    IBindResult bindDevice( String pDeviceToken, String pPushID,
                           String pSerialNum) throws Exception;

    @Deprecated
    IBindResult bindDeviceWithChannelType( String pDeviceToken, String pPushID,
                            String pSerialNum, int pPushChannelType) throws Exception;

    BindPushIDResponse bindDeviceIDToPushID(String pDeviceID, String pPushID) throws Exception;

//    ActivateUserResponse activateUser(String pDeviceID, String pSerialNo,
//                                      ActivateUserType pUserType, String pLoginToken) throws Exception;

    ActivateUserResponse activateUser(String pDeviceID, String pSerialNo, String pDeviceSerialNo, ActivateUserType pUserType,
                                      String pLoginToken,String pOrgId) throws Exception;

    ActivateUserResponse activateUser(String pDeviceID, String pSerialNo, String pDeviceSerialNo , String pSchoolGroupCode,
                                      ActivateUserType pUserType, String pLoginToken,
                                      int pRealType,String pOrgId) throws Exception;

    GetOldTokenResult getOldDeviceToken(String  pBuildSn, String pCpuSn, String pIMEI, String pWifiMac,
                                        String pBlueToothMac, String pSerialNo, String pDeviceToken) throws Exception;

    LoginUserResponse login(String pEncryptUserName, String pEncryptPassword) throws Exception;

    GetActivateUserResultResponse queryActivateResult(String pDeviceID, String pRequestID) throws
            Exception;

//    @Deprecated
//    ConfirmDeviceIDResponse confirmDeviceID(String  pBuildSn, String pCpuSn, String pIMEI, String pWifiMac,
//                                            String pLanMac, String pBlueToothMac, String
//                                                    pSerialNo, String pAndroidID,
//                                            String pDeviceToken) throws Exception;

    ConfirmDeviceIDResponse confirmDeviceID(Map<String, Object> pHardwareMap, String pDeviceID)
            throws Exception;

    QueryDeviceStatusResponse getDeviceStatus(String pDeviceID, String pSerialNum) throws Exception;

    QueryDeviceStatusResponse getDeviceStatus(String pDeviceID, String pSerialNum, int pAutoLogin)
            throws Exception;

    QueryDeviceStatusResponse getDeviceStatus(String pDeviceID, String pSerialNum, int pAutoLogin,
                                              int pNeedGroup)
            throws Exception;

    GetUserInfoResponse getUserInfo(String pDeviceID) throws Exception;

    Boolean reportHardwareInfo(String pDeviceID, Map<String, Object> pInfo) throws Exception;

    EnrollUserInfoResult setAssetCode(String strDeviceToken, String strAssetCode) throws Exception;
}
