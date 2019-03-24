package com.nd.android.mdm.autoupdate.initialize;

import android.content.Context;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.frame.api.initialization.AdhocAppInitAsyncAbs;
import com.nd.android.adhoc.basic.update.AdhocUpdateVersionManager;
import com.nd.android.adhoc.basic.update.UpdateConfiguration;
import com.nd.android.adhoc.basic.update.installbusiness.IInstallPackage;
import com.nd.android.adhoc.control.define.IControl_Apk;
import com.nd.android.mdm.basic.ControlFactory;
import com.nd.android.mdm.biz.common.ErrorCode;
import com.nd.sdp.android.serviceloader.annotation.Service;

/**
 * Created by HuangYK on 2018/12/8.
 */
@Service(AdhocAppInitAsyncAbs.class)
public class MdmAutoUpdateInitAsync extends AdhocAppInitAsyncAbs {

    @Override
    public void doInitAsync() {
        UpdateConfiguration configuration = new UpdateConfiguration();
        configuration.setInstalledInBackground(true);
        configuration.setInstallPackage(new IInstallPackage() {
            @Override
            public boolean installPackage(Context context, String strApkPath) {
                IControl_Apk control_apk = ControlFactory.getInstance().getControl(IControl_Apk.class);
                return null != control_apk && ErrorCode.SUCCESS == control_apk.install(strApkPath, false);
            }
        });
        AdhocUpdateVersionManager.getInstance().autoUpdate(AdhocBasicConfig.getInstance().getAppContext(), configuration);
    }
}
