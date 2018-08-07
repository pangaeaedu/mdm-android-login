package com.nd.android.adhoc.communicate.connect;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.communicate.connect.event.IDeviceInfoEvent;
import com.nd.android.adhoc.communicate.connect.listener.IAdhocConnectListener;
import com.nd.android.adhoc.communicate.connect.listener.IAdocFileTransferListener;
import com.nd.android.adhoc.communicate.receiver.ICmdReceiver;

/**
 * Created by HuangYK on 2018/5/4.
 */

public interface IAdhocConnectModule {

    void startAdhoc();

    void setDeviceInfoEvent(IDeviceInfoEvent pDeviceInfoEvent);

    void setConnectListener(IAdhocConnectListener pListener);

    void sendLoginInfo(String pDevToken);

    boolean isAdhocConnect();

    void modifyTurnId(@NonNull String pTurnId);

    void sendMessage(@NonNull final String pMessage);

    void doHttpPost(final String pUrl, final String pContent);

    void addCmdReceiver(ICmdReceiver pCmdReceiver);

    void setAdocFileTransferListener(IAdocFileTransferListener pListener);

    void uploadFile(String pLocalPath, String pFileInfo, int pTimeOut);

    void release();
}
