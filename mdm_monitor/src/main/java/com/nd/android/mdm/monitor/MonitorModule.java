package com.nd.android.mdm.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.usb.UsbManager;
import android.os.BatteryManager;
import android.os.Build;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.net.dao.AdhocHttpDao;
import com.nd.android.adhoc.basic.util.net.AdhocNetworkUtil;
import com.nd.android.adhoc.basic.util.net.speed.NetSpeedBean;
import com.nd.android.adhoc.basic.util.net.speed.NetSpeedUtil;
import com.nd.android.adhoc.basic.util.system.AdhocDeviceUtil;
import com.nd.android.adhoc.basic.util.thread.AdhocRxJavaUtil;
import com.nd.android.adhoc.basic.util.time.AdhocTimeUtil;
import com.nd.android.adhoc.command.basic.constant.AdhocCmdFromTo;
import com.nd.android.adhoc.command.basic.response.ResponseBase;
import com.nd.android.adhoc.control.define.IControl_AppList;
import com.nd.android.adhoc.control.define.IControl_CameraFacing;
import com.nd.android.aioe.device.info.cache.DeviceIdCache;
import com.nd.android.mdm.basic.ControlFactory;
import com.nd.android.mdm.biz.env.MdmEvnFactory;
import com.nd.android.mdm.monitor.info.AdhocBatteryInfo;
import com.nd.android.mdm.monitor.info.AdhocCpuInfo;
import com.nd.android.mdm.monitor.info.AdhocMemoryInfo;
import com.nd.android.mdm.monitor.info.AdhocNetworkInfo;
import com.nd.android.mdm.monitor.info.AdhocSDCardInfo;
import com.nd.android.mdm.monitor.monitormodule.BatteryInfoProvider;
import com.nd.android.mdm.monitor.monitormodule.CpuInfoProvider;
import com.nd.android.mdm.monitor.monitormodule.DeviceInfoProvider;
import com.nd.android.mdm.monitor.monitormodule.MdmAppInfoProvider;
import com.nd.android.mdm.monitor.monitormodule.MemoryInfoProvider;
import com.nd.android.mdm.monitor.monitormodule.NetWorkInfoProvider;
import com.nd.android.mdm.monitor.monitormodule.SdCardInfoProvider;
import com.nd.android.mdm.wifi_sdk.business.MdmWifiInfoManager;
import com.nd.android.mdm.wifi_sdk.business.basic.constant.MdmWifiStatus;
import com.nd.android.mdm.wifi_sdk.business.basic.listener.IMdmWifiStatusChangeListener;
import com.nd.android.mdm.wifi_sdk.business.bean.MdmWifiInfo;
import com.nd.android.mdm.wifi_sdk.business.bean.MdmWifiVendor;
import com.nd.screen.Screenshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by yaoyue1019 on 9-12.
 */

public class MonitorModule implements IMonitor {

    private static final String TAG = "MonitorModule";


    public static final int TYPE_ALL_APP = 1;
    public static final int TYPE_RUNNING_APP = 2;
    private static final int UPDATE_MESSAGE_DELAY = 5000;
    private static MonitorModule instance;

    private Context mContext;

    private BatteryInfoProvider mBatteryInfoProvider = new BatteryInfoProvider();
    private MemoryInfoProvider mMemoryInfoProvider = new MemoryInfoProvider();
    private CpuInfoProvider mCpuInfoProvider = new CpuInfoProvider();
    private SdCardInfoProvider mSdCardInfoProvider = new SdCardInfoProvider();
    private NetWorkInfoProvider mNetWorkInfoProvider = new NetWorkInfoProvider();
    private DeviceInfoProvider mDeviceInfoProvider = new DeviceInfoProvider();
    private MdmAppInfoProvider mMdmAppInfoProvider = new MdmAppInfoProvider();
    
