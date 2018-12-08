package com.nd.android.adhoc.communicate.connect.callback;

import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.communicate.connect.listener.IAdhocConnectListener;
import com.nd.android.adhoc.communicate.connect.listener.IAdocFileTransferListener;
import com.nd.android.adhoc.communicate.constant.AdhocCmdFromTo;
import com.nd.android.adhoc.communicate.receiver.ICmdMsgReceiver;
import com.nd.android.adhoc.communicate.utils.BroadcastUtil;
import com.nd.android.mdm.biz.common.ErrorCode;
import com.nd.eci.sdk.IAdhocCallback;
import com.nd.sdp.android.serviceloader.AnnotationServiceLoader;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Created by HuangYK on 2018/5/4.
 */

public class AdhocCallbackImpl implements IAdhocCallback {

    private static final String TAG = "AdhocCallbackImpl";

    private Map<Long, String> fileInfos;
    private Map<Long, String> fileNames = new HashMap<>();
    private IAdhocConnectListener mConnectListener;
    private boolean mAdhocConnect = false;

    private ICmdMsgReceiver mCmdReceiver;

    private IAdocFileTransferListener mFileTransferListener;


    public AdhocCallbackImpl() {
        fileInfos = new HashMap<>();

        Iterator<ICmdMsgReceiver> receiverIterator =  AnnotationServiceLoader.load(ICmdMsgReceiver.class).iterator();
        mCmdReceiver = receiverIterator.next();
    }

    public void setConnectListener(@NonNull IAdhocConnectListener pConnectListener) {
        mConnectListener = pConnectListener;
        if (mAdhocConnect && mConnectListener != null) {
            mConnectListener.onConnectionAvaialble();
        }
    }

    public void setFileTransferListener(IAdocFileTransferListener pFileTransferListener) {
        mFileTransferListener = pFileTransferListener;
    }

    public boolean isAdhocConnect() {
        return mAdhocConnect;
    }


    @Override
    public void onSendProgress(long l, int i) throws RemoteException {

    }

    @Override
    public void onSendException(long sessionId, int errorCode, String errorMsg) throws RemoteException {
        Logger.e(TAG, String.format(Locale.getDefault(), "on data arrive exception,session id : %d ,error code: %d , error message: %s", sessionId, errorCode, errorMsg));
    }

    @Override
    public void onSendComplete(long l) throws RemoteException {
        Logger.d(TAG, "send complete:" + l);
    }

    @Override
    public void onCmdArrive(long sessionId, byte[] bytes) throws RemoteException {
        if (bytes == null || bytes.length <= 0) {
            Logger.w(TAG, "onCmdArrive, data byte is empty");
            return;
        }

        doCmdReceived(bytes);
        Logger.d(TAG, "on Cmd Arrive : session Id = " + sessionId + ", from = " + "" + ", data length = " + bytes.length);
    }

    @Override
    public void onCmdArriveEx(long l, byte[] bytes) throws RemoteException {
        if (bytes == null) {
            Logger.w(TAG, "onCmdArriveEx, bytes is empty");
            return;
        }

        doCmdReceived(bytes);

        String strLog = String.format("on cmd arrive:%s", new String(bytes));
        Logger.d(TAG, strLog);
    }

    @Override
    public int onDataArriveBegin(long l, byte[] bytes, int i) throws RemoteException {
        return 1;
    }

    @Override
    public void onDataArriveComplete(long l, byte[] bytes) throws RemoteException {
        if (bytes == null) {
            Logger.w(TAG, "onDataArriveComplete, bytes is empty");
            return;
        }

        doCmdReceived(bytes);
    }

    @Override
    public void onDataArriveProgress(long l, int i, int i1) throws RemoteException {

    }

    @Override
    public void onDataArriveException(long sessionId, int errorCode, String errorMsg) throws RemoteException {
    }

