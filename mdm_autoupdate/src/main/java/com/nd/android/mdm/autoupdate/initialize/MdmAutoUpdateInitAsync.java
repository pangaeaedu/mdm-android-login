package com.nd.android.mdm.autoupdate.initialize;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.frame.api.initialization.AdhocAppInitAsyncAbs;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.update.AdhocUpdateVersionManager;
import com.nd.android.adhoc.basic.update.IAutoUpdateFilter;
import com.nd.android.adhoc.basic.update.IAutoUpdatePackages;
import com.nd.android.adhoc.basic.update.UpdateConfiguration;
import com.nd.android.adhoc.basic.util.app.AdhocAppUtil;
import com.nd.android.adhoc.basic.util.thread.AdhocMainLooper;
import com.nd.sdp.android.serviceloader.AnnotationServiceLoader;
import com.nd.sdp.android.serviceloader.annotation.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by HuangYK on 2018/12/8.
 */
@Service(AdhocAppInitAsyncAbs.class)
public class MdmAutoUpdateInitAsync extends AdhocAppInitAsyncAbs {

    private static final String TAG = "MdmAutoUpdateInitAsync";

    @Override
    public void doInitAsync(){
        List<IAutoUpdatePackages> packages = getFromServiceLoader(IAutoUpdatePackages.class);
        if (packages.isEmpty()) {
            Logger.w(TAG, "doInitAsync: no valid auto update packages");
            return;
        }

        final Context context = AdhocBasicConfig.getInstance().getAppContext();
        String strChannel = "";
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            strChannel = appInfo.metaData.getString("APP_CHANNEL");
            Logger.i(TAG, "doInitAsync: current channel is :" + strChannel);
        } catch (PackageManager.NameNotFoundException e) {
            Logger.e(TAG, "no channel");
        }
        List<IAutoUpdateFilter> filters = getFromServiceLoader(IAutoUpdateFilter.class);
        for (IAutoUpdatePackages updatePackages : packages) {
            //遍历所有需要自动升级的包
            boolean isValid = true;
            UpdateConfiguration configuration = updatePackages.getConfigration();
            for (IAutoUpdateFilter filter : filters) {
                if (filter.filter(configuration)) {
                    isValid = false;
                    Logger.i(TAG, updatePackages + "is filtered by " + filter);
                    break;
                }
            }
            if (!isValid) {
                continue;
            }
            Logger.i(TAG, "start auto update:" + updatePackages);
            configuration.setInstalledInBackground(true);
            if (TextUtils.isEmpty(configuration.getChannel())) {
                configuration.setChannel(strChannel);
            }
            if(TextUtils.isEmpty(configuration.getChannel()) && AdhocAppUtil.isDebuggable(context)){
                AdhocMainLooper.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "少年，你是不是没有配置渠道？提示内容长又长~~~~~~~~~", Toast.LENGTH_LONG).show();
                    }
                });
            }
            AdhocUpdateVersionManager.getInstance().autoUpdate(context, configuration);
        }
    }

    @NonNull
    private <T> List<T> getFromServiceLoader(Class<T> pClass){
        List<T> result = new ArrayList<>();
        Iterator<T> iterator = AnnotationServiceLoader.load(pClass, pClass.getClassLoader()).iterator();
        if (iterator != null) {
            while (iterator.hasNext()){
                T next = iterator.next();
                if (next != null) {
                    Logger.i(TAG, "getFromServiceLoader: " + next);
                    result.add(next);
                }
            }
        }
        return result;
    }
}