    private Boolean mHasFrontCamera = true;
    private Boolean mHasBackCamera = true;
    
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            switch (action) {
                case BatteryManager.ACTION_CHARGING:
                    mBatteryInfoProvider.setIsCharging(true);
                    break;
                case BatteryManager.ACTION_DISCHARGING:
                    mBatteryInfoProvider.setIsCharging(false);
                    break;
                case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                    mBatteryInfoProvider.setNeedUpdateInfo();
                    usbAttached();
                    break;
                case UsbManager.ACTION_USB_DEVICE_DETACHED:
                    mBatteryInfoProvider.setNeedUpdateInfo();
                    usbDetached();
                    break;
            }
        }
    };


    private MonitorModule() {
        mContext = AdhocBasicConfig.getInstance().getAppContext();

        MdmWifiInfoManager.getInstance().getWifiListenerManager().addStatusChangeListener(
                new IMdmWifiStatusChangeListener() {
                    @Override
                    public void onWifiStatusChange(MdmWifiStatus pStatus) {
                        if(MdmWifiStatus.CONNECTED != pStatus){
                            return;
                        }
                        mNetWorkInfoProvider.setNeedUpdateInfo();
                        responseDevInfo();
                    }
                }
        );
    }

    private void responseDevInfo() {
        //会出现wifi connect事件到达的时候，deviceID还没有确认的情况，这种情况下，不要上报DevInfo

        String deviceToken = DeviceIdCache.getDeviceId();
        if(TextUtils.isEmpty(deviceToken)){
            return;
        }
        AdhocRxJavaUtil.safeSubscribe(Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    ResponseBase responseBase = new ResponseBase("postdeviceinfo",
                            UUID.randomUUID().toString(),
                            AdhocCmdFromTo.MDM_CMD_DRM.getValue(),
                            "",
                            System.currentTimeMillis());
                    responseBase.setJsonData(getDevInfoJson());
                    new AdhocHttpDao(MdmEvnFactory.getInstance().getCurEnvironment().getUrl())
                            .postAction()
                            .post("/v1/device/deviceinfo", String.class, responseBase.toString());

                } catch (Exception e) {
                    Logger.e(TAG, "responseDevInfo, do response error: " + e);
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()));
    }

    public static MonitorModule getInstance() {
        if (instance == null) {
            instance = new MonitorModule();
        }
        return instance;
    }

    public void init(Context pContext) {
        IntentFilter filter = new IntentFilter();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            filter.addAction(BatteryManager.ACTION_CHARGING);
            filter.addAction(BatteryManager.ACTION_DISCHARGING);
        }
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
//        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mBroadcastReceiver, filter);
        //初始化的时候先去检测一下摄像头的状态
        isCameraFacingChanged();
        Logger.w(TAG, "init:has front camera:" + mHasFrontCamera + ",has back camera:" + mHasBackCamera );
    }


    public void release() {
        mContext.unregisterReceiver(mBroadcastReceiver);
    }


    private void writeToDB(int now) {
        Logger.d(TAG, "MonitorModule write openinfo to db");
    }


    private void usbAttached() {
        Logger.d(TAG,"usb attached:" + AdhocTimeUtil.getTimeStamp());
//        new UsbAttachMessage(true).send();
        if (isCameraFacingChanged()) {
            Logger.w(TAG, "usbAttached:CameraFacingChanged：has front camera:" + mHasFrontCamera + ",has back camera:" + mHasBackCamera );
            responseDevInfo();
        }
    }

    private void usbDetached() {
        Logger.d(TAG,"usb detached:" + AdhocTimeUtil.getTimeStamp());
//        new UsbAttachMessage(false).send();
        if (isCameraFacingChanged()) {
            Logger.w(TAG, "usbDetached:CameraFacingChanged：has front camera:" + mHasFrontCamera + ",has back camera:" + mHasBackCamera );
            responseDevInfo();
        }
    }

    @Override
    public List<PackageInfo> getApplications(int type) {
        List<PackageInfo> packageInfos = new ArrayList<PackageInfo>();
        if (type == TYPE_ALL_APP) {
            PackageManager pm = mContext.getPackageManager();
            if (pm != null) {
                packageInfos.addAll(pm.getInstalledPackages(0));
            }
        } else if (type == TYPE_RUNNING_APP) {
            IControl_AppList control_appList = ControlFactory.getInstance().getControl(IControl_AppList.class);
            if(null != control_appList){
                return control_appList.getRunningAppList();
            }
        }
        return packageInfos;
    }

    @Override
    public AdhocBatteryInfo getBatteryInfo() {
        return new AdhocBatteryInfo(mBatteryInfoProvider.getBatteryLevel(false),
                mBatteryInfoProvider.getIsCharging());
    }

    @Override
    public Bitmap screenShot() {
        return Screenshot.getInstance().getScreenshot();
    }

    @Override
    public Object getValue(String index) {
        switch (index) {
            case IMonitor.CPU_RATE:
                return getCpuInfo().cpuRate;
            case IMonitor.MEMORY_USED:
                return getMemoryInfo().usedMemory;
            case IMonitor.MEMORY_FREE:
                return getMemoryInfo().freeMemory;
            case IMonitor.SD_FREE:
                return getSDCardInfo().freeExternalSpace;
            case IMonitor.SYSTEM_SPACE_FREE:
                return getSDCardInfo().freeSystemSpace;
            case IMonitor.RSSI:
                return getNetworkInfo().rssi;
            case IMonitor.LINK_SPEED:
                return getNetworkInfo().linkSpeed;
            case IMonitor.UPLOAD:
                return NetSpeedUtil.getNetSpeed(mContext.getApplicationInfo().uid).getTxSpeed();
            case IMonitor.DOWNLOAD:
                return NetSpeedUtil.getNetSpeed(mContext.getApplicationInfo().uid).getRxSpeed();
            case IMonitor.SSID:
                return getNetworkInfo().ssid;
            case IMonitor.BATTERY:
                return mBatteryInfoProvider.getBatteryLevel(false);
            case IMonitor.CHARGE:
                return mBatteryInfoProvider.getIsCharging();
            case IMonitor.USED_MEMORY_RATE:
                long usedMemory = getMemoryInfo().usedMemory;
                long totalMemory = getMemoryInfo().totalMemory;
                if (totalMemory == 0) {
                    return "0";
                } else {
                    return String.valueOf(100 * usedMemory / totalMemory);
                }
            case IMonitor.USED_SYSTEM_SPACE_RATE:
                long usedSystemSpace = getSDCardInfo().usedSystemSpace;
                long totalSystemSpace = getSDCardInfo().totalSystemSpace;
                if (totalSystemSpace == 0) {
                    return "0";
                } else {
                    return String.valueOf(100 * usedSystemSpace / totalSystemSpace);
                }
            default:
                return null;
        }
    }

    @Override
    public AdhocCpuInfo getCpuInfo() {
        return mCpuInfoProvider.getCpuInfo(false);
    }

    @Override
    public AdhocMemoryInfo getMemoryInfo() {
        return mMemoryInfoProvider.getMemoryInfo(false);
    }

    @Override
    public AdhocNetworkInfo getNetworkInfo() {
        return mNetWorkInfoProvider.getNetworkInfo(false);
    }

    @Override
    public AdhocSDCardInfo getSDCardInfo() {
        return mSdCardInfoProvider.getSDCardInfo(false);
    }

    public JSONObject getDevInfoJson() throws JSONException {
        JSONObject data = new JSONObject();

        //WIFI信息
        MdmWifiInfo wifiInfo = MdmWifiInfoManager.getInstance().getWifiInfo();
//        data.put("ip", wifiInfo.getIp());
        putJsonData(data,"ip",wifiInfo.getIp());

//        data.put("ssid", wifiInfo.getSsid());
        putJsonData(data,"ssid",wifiInfo.getSsid());

//        data.put("rssi", wifiInfo.getRssi());
        putJsonData(data,"rssi",wifiInfo.getRssi());

        if (MdmWifiInfoManager.getInstance().getIsWiFiConnected()) {
//            data.put("mac", wifiInfo.getMac().replace(":", ""));

            putJsonData(data,"mac", wifiInfo.getMac().replace(":", ""));
        } else {
            String mac = MdmWifiInfoManager.getInstance().getLanConnInfo().getMac();
//            data.put("mac", mac.replace(":", ""));

            putJsonData(data,"mac", mac.replace(":", ""));
        }

//        data.put("link_speed", wifiInfo.getSpeed());
        putJsonData(data,"link_speed",wifiInfo.getSpeed());

//        data.put("ap_mac", wifiInfo.getApMac());
        putJsonData(data,"ap_mac",wifiInfo.getApMac());


        MdmWifiVendor vendor = wifiInfo.getVendor();
//        data.put("ap_factory", vendor == null ? "" : vendor.getVendorName());
        putJsonData(data, "ap_factory", vendor == null ? "" : vendor.getVendorName());

        //网络类型
//        data.put("netType", AdhocNetworkUtil.getNetWorkStateString());
        putJsonData(data,"netType",  mNetWorkInfoProvider.getNetWorkType());

        //SIM卡信息
        String strTemp = AdhocNetworkUtil.getNetworkOperatorName();
        if(null == strTemp){
            strTemp = "";
        }
//        data.put("carrier", strTemp);
        putJsonData(data,"carrier", strTemp);

        strTemp = AdhocDeviceUtil.getSimLine1Numeber();
        if(null == strTemp){
            strTemp = "";
        }
//        data.put("sim", strTemp);
        putJsonData(data,"sim", strTemp);

        //设备信息
//        data.put("model", Build.MODEL);
        putJsonData(data,"model", Build.MODEL);

//        data.put("battery", batteryLevel);
        putJsonData(data,"battery", mBatteryInfoProvider.getBatteryLevel(false));

//        data.put("charge", batteryIsCharging);
        putJsonData(data,"charge", mBatteryInfoProvider.getIsCharging());

//        data.put("cpu_rate", (int) MonitorUtil.getCpuInfo()[8]);
        putJsonData(data,"cpu_rate", getCpuInfo().cpuRate);

//        data.put("serialnum", DeviceHelper.getSerialNumberThroughControl());
        putJsonData(data,"serialnum", mDeviceInfoProvider.getSerialNumber() );

//        data.put("terminaltype", AdhocDeviceUtil.isTabletDevice(mContext) ? 2 : 1);
        putJsonData(data,"terminaltype", AdhocDeviceUtil.isTabletDevice(mContext) ? 2 : 1);

//        data.put("resolution", AdhocDeviceUtil.getRealScreenWidth() + "*" + AdhocDeviceUtil.getRealScreenHeight());
        putJsonData(data,"resolution", mDeviceInfoProvider.getResolution());

        putJsonData(data,"device_size_inch", mDeviceInfoProvider.getScreenInch());

//        data.put("bluetoothmac", AdhocDeviceUtil.retrieveThenCacheBluetoothMacAddressViaReflection());
        putJsonData(data,"bluetoothmac", mDeviceInfoProvider.getBlueToothMac());

        String strImei1 = mDeviceInfoProvider.getImei1();
        String strImei2 = mDeviceInfoProvider.getImei2();
        if(!TextUtils.isEmpty(strImei1)){
            putJsonData(data, "imei", strImei1);
        }
        if(!TextUtils.isEmpty(strImei2)){
            putJsonData(data, "imei2", strImei2);
        }

        //系统、软件信息
//        data.put("sys_version", Build.VERSION.RELEASE);
        putJsonData(data,"sys_version", Build.VERSION.RELEASE);

//        data.put("language", Locale.getDefault().getDisplayLanguage());
        putJsonData(data,"language", Locale.getDefault().getDisplayLanguage());

//        data.put("isRooted", AdhocNewRootUtils.retrieveRootStatusViaExecuteSuCommand() ? 1 : 0);
        putJsonData(data,"isRooted", mDeviceInfoProvider.getIsRoot() ? 1 : 0);

//        data.put("panelId", DeviceHelper.getSerialNumberThroughControl());
        putJsonData(data,"panelId", mDeviceInfoProvider.getSerialNumber());

        String strRomeName = mDeviceInfoProvider.getRomName();
        String strRomVersion = mDeviceInfoProvider.getDeviceRomVersion();
        if (!TextUtils.isEmpty(strRomeName)) {
            putJsonData(data,"romName", strRomeName);
        }
        if (!TextUtils.isEmpty(strRomVersion)) {
            putJsonData(data,"romVersion", strRomVersion);
        }

        //硬件信息
//        data.put("memory_total", memInfo[0]);
        putJsonData(data,"memory_total", getMemoryInfo().totalMemory);

//        data.put("memory_free", memInfo[0] - memInfo[4]);
        putJsonData(data,"memory_free", getMemoryInfo().freeMemory);

//        data.put("system_space_total", sdcardInfo[0]);
        putJsonData(data,"system_space_total", getSDCardInfo().totalSystemSpace);

//        data.put("system_space_free", sdcardInfo[1]);
        putJsonData(data,"system_space_free", getSDCardInfo().freeSystemSpace);

//        data.put("sd_total", sdcardInfo[2]);
        putJsonData(data,"sd_total", getSDCardInfo().totalExternalSpace);

//        data.put("sd_free", sdcardInfo[3]);
        putJsonData(data,"sd_free", getSDCardInfo().freeExternalSpace);

        NetSpeedBean speedBean = NetSpeedUtil.getNetSpeed(mContext.getApplicationInfo().uid);
        long rxBytes = speedBean.getRxSpeed();
        long txBytes = speedBean.getTxSpeed();
//        data.put("upload", traficBytes[2]);
        putJsonData(data,"upload", txBytes);

//        data.put("download", traficBytes[3]);
        putJsonData(data,"download", rxBytes);

//        data.put("AppVerCode", AdhocDeviceUtil.getPackageVerCode(mContext));
        putJsonData(data,"AppVerCode", mMdmAppInfoProvider.getAppVersionCode(mContext));


//        data.put("AppVerName", packageInfo == null ? "" : packageInfo.versionName);
        putJsonData(data,"AppVerName", mMdmAppInfoProvider.getAppVersionName(mContext));

//        data.put("AppSignedSys", AdhocDeviceUtil.getAppSignedSys());
        putJsonData(data,"AppSignedSys", mMdmAppInfoProvider.getAppSignedSys());

        putJsonData(data,"front_camera",mHasFrontCamera == null ? false : mHasFrontCamera);
        putJsonData(data,"back_camera",mHasBackCamera == null ? false : mHasBackCamera);

        return data;
    }


    private void putJsonData(JSONObject jsonObject, String key, Object data) throws JSONException {
        if (data == null) {
            return;
        }
        if (data instanceof CharSequence && TextUtils.isEmpty((CharSequence) data)) {
            return;
        }

        jsonObject.put(key, data);
    }

    private boolean isCameraFacingChanged(){
        IControl_CameraFacing cameraFacing = ControlFactory.getInstance().getControl(IControl_CameraFacing.class);
        if (cameraFacing != null) {
            boolean cameraChanged = false;
            Map<Integer, Boolean> facing = cameraFacing.getCameraFacing();
            if (facing == null) {
                return false;
            }
            Boolean hasBackCamera = facing.get(Camera.CameraInfo.CAMERA_FACING_BACK);
            if (hasBackCamera != mHasBackCamera){
                cameraChanged = true;
                mHasBackCamera = hasBackCamera;
            }
            Boolean hasFrontCamera = facing.get(Camera.CameraInfo.CAMERA_FACING_FRONT);
            if (hasFrontCamera != mHasFrontCamera){
                cameraChanged = true;
                mHasFrontCamera = hasFrontCamera;
            }
            return cameraChanged;
        }
        return false;
    }
}
