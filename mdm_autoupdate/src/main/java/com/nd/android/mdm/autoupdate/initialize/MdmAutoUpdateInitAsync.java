package com.nd.android.mdm.autoupdate.initialize;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.frame.api.initialization.AdhocAppInitAsyncAbs;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.update.AdhocUpdateVersionManager;
import com.nd.android.adhoc.basic.update.UpdateConfiguration;
import com.nd.android.adhoc.basic.update.installbusiness.IInstallPackage;
import com.nd.android.adhoc.basic.util.app.AdhocAppUtil;
import com.nd.android.adhoc.basic.util.thread.AdhocMainLooper;
import com.nd.android.adhoc.control.define.IControl_Apk;
import com.nd.android.mdm.basic.ControlFactory;
import com.nd.android.mdm.biz.common.ErrorCode;
import com.nd.sdp.android.serviceloader.annotation.Service;

/**
 * Created by HuangYK on 2018/12/8.
 */
@Service(AdhocAppInitAsyncAbs.class)
public class MdmAutoUpdateInitAsync extends AdhocAppInitAsyncAbs {

    private static final String TAG = "MdmAutoUpdateInitAsync";

    /**
     * 决定没有配置channel时，是否要抛异常，　这个方法　好几个地方用了，可以搞成通用
     * @return
     */
    private boolean getNeedToToastWhenNoChannel(){
        Context appContext = AdhocBasicConfig.getInstance().getAppContext();
        boolean isLocalEnv;
        try {
            ApplicationInfo appInfo = appContext.getPackageManager().getApplicationInfo(appContext.getPackageName(),
                    PackageManager.GET_META_DATA);
            if (!appInfo.metaData.containsKey("LOCAL_ENV")) {
                throw new IllegalArgumentException("The LOCAL_ENV value of the META_DATA configuration in the manifest file does not exist.");
            }
            isLocalEnv = appInfo.metaData.getBoolean("LOCAL_ENV");
        } catch (PackageManager.NameNotFoundException e) {
            isLocalEnv = false;
        }

        if (!AdhocAppUtil.isDebuggable(appContext) || !isLocalEnv) {
            return false;
        }else {
            return true;
        }
    }

    @Override
    public void doInitAsync(){
        final Context context = AdhocBasicConfig.getInstance().getAppContext();
        UpdateConfiguration configuration = new UpdateConfiguration();
        configuration.setInstalledInBackground(true);
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            String strChannel = appInfo.metaData.getString("APP_CHANNEL");
            if (!TextUtils.isEmpty(strChannel)) {
                configuration.setChannel(strChannel);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Logger.e(TAG, "no channel");
        }

        if(TextUtils.isEmpty(configuration.getChannel()) && getNeedToToastWhenNoChannel()){
            AdhocMainLooper.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "少年，你是不是没有配置渠道？提示内容长又长~~~~~~~~~", Toast.LENGTH_LONG).show();
                }
            });
        }

        configuration.setInstallPackage(new IInstallPackage() {
            @Override
            public boolean installPackage(Context context, String strApkPath) {
                IControl_Apk control_apk = ControlFactory.getInstance().getControl(IControl_Apk.class);
                return null != control_apk && ErrorCode.SUCCESS == control_apk.install(strApkPath, false);
            }
        });
        AdhocUpdateVersionManager.getInstance().autoUpdate(context, configuration);
    }
}
