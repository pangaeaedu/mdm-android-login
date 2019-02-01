package com.nd.android.adhoc.login.basicService.http;

import com.nd.android.adhoc.login.basicService.data.http.ActivateHttpResult;
import com.nd.android.adhoc.login.basicService.data.http.GetDeviceStatusResult;
import com.nd.android.adhoc.login.basicService.data.http.GetOldTokenResult;
import com.nd.android.adhoc.login.basicService.data.http.GetTokenResult;
import com.nd.android.adhoc.login.basicService.data.http.LoginUserResult;

import org.json.JSONObject;

public interface IHttpService {

    void requestPolicy(String pDeviceToken, long pTime, JSONObject pData) throws Exception;

    IBindResult bindDevice( String pDeviceToken, String pPushID,
                           String pSerialNum) throws Exception;

    IBindResult bindDeviceWithChannelType( String pDeviceToken, String pPushID,
                            String pSerialNum, int pPushChannelType) throws Exception;

    ActivateHttpResult activateUser(String pUCAccessToken, String pDeviceToken) throws Exception;

    GetOldTokenResult getOldDeviceToken(String  pBuildSn, String pCpuSn, String pIMEI, String pWifiMac,
                                        String pBlueToothMac, String pSerialNo, String pDeviceToken) throws Exception;

    LoginUserResult login(String pEncryptUserName, String pEncryptPassword) throws Exception;

    IQueryActivateResult queryActivateResult(String pDeviceID) throws Exception;

    GetTokenResult confirmDeviceID(String  pBuildSn, String pCpuSn, String pIMEI, String pWifiMac,
                                   String pBlueToothMac, String pSerialNo, String pAndroidID,
                                   String pDeviceToken) throws Exception;

    GetDeviceStatusResult getDeviceStatus(String pDeviceID, String pSerialNum) throws Exception;

}
