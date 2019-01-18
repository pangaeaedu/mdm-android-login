package com.nd.android.mdm.autoupdate;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.util.storage.AdhocStorageUtil;
import com.nd.android.adhoc.basic.util.app.AdhocPackageUtil;
import com.nd.android.adhoc.communicate.utils.HttpUtil;
import com.nd.android.adhoc.control.MdmControlFactory;
import com.nd.android.adhoc.control.define.IControl_Apk;
import com.nd.android.adhoc.file_transfer.business.download.AdhocDownloader;
import com.nd.android.adhoc.file_transfer.business.download.listener.DownloadStatusListener;
import com.nd.android.adhoc.file_transfer.business.download.operator.DownloadListenerManager;
import com.nd.android.adhoc.file_transfer.business.download.param.AdhocDownloadParams;
import com.nd.android.adhoc.file_transfer_api.define.AdhocFileStatusConstant;
import com.nd.android.mdm.biz.common.ErrorCode;
import com.nd.android.mdm.biz.common.MsgCode;
import com.nd.android.mdm.biz.env.MdmEvnFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by XWQ on 2018/1/30 0030.
 */

public class AutoUpdateModule {
    private static final String TAG = "AutoUpdateModule";


    private final static int MSG_DOWNLOAD = 1;

    private static AutoUpdateModule instance;
    private HandlerThread mAutoUpdateThread;
    private Handler mAutoUpdateHandler;
    private Handler mHandler;
    private int mVersionCode;

    private String mOsType;
    private String mPkgName;
    private String mPlatfromVer;

    private String mAppName;

    private int mTargetVersionCode;
    private int mTargetRestrictVersionCode;

    private int mLatestVersionCode;

    private String mTargetResAddr;
    private boolean mEnableMobileData;

//    private static final String sFileName = "Assistant.apk";
//    private static final String sParentPath = "/sdcard/temp/";

    private int mErrorCode = ErrorCode.UNKNOWN;
    private int mMegCode = MsgCode.ERROR_NONE;

    private String mApkPath;

