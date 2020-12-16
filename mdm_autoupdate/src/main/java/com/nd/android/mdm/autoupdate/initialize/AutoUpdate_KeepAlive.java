package com.nd.android.mdm.autoupdate.initialize;

import android.content.Context;
import android.support.annotation.Keep;

import com.nd.android.adhoc.basic.update.IAutoUpdatePackages;
import com.nd.android.adhoc.basic.update.UpdateConfiguration;
import com.nd.android.adhoc.basic.update.installbusiness.IInstallPackage;
import com.nd.android.adhoc.control.define.IControl_Apk;
import com.nd.android.mdm.basic.ControlFactory;
import com.nd.android.mdm.biz.common.ErrorCode;
import com.nd.sdp.android.serviceloader.annotation.Service;

/**
 * 保活的自动升级
 */
@Keep
@Service(IAutoUpdatePackages.class)
public class AutoUpdate_KeepAlive implements IAutoUpdatePackages {
    @Override
    public UpdateConfiguration getConfigration() {
        UpdateConfiguration configuration = new UpdateConfiguration();
        configuration.setPackageName("com.nd.adhoc.keepaliveservice");
        configuration.setInstallPackage(new IInstallPackage() {
            @Override
            public boolean installPackage(Context context, String strApkPath) {
                IControl_Apk control_apk = ControlFactory.getInstance().getControl(IControl_Apk.class);
                return null != control_apk && ErrorCode.SUCCESS == control_apk.install(strApkPath, false);
            }
        });
        return configuration;
    }
}
