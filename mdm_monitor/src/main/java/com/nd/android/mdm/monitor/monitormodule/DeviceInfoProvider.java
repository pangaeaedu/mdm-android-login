package com.nd.android.mdm.monitor.monitormodule;

import android.text.TextUtils;

import com.nd.android.adhoc.basic.util.root.AdhocNewRootUtils;
import com.nd.android.adhoc.basic.util.system.AdhocDeviceUtil;
import com.nd.android.adhoc.control.define.IControl_DeviceRomName;
import com.nd.android.adhoc.control.define.IControl_DeviceRomVersion;
import com.nd.android.adhoc.control.define.IControl_IMEI;
import com.nd.android.aioe.device.info.util.DeviceInfoHelper;
import com.nd.android.mdm.basic.ControlFactory;

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
public class DeviceInfoProvider {
    private static final String TAG = "DeviceInfoProvider";
    private String mSerialNumber;
    private String mScreenInch;
    private String mResolution;
    private String mBlueToothMac;

    private boolean mbRootCmdExecute = false;
    private boolean mbIsRoot;

    private String mImei1;
    private boolean mGetImei1Before = false;
    private String mImei2;
    private boolean mGetImei2Before = false;

    private String mstrRomName;
    private boolean mbGetRomNameByControl = false;

    private String mstrDeviceRomVersion;
    private boolean mbGetDeviceRomVersionByControl = false;

    public String getDeviceRomVersion() {
        if(mbGetDeviceRomVersionByControl){
            return mstrDeviceRomVersion;
        }

        IControl_DeviceRomVersion control_deviceRomVersion = ControlFactory.getInstance().getControl(IControl_DeviceRomVersion.class);
        if (control_deviceRomVersion != null) {
            mstrDeviceRomVersion = control_deviceRomVersion.getRomVersion();
        }
        mbGetDeviceRomVersionByControl = true;
        return mstrDeviceRomVersion;
    }

    public String getRomName() {
        if(mbGetRomNameByControl){
            return mstrRomName;
        }

        IControl_DeviceRomName control_deviceRomName = ControlFactory.getInstance().getControl(IControl_DeviceRomName.class);
        if (control_deviceRomName != null) {
            mstrRomName = control_deviceRomName.getRomName();
        }
        mbGetRomNameByControl = true;
        return mstrRomName;
    }

    public String getImei1() {
        if(mGetImei1Before){
            return mImei1;
        }

        IControl_IMEI control_imei = ControlFactory.getInstance().getControl(IControl_IMEI.class);
        if (control_imei != null) {
            mImei1 = control_imei.getIMEI(0);
        }
        mGetImei1Before = true;
        return mImei1;
    }

    public String getImei2() {
        if(mGetImei2Before){
            return mImei2;
        }

        IControl_IMEI control_imei = ControlFactory.getInstance().getControl(IControl_IMEI.class);
        if (control_imei != null) {
            mImei2 = control_imei.getIMEI(1);
        }
        mGetImei2Before = true;
        return mImei2;
    }

    public boolean getIsRoot() {
        if(mbRootCmdExecute){
            return mbIsRoot;
        }
        mbIsRoot = AdhocNewRootUtils.retrieveRootStatusViaExecuteSuCommand();
        mbRootCmdExecute = true;
        return mbIsRoot;
    }

    public String getBlueToothMac() {
        if(TextUtils.isEmpty(mBlueToothMac)){
            mBlueToothMac = AdhocDeviceUtil.retrieveThenCacheBluetoothMacAddressViaReflection();
        }
        return mBlueToothMac;
    }

    public String getScreenInch(){
        if(TextUtils.isEmpty(mScreenInch)){
            mScreenInch = AdhocDeviceUtil.getScreenSizeInch();
        }
        return mScreenInch;
    }

    public String getResolution(){
        if(TextUtils.isEmpty(mResolution)){
            mResolution = AdhocDeviceUtil.getRealScreenWidth() + "*" + AdhocDeviceUtil.getRealScreenHeight();
        }
        return mResolution;
    }

    public String getSerialNumber(){
        if(TextUtils.isEmpty(mSerialNumber)){
            mSerialNumber = DeviceInfoHelper.getSerialNumberThroughControl();
        }
        return mSerialNumber;
    }

}
