package com.nd.android.adhoc.login.basicService.http;

import com.nd.android.adhoc.login.basicService.data.http.ActivateHttpResult;
import com.nd.android.adhoc.login.basicService.data.http.GetOldTokenResult;

import org.json.JSONObject;

public interface IHttpService {

    void requestPolicy(String pDeviceToken, long pTime, JSONObject pData) throws Exception;

    IBindResult bindDevice( String pDeviceToken, String pPushID,
                           String pSerialNum) throws Exception;

    ActivateHttpResult activateUser(String pUCAccessToken, String pDeviceToken) throws
            Exception;

    GetOldTokenResult getOldDeviceToken(String  pBuildSn, String pCpuSn, String pIMEI, String pWifiMac,
                                        String pBlueToothMac, String pSerialNo) throws Exception;
}
