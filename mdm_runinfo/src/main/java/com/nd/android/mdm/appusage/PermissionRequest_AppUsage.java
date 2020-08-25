package com.nd.android.mdm.appusage;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.frame.api.permission.AdhocPermissionRequestAbs;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.ui.activity.AdhocRequestActivity;
import com.nd.android.adhoc.basic.util.app.AdhocPackageUtil;
import com.nd.sdp.android.serviceloader.annotation.Service;

/**
 * Created by HuangYK on 2020/7/14.
 */
@Service(AdhocPermissionRequestAbs.class)
public class PermissionRequest_AppUsage extends AdhocPermissionRequestAbs {

    private static final String TAG = "PermissionRequest_AppUsage";

    @NonNull
    @Override
    public String getPermission() {
        return "android.settings.USAGE_ACCESS_SETTINGS";
    }

    @NonNull
    @Override
    public String geManifestPermission() {
        return "android.settings.USAGE_ACCESS_SETTINGS";
    }

    @Override
    public void doPermissionRequest(@NonNull final IAdhocPermissionRequestCallback pCallback) {

        // 只有 SDK 23 以上才去申请这个权限，否则直接返回 true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AppUsageRequestActivity.startRequest(AdhocBasicConfig.getInstance().getAppContext(), new AdhocRequestActivity.IResultCallback() {
                        @Override
                        public void onCallback(int requestCode, int resultCode, Intent intent) {
                            pCallback.onResult(checkGranted());
                        }
                    }
            );
            return;
        }

        pCallback.onResult(true);
    }

    @Override
    public boolean checkGranted() {
        // SDK 23 以下，不申请这个权限，直接过
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        // 已经安装了 SystemService，就当作过了，不需要申请这个权限了
        if (AdhocPackageUtil.checkPackageInstalled("com.nd.adhoc.systemservice")) {
            return true;
        }
        // 已经安装了 华为控制包
        if (AdhocPackageUtil.checkPackageInstalled("com.nd.android.adhoc.huawei.sdkprovider")) {
            return true;
        }

        try {
            Context context = AdhocBasicConfig.getInstance().getAppContext();
            PackageManager pm = context.getPackageManager();
            ApplicationInfo info = pm.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager aom = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            if (aom == null) {
                return false;
            }
            aom.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, info.uid, info.packageName);
            return aom.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, info.uid, info.packageName)
                    == AppOpsManager.MODE_ALLOWED;
        } catch (PackageManager.NameNotFoundException e) {
            Logger.w(TAG, "checkGranted error: " + e);
        }
        return false;
    }
}