    private Runnable mAutoUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            getLatestAppInfo();
            if (checkNeedUpdate()) {
                mHandler.removeMessages(MSG_DOWNLOAD);
                mHandler.sendEmptyMessage(MSG_DOWNLOAD);
            }
        }
    };

    private Runnable mInstallRunnable = new Runnable() {
        @Override
        public void run() {
            install();
        }
    };

    public AutoUpdateModule() {
        mAutoUpdateThread = new HandlerThread("auto_update_thread");
        mAutoUpdateThread.start();
        mAutoUpdateHandler = new Handler(mAutoUpdateThread.getLooper());
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_DOWNLOAD:
                        download();
                        break;
                    default:
                        break;
                }
            }
        };

        // 1：android 2：ios
        mOsType = "1";
        mPlatfromVer = android.os.Build.VERSION.RELEASE;
    }

    public static AutoUpdateModule getInstance() {
        if (instance == null) {
            instance = new AutoUpdateModule();
        }
        return instance;
    }

    public void init(@NonNull Context context) {
        mPkgName = context.getPackageName();
        PackageInfo packageInfo = AdhocPackageUtil.getPackageInfo(context, mPkgName);
        if (packageInfo != null) {
            mVersionCode = packageInfo.versionCode;
            mAppName = packageInfo.applicationInfo == null ? "" : packageInfo.applicationInfo.name;
        }
    }

    public void trigger() {
        mAutoUpdateHandler.removeCallbacksAndMessages(null);
        mAutoUpdateHandler.post(mAutoUpdateRunnable);
    }

    public void release() {
        // If "token" is null, all callbacks and messages will be removed.
        mAutoUpdateHandler.removeCallbacksAndMessages(null);
        mHandler.removeCallbacksAndMessages(null);
    }

    private void getLatestAppInfo() {
        Logger.d(TAG, "get Latest App Info");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("osType", mOsType);
        params.put("pkgName", mPkgName);
        params.put("platfromVer", mPlatfromVer);
        String url = MdmEvnFactory.getInstance().getCurEnvironment().getUrl() + "/v1/app/appInfo";
        String responseStr = null;
        try {
            responseStr = HttpUtil.get(url, params);
        } catch (IllegalArgumentException e) {
            Logger.e(TAG, "Illegal Argument Exception," + e.toString());
        }
        if (responseStr != null && !responseStr.isEmpty()) {
            try {
                JSONObject json = new JSONObject(responseStr);
                mTargetVersionCode = json.getInt("versionCode");
                mTargetRestrictVersionCode = json.getInt("restrictVersionCode");
                if (!json.isNull("resAddr")) {
                    mTargetResAddr = json.getString("resAddr");
                }
                mEnableMobileData = json.getInt("enableMobileData") != 0;
            } catch (JSONException e) {
                Logger.e(TAG, "read json failed," + e.toString() + ";" + responseStr);
            }
        }
    }

    public boolean checkNeedUpdate(int targetVersionCode) {
        boolean retNeedUpdate = false;
        // 未取到 Version code, 版本有問題, 強制升級
        if (mVersionCode == 0) {
            retNeedUpdate = true;
        }
        // 強制升級
        if (targetVersionCode > mVersionCode) {
            retNeedUpdate = true;
        }
        return retNeedUpdate;
    }

    private boolean checkNeedUpdate() {
        boolean retNeedUpdate = false;
        // 未取到 Version code, 版本有問題, 強制升級
        if (mVersionCode == 0) {
            retNeedUpdate = true;
        }
        // 一般升級, 目前一般升級的行為和強制升級一樣
        if (mTargetVersionCode > mVersionCode) {
            mLatestVersionCode = mTargetVersionCode;
            retNeedUpdate = true;
        }
        // 強制升級
        if (mTargetRestrictVersionCode > mVersionCode) {
            mLatestVersionCode = mTargetRestrictVersionCode;
            retNeedUpdate = true;
        }
        return retNeedUpdate;
    }

    private void download() {
        if (mTargetResAddr == null || mTargetResAddr.isEmpty()) {
            return;
        }

//        final String parentDirPath = AdhocStorageUtil.makesureFileSepInTheEnd(sParentPath + mPkgName);
        final String parentDirPath =
                AdhocStorageUtil.getFileSDCardCacheDir(AdhocBasicConfig.getInstance().getAppContext(), "update");
        if (TextUtils.isEmpty(parentDirPath)) {
            Logger.e(TAG, "Error downloading upgrade file: sdcard dir is empty");
            return;
        }

        final String apkName = mAppName + "_" + mLatestVersionCode + ".apk";

        // 格式： /sdcard/xxxxxxxx/appname_versioncode.apk
        mApkPath = parentDirPath + apkName;

        DownloadListenerManager.addDownloadListener(new DownloadStatusListener() {
            @Override
            public void onFailed(@NonNull String pUrl) {
                if (!mTargetResAddr.equals(pUrl)) {
                    return;
                }

                mErrorCode = ErrorCode.FAILED;
                mMegCode = MsgCode.ERROR_DOWNLOAD_FAIL;
                Logger.e(TAG, String.format("download '%s' from '%s' failed", parentDirPath, mTargetResAddr));

                DownloadListenerManager.removeDownloadListener(this);
            }

            @Override
            public void onProgress(@NonNull String pUrl, long pCurSize, long pTotal, long pSpeed) {

            }

            @Override
            public void onSuccess(@NonNull String pUrl) {
                if (!mTargetResAddr.equals(pUrl)) {
                    return;
                }
                Logger.d(TAG, String.format("download '%s' from '%s' success", parentDirPath, mTargetResAddr));
                mHandler.removeCallbacks(null);
                mHandler.post(mInstallRunnable);

                DownloadListenerManager.removeDownloadListener(this);

            }
        });

        AdhocDownloadParams downloadParams =
                new AdhocDownloadParams(AdhocFileStatusConstant.FILE_TYPE_NORMAL)
                        .setUrl(mTargetResAddr)
                        .setFileName(apkName)
                        .setLocalDir(parentDirPath);
        AdhocDownloader.startDownload(downloadParams);



//        DownloadReq downloadReq = new DownloadReq(downloadUri, mDestinationUri);
//        downloadReq.listener(new DownloadListener() {
//            @Override
//            public void onStart(DownloadReq downloadReq) {
//
//            }
//
//            @Override
//            public void onProgress(DownloadReq downloadReq, long l, long l1, int i) {
//
//            }
//
//            @Override
//            public void onComplete(DownloadReq downloadReq) {
//                SDKLogUtil.d(String.format("download '%s' from '%s' success", downloadReq.getDestUri(), downloadReq.getDownUri()));
//                mHandler.removeCallbacks(null);
//                mHandler.post(mInstallRunnable);
//            }
//
//            @Override
//            public void onFailed(DownloadReq downloadReq, Throwable throwable) {
//                mErrorCode = ErrorCode.FAILED;
//                mMegCode = MsgCode.ERROR_DOWNLOAD_FAIL;
//                SDKLogUtil.e(String.format("download '%s' from '%s' failed, reason '%s'", downloadReq.getDestUri(), downloadReq.getDownUri(), throwable.getMessage()));
//            }
//        });
//        DownloadModule.getInstance().getDownloader().add(downloadReq);
    }

    private void install() {
        if (TextUtils.isEmpty(mApkPath)) {
            return;
        }

        IControl_Apk control_apk = MdmControlFactory.getInstance().getControl(IControl_Apk.class);

        if (control_apk != null) {
            mErrorCode = control_apk.install(mApkPath);
            if (mErrorCode == ErrorCode.FAILED) {
                mMegCode = MsgCode.ERROR_COMMAND_EXECUTE_FAIL;
            }
        } else {
            Logger.e(TAG, "install controller not found");
            mErrorCode = ErrorCode.FAILED;
            mMegCode = MsgCode.ERROR_COMMAND_MODULE_UNINITIALIZATION;
        }
    }
}