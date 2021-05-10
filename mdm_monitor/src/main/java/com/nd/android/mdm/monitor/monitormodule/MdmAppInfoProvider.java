package com.nd.android.mdm.monitor.monitormodule;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.util.app.AdhocPackageUtil;
import com.nd.android.adhoc.basic.util.system.AdhocDeviceUtil;

/**Administrator
 * @author
 * @name adhoc-101-assistant-app
 * @class name：com.nd.android.mdm.monitor.monitormodule
 * @class describe
 * @time 2021/4/27 19:47
 * @change
 * @chang time
 * @class describe
 */
public class MdmAppInfoProvider {
    private static final String TAG = "MdmAppInfoProvider";
    private int miAppVersionCode = -1;
    private String mstrVersionName = "";

    private boolean mbAppSignedSys = false;
    private boolean mbGetAppSignedSysBefore = false;

    public int getAppVersionCode(Context context){
        if(-1 == miAppVersionCode){
            miAppVersionCode = AdhocDeviceUtil.getPackageVerCode(context);
        }
        return miAppVersionCode;
    }

    public int getAppVersionName(Context context){
        if(TextUtils.isEmpty(mstrVersionName)){
            PackageInfo packageInfo = AdhocPackageUtil.getPackageInfo(context);
            mstrVersionName = packageInfo == null ? "" : packageInfo.versionName;
        }
        return miAppVersionCode;
    }

    public boolean getAppSignedSys(){
        if(!mbGetAppSignedSysBefore){
            mbAppSignedSys = AdhocDeviceUtil.getAppSignedSys();
            mbGetAppSignedSysBefore = true;
        }
        return mbAppSignedSys;
    }
}
