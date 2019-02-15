package com.nd.android.adhoc.login.basicService.http;

import com.nd.android.adhoc.login.basicService.data.http.ActivateUserResponse;
import com.nd.android.adhoc.login.basicService.data.http.BindPushIDResponse;
import com.nd.android.adhoc.login.basicService.data.http.ConfirmDeviceIDResponse;
import com.nd.android.adhoc.login.basicService.data.http.GetActivateUserResultResponse;
import com.nd.android.adhoc.login.basicService.data.http.GetOldTokenResult;
import com.nd.android.adhoc.login.basicService.data.http.LoginUserResponse;
import com.nd.android.adhoc.login.basicService.data.http.QueryDeviceStatusResponse;
import com.nd.android.adhoc.login.enumConst.ActivateUserType;

import org.json.JSONObject;

public interface IHttpService {

    void requestPolicy(String pDeviceToken, long pTime, JSONObject pData) throws Exception;

    @Deprecated
    IBindResult bindDevice( String pDeviceToken, String pPushID,
                           String pSerialNum) throws Exception;

    @Deprecated
    IBindResult bindDeviceWithChannelType( String pDeviceToken, String pPushID,
                            String pSerialNum, int pPushChannelType) throws Exception;

    BindPushIDResponse bindDeviceIDToPushID(String pDeviceID, String pPushID) throws Exception;

    ActivateUserResponse activateUser(String pDeviceID, String pSerialNo,
                                      ActivateUserType pUserType, String pLoginToken) throws Exception;

    GetOldTokenResult getOldDeviceToken(String  pBuildSn, String pCpuSn, String pIMEI, String pWifiMac,
                                        String pBlueToothMac, String pSerialNo, String pDeviceToken) throws Exception;

    LoginUserResponse login(String pEncryptUserName, String pEncryptPassword) throws Exception;

    GetActivateUserResultResponse queryActivateResult(String pDeviceID, String pRequestID) throws
            Exception;

    ConfirmDeviceIDResponse confirmDeviceID(String  pBuildSn, String pCpuSn, String pIMEI, String pWifiMac,
                                            String pBlueToothMac, String pSerialNo, String pAndroidID,
                                            String pDeviceToken) throws Exception;

    QueryDeviceStatusResponse getDeviceStatus(String pDeviceID, String pSerialNum) throws Exception;

}