    @Override
    public int onFileArriveBegin(long sessionId, String filePath, byte[] bytes, int totalSize) throws RemoteException {

        String strLog = "On File Arrive Begin : session Id = " + sessionId + ", file path = " + filePath + ", file size = " + totalSize;
        BroadcastUtil.sendLogBroadcast(strLog);
        Logger.d(TAG, strLog);
        fileInfos.put(sessionId, new String(bytes));
        String fileName = new File(filePath).getName();
        // todo zyb的写法 只能沿用,没法改
        fileNames.put(sessionId, fileName);

        if (mFileTransferListener != null) {
            mFileTransferListener.onFileArriveBegin(sessionId, fileName);
        }
        return 1;
    }

    @Override
    public void onFileArriveProgress(long sessionId, int totalSize, int recvSize) throws RemoteException {

        String strLog = "On File Arrive Progress : session Id = " + sessionId + ", file size = " + totalSize + ", recv size = " + recvSize;
        BroadcastUtil.sendLogBroadcast(strLog);
        Logger.d(TAG, strLog);
        // todo zyb的写法 只能沿用,没法改
        String fileName = fileNames.get(sessionId);

        if (mFileTransferListener != null) {
            mFileTransferListener.onFileArriveProgress(sessionId, fileName, totalSize, recvSize);
        }

    }

    @Override
    public void onFileArriveComplete(long sessionId, String filePath) throws RemoteException {

        String strLog = "On File Arrive Complete : session Id = " + sessionId + ", file path = " + filePath;
        BroadcastUtil.sendLogBroadcast(strLog);
        Logger.d(TAG, strLog);
        String info = fileInfos.remove(sessionId);
        if (info != null && !info.isEmpty()) {
            // 自组网发送文件时可以携带一个消息,消息只在start的时候获取,但有时在结束时才用,因此在结束时才触发,感觉这么写不合理,但是流程需要
            onCmdArrive(sessionId, info.getBytes());
        }
        // todo zyb的写法 只能沿用,没法改
        String fileName = fileNames.get(sessionId);

        if (mFileTransferListener != null) {
            if (filePath.contains(fileName)) {
                mFileTransferListener.onFileArriveComplete(sessionId, filePath);
            } else {
                mFileTransferListener.onFileArriveException(sessionId, fileName, ErrorCode.FAILED, "文件下载成功信息不匹配");
            }
        }

        fileNames.remove(sessionId);
    }

    @Override
    public void onFileArriveException(long sessionId, int errorCode, String errorMsg) throws RemoteException {

        String strLog = "On File Arrive Exception : session Id = " + String.valueOf(sessionId) + ", errorCode = " + errorCode + ", errMsg = " + errorMsg;
        Logger.e(TAG, strLog);
        BroadcastUtil.sendLogBroadcast(strLog);
        String info = fileInfos.remove(sessionId);
        if (info != null && !info.isEmpty()) {
            onCmdArrive(sessionId, info.getBytes());
        }
        // todo zyb的写法 只能沿用,没法改
        String fileName = fileNames.get(sessionId);

        if (mFileTransferListener != null) {
            mFileTransferListener.onFileArriveException(sessionId, fileName, errorCode, errorMsg);
        }

        fileNames.remove(sessionId);
    }

    @Override
    public void onConnectionAvailable(int connectionTarget) throws RemoteException {
        mAdhocConnect = true;

        if (mConnectListener != null) {
            mConnectListener.onConnectionAvaialble();
        }
    }

    @Override
    public void onConnectionChanged(int connectionTarget) throws RemoteException {

    }

    @Override
    public void onConnectionClosed(int connectionTarget) throws RemoteException {
        mAdhocConnect = false;
    }

    @Override
    public IBinder asBinder() {
        return null;
    }


    private void doCmdReceived(byte[] pCmdMsgBytes) {

        if (mCmdReceiver == null) {
            Logger.w(TAG, "mCmdReceiver is null");
            return;
        }

        if (pCmdMsgBytes == null) {
            return;
        }

        mCmdReceiver.onCmdReceived(new String(pCmdMsgBytes), AdhocCmdFromTo.MDM_CMD_ADHOC, AdhocCmdFromTo.MDM_CMD_ADHOC);
    }
}
