package com.nd.android.adhoc.communicate.connect;

import androidx.annotation.NonNull;

import org.json.JSONObject;


/**
 * Created by HuangYK on 2018/5/4.
 */

public interface IAdhocConnectModule {

    void startAdhoc();

//    void setDeviceInfoEvent(IDeviceInfoEvent pDeviceInfoEvent);

//    void setConnectListener(IAdhocConnectListener pListener);

    void sendLoginInfo(String pDevToken, JSONObject pDeviceInfo);

    boolean isAdhocConnect();

    void modifyTurnId(@NonNull String pTurnId);

    void sendMessage(@NonNull final String pMessage);

//    void doHttpPost(final String pUrl, final String pContent);

//    void setAdocFileTransferListener(IAdocFileTransferListener pListener);

    void uploadFile(String pLocalPath, String pFileInfo, int pTimeOut);

    void release();
}
