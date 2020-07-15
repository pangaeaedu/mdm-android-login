package com.nd.android.mdm.appusage;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.log.Logger;

/**
 * Created by HuangYK on 2020/7/14.
 */

public class AppUsagePermissionUtil {

    private static final String TAG = "AppUsagePermissionUtil";

    public static boolean checkAppUsagePermission(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return false;
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
            Logger.w(TAG, "checkAppUsagePermission error: " + e);
        }
        return false;
    }
}
