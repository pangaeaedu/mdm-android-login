package com.nd.android.adhoc.communicate.connect.listener;

import android.support.annotation.Nullable;

/**
 * 文件传输监听
 * <p>
 * Created by HuangYK on 2018/5/18.
 */
public interface IAdocFileTransferListener {

    void onFileArriveBegin(long pSessionId, @Nullable String pFileName);

    void onFileArriveProgress(long pSessionId, @Nullable String pFileName, long totalSize, long recvSize);

    void onFileArriveComplete(long pSessionId, @Nullable String pFilePath);

    void onFileArriveException(long pSessionId, @Nullable String pFileName, int pErrorCode, @Nullable String pErrorMsg);
}
