package com.nd.android.mdm.monitor;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.usb.UsbManager;
import android.os.BatteryManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.frame.factory.AdhocFrameFactory;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.util.system.AdhocDeviceUtil;
import com.nd.android.adhoc.basic.util.system.AdhocPackageUtil;
import com.nd.android.adhoc.basic.util.time.AdhocTimeUtil;
import com.nd.android.adhoc.command.basic.response.ResponseBase;
import com.nd.android.adhoc.command.normal.response.ResponseLocation;
import com.nd.android.adhoc.communicate.constant.AdhocCmdFromTo;
import com.nd.android.adhoc.control.MdmControlFactory;
import com.nd.android.adhoc.control.define.IControl_DeviceInfo;
import com.nd.android.adhoc.location.ILocationNavigation;
import com.nd.android.adhoc.location.dataDefine.ILocation;
import com.nd.android.adhoc.location.locationCallBack.ILocationChangeListener;
import com.nd.android.mdm.monitor.info.AdhocBatteryInfo;
import com.nd.android.mdm.monitor.info.AdhocCpuInfo;
import com.nd.android.mdm.monitor.info.AdhocMemoryInfo;
import com.nd.android.mdm.monitor.info.AdhocNetworkInfo;
import com.nd.android.mdm.monitor.info.AdhocSDCardInfo;
import com.nd.android.mdm.monitor.message.BatteryChangeMessage;
import com.nd.android.mdm.monitor.message.UsbAttachMessage;
import com.nd.android.mdm.wifi_sdk.business.MdmWifiInfoManager;
import com.nd.android.mdm.wifi_sdk.business.basic.constant.MdmWifiStatus;
import com.nd.android.mdm.wifi_sdk.business.basic.listener.IMdmWifiInfoUpdateListener;
import com.nd.android.mdm.wifi_sdk.business.basic.listener.IMdmWifiStatusChangeListener;
import com.nd.android.mdm.wifi_sdk.business.bean.MdmWifiInfo;
import com.nd.android.mdm.wifi_sdk.business.bean.MdmWifiVendor;
import com.nd.eci.sdk.utils.MonitorUtil;
import com.nd.screen.Screenshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by yaoyue1019 on 9-12.
 */

public class MonitorModule implements IMonitor {

    private static final String TAG = "MonitorModule";


    public static final int TYPE_ALL_APP = 1;
    public static final int TYPE_RUNNING_APP = 2;
    private static final int UPDATE_MESSAGE_DELAY = 5000;
    private static MonitorModule instance;
//    private static final String[] VR_EQUIP = {"Gear VR"};
//    private static final int MESSAGE_CHECK_VR = 2;

    //    private UsbManager mUsbManager;
    private Context mContext;
    private boolean batteryIsCharging;
    private int batteryLevel;
//    private HandlerThread mBackgroundThread;
//    private Handler mBackgroundHandler;
//    private boolean vrWear = false;
//    private Map<String, Long> mExecuteTime;
//    private long lastUpdate = 0;
//    private long lastHour = 0;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            switch (action) {
                case Intent.ACTION_BATTERY_CHANGED:
                    updateBattery(intent);
                    break;
                case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                    usbAttached();
                    break;
                case UsbManager.ACTION_USB_DEVICE_DETACHED:
                    usbDetached();
                    break;
//                case ConnectivityManager.CONNECTIVITY_ACTION:
//                    ConnectivityManager manager =
//                            (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//                    NetworkInfo info = manager == null ? null : manager.getActiveNetworkInfo();
//                    if (info == null) {
//                        break;
//                    }
////                    mDeviceInfoEvent.notifyDeviceInfo(UUID.randomUUID().toString(), AdhocCmdFromTo.MDM_CMD_DRM.getValue());
//                    try {
//                        IResponse_MDM response =
//                                MdmResponseHelper.createResponseBase(
//                                        "postdeviceinfo",
//                                        "",
//                                        UUID.randomUUID().toString(),
//                                        AdhocCmdFromTo.MDM_CMD_DRM.getValue(),
//                                        System.currentTimeMillis());
//
//                        response.setJsonData(getDevInfoJson());
//                        response.post();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    break;
            }
        }
    };


    private MonitorModule() {
        ILocationNavigation locationNavigation =
                (ILocationNavigation) AdhocFrameFactory.getInstance().getAdhocRouter().build(ILocationNavigation.PATH).navigation();
        if (locationNavigation != null) {
            locationNavigation.addLocationListener(mLocationChangeListener);
        }

        MdmWifiInfoManager.getInstance().getWifiListenerManager().addInfoUpdateListener(
                new IMdmWifiInfoUpdateListener() {
                    @Override
                    public void onInfoUpdated(MdmWifiInfo pWifiInfo) {
                        try {
//                            IResponse_MDM response =
//                                    MdmResponseHelper.createResponseBase(
//                                            "postdeviceinfo",
//                                            "",
//                                            UUID.randomUUID().toString(),
//                                            AdhocCmdFromTo.MDM_CMD_DRM.getValue(),
//                                            System.currentTimeMillis());
//
//                            response.setJsonData(getDevInfoJson());
//                            response.post();

                            ResponseBase responseBase = new ResponseBase("postdeviceinfo",
                                    UUID.randomUUID().toString(),
                                    AdhocCmdFromTo.MDM_CMD_DRM.getValue(),
                                    "",
                                    System.currentTimeMillis());
                            responseBase.setJsonData(getDevInfoJson());
                            responseBase.postAsync();

                        } catch (JSONException e) {
                            Logger.e(TAG, "onInfoUpdated, do response error: " + e);
                        }
                    }
                }
        );

        MdmWifiInfoManager.getInstance().getWifiListenerManager().addStatusChangeListener(
                new IMdmWifiStatusChangeListener() {
                    @Override
                    public void onWifiStatusChange(MdmWifiStatus pStatus) {
                        if(MdmWifiStatus.CONNECTED != pStatus){
                            return;
                        }

                        try {
//                            IResponse_MDM response =
//                                    MdmResponseHelper.createResponseBase(
//                                            "postdeviceinfo",
//                                            "",
//                                            UUID.randomUUID().toString(),
//                                            AdhocCmdFromTo.MDM_CMD_DRM.getValue(),
//                                            System.currentTimeMillis());
//                            response.setJsonData(getDevInfoJson());
//                            response.post();

                            ResponseBase responseBase = new ResponseBase("postdeviceinfo",
                                    UUID.randomUUID().toString(),
                                    AdhocCmdFromTo.MDM_CMD_DRM.getValue(),
                                    "",
                                    System.currentTimeMillis());
                            responseBase.setJsonData(getDevInfoJson());
                            responseBase.postAsync();

                        } catch (JSONException e) {Logger.e(TAG, "onWifiStatusChange, do response error: " + e);
                        }

                    }
                }
        );
    }

    public static MonitorModule getInstance() {
        if (instance == null) {
            instance = new MonitorModule();
        }
        return instance;
    }

    public void init(Context pContext) {
//        MdmTransferFactory.getCommunicationModule().setDeviceInfoEvent(mDeviceInfoEvent);
//        MdmTransferFactory.getCommunicationModule().setConnectListener(mAdhocConnectListener);

        mContext = pContext.getApplicationContext();
//        mExecuteTime = new HashMap<>();
//        mBackgroundThread = new HandlerThread("monitorthread");
//        mBackgroundThread.start();
//        mBackgroundHandler = new Handler(mBackgroundThread.getLooper()) {
//            @Override
//            public void handleMessage(Message msg) {
//                switch (msg.what) {
//                    case MESSAGE_CHECK_VR:
//                        checkVR();
//                        break;
//                    default:
//                        Logger.w(TAG, "Monitor Module background handle message not defined:" + msg.what);
//                        break;
//                }
//            }
//        };
//        mUsbManager = ((UsbManager) mContext.getSystemService(Context.USB_SERVICE));
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
//        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mBroadcastReceiver, filter);
    }


    public void release() {
//        mBackgroundHandler.removeCallbacks(null);
//        mBackgroundThread.quit();
        ILocationNavigation locationNavigation =
                (ILocationNavigation) AdhocFrameFactory.getInstance().getAdhocRouter().build(ILocationNavigation.PATH).navigation();
        if (locationNavigation != null) {
            locationNavigation.removeLocationListener(mLocationChangeListener);
        }
        mContext.unregisterReceiver(mBroadcastReceiver);
    }


//    private void repeatPost(final String content, final int msg) {
//        mBackgroundHandler.removeMessages(msg);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    String resString = HttpUtil.post(MdmEvnFactory.getInstance().getCurEnvironment().getUrl() + "/v1/device/cmdresult/", content);
//                    if (AdhocTextUtil.isBlank(resString)) {
//                        return;
//                    }
//                    JSONObject res = new JSONObject(resString);
//                    int delay = res.isNull("timeRate") ? 0 : res.getInt("timeRate");
//                    if (!res.isNull("nextstep") && res.getString("nextstep").equals("true") && delay > 0) {
//                        mBackgroundHandler.sendMessageDelayed(mBackgroundHandler.obtainMessage(msg, delay, 0), delay);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }

//    private void checkVR() {
//        mBackgroundHandler.removeMessages(MESSAGE_CHECK_VR);
//        boolean wear = SystemProps.getHmtMounted();
//        if (vrWear != wear) {
//            new VRWearMessage().send();
//        }
//        vrWear = wear;
//        mBackgroundHandler.sendEmptyMessageDelayed(MESSAGE_CHECK_VR, 5000);
//    }

    private void writeToDB(int now) {
        Logger.d(TAG, "MonitorModule write openinfo to db");
    }

    // 可以只指定一个time 参数,但是这样每次都要多计算时间转化,不如统一一次计算了,再传进来使用


//    private void getTopActivity() {
//        Intent intent = new Intent();
//        String compName = null;
//        try {
//            ISystemControl control = SystemControFactory.getInstance().getSystemControl();
//            if (control != null) {
//                compName = control.invokeMethod("getRunnApps", intent);
//            }
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        if (compName != null) {
//            Logger.d(TAG,"top activity:" + compName);
//        } else {
//            Logger.d(TAG,"top activity not exist");
//        }
////        ActivityManager activityManager = (ActivityManager) mContext.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
////        List<ActivityManager.RunningTaskInfo> forGroundActivity = activityManager.getRunningTasks(1);
////        ActivityManager.RunningTaskInfo currentActivity;
////        currentActivity = forGroundActivity.get(0);
////        String activityName = currentActivity.topActivity.getClassName();
////        Logger.d(TAG,activityName);
//    }

    // 方法没有地方使用，暂时注释
//    private String getLocalIpAddress() {
//        try {
//            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
//                NetworkInterface intf = en.nextElement();
//                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
//                    InetAddress inetAddress = enumIpAddr.nextElement();
////这里需要注意：这里增加了一个限定条件( inetAddress instanceof Inet4Address ),主要是在Android4.0高版本中可能优先得到的是IPv6的地址。参考：http://blog.csdn.net/stormwy/article/details/8832164
//                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
//                        return inetAddress.getHostAddress().toString();
//                    }
//                }
//            }
//        } catch (SocketException ex) {
//        }
//        return null;
//    }

    private void usbAttached() {
        Logger.d(TAG,"usb attached:" + AdhocTimeUtil.getTimeStamp());
        new UsbAttachMessage(true).send();
    }

    private void usbDetached() {
        Logger.d(TAG,"usb detached:" + AdhocTimeUtil.getTimeStamp());
        new UsbAttachMessage(false).send();
    }

    private void updateBattery(Intent intent) {
        updateBattery(intent, false);
    }

    private void updateBattery(Intent intent, boolean forceUpdate) {
        int curLevel = intent.getIntExtra("level", -1);
        boolean curCharging = intent.getIntExtra("status", -1) == BatteryManager.BATTERY_STATUS_CHARGING;
        if (!((curLevel == batteryLevel) && (curCharging == batteryIsCharging)) || forceUpdate) {
            new BatteryChangeMessage(curLevel, curCharging, "").send();
        }
        batteryLevel = curLevel;
        batteryIsCharging = curCharging;
    }

//    private boolean isUsbAttached() {
//        if (mUsbManager != null) {
//            HashMap<String, UsbDevice> devices = mUsbManager.getDeviceList();
//            boolean attached = false;
//            for (String key : devices.keySet()) {
//                UsbDevice device = devices.get(key);
//                String c = "usb:" + device.toString();
//                // 添加vr设备需要添加这个list
//                for (int i = 0; i < VR_EQUIP.length; i++) {
//                    if (c.contains(VR_EQUIP[i])) {
//                        attached = true;
//                        break;
//                    }
//                }
//            }
//            return attached;
//        } else {
//            return false;
//        }
//    }

    @Override
    public List<PackageInfo> getApplications(int type) {
        List<PackageInfo> packageInfos = new ArrayList<PackageInfo>();
        if (type == TYPE_ALL_APP) {
            PackageManager pm = mContext.getPackageManager();
            if (pm != null) {
                packageInfos.addAll(pm.getInstalledPackages(0));
            }
        } else if (type == TYPE_RUNNING_APP) {
            ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningTasks = am.getRunningAppProcesses();
            List<String> packageNameList = new ArrayList<String>();
            for (int i = 0; i < runningTasks.size(); i++) {
                String packageName = runningTasks.get(i).processName.replaceAll("(:.+)?", "");
                if (!packageNameList.contains(packageName)) {
                    packageNameList.add(packageName);
                    PackageInfo packageInfo = AdhocPackageUtil.getPackageInfo(mContext, packageName);
                    if (packageInfo == null) {
                        Logger.w(TAG, String.format("monitor module getApplication: get package:%s result is null", packageName));
                    } else {
                        packageInfos.add(packageInfo);
                    }
                }
            }
        }
        return packageInfos;
    }

    @Override
    public AdhocBatteryInfo getBatteryInfo() {
        return new AdhocBatteryInfo(batteryLevel, batteryIsCharging);
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
                return getNetworkInfo().uploadSpeed;
            case IMonitor.DOWNLOAD:
                return getNetworkInfo().downloadSpeed;
            case IMonitor.SSID:
                return getNetworkInfo().ssid;
            case IMonitor.BATTERY:
                return getBatteryInfo().level;
            case IMonitor.CHARGE:
                return getBatteryInfo().isCharging;
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
        return new AdhocCpuInfo(MonitorUtil.getCpuInfo());
    }

    @Override
    public AdhocMemoryInfo getMemoryInfo() {
        return new AdhocMemoryInfo(MonitorUtil.getMemoryInfo());
    }

    @Override
    public AdhocNetworkInfo getNetworkInfo() {
        MdmWifiInfo wifiInfo = MdmWifiInfoManager.getInstance().getWifiInfo();
        AdhocNetworkInfo networkInfo = new AdhocNetworkInfo();
        networkInfo.ip = wifiInfo.getIp();
        networkInfo.ssid = wifiInfo.getSsid();
        networkInfo.rssi = wifiInfo.getRssi();
        networkInfo.linkSpeed = wifiInfo.getSpeed();
        networkInfo.BSSID = wifiInfo.getApMac();
        networkInfo.apMac = wifiInfo.getApMac();
        networkInfo.mac = wifiInfo.getMac().replace(":", "");
//            networkInfo.apFactory = WifiUtils.getVendorNameByMac(mContext, networkInfo.BSSID);

        // HYK Modified on 2018-04-12
        MdmWifiVendor vendor = wifiInfo.getVendor();
        networkInfo.apFactory = vendor == null ? "" : vendor.getVendorName();

        long[] traficBytes = MonitorUtil.getTraficByte();
        networkInfo.downloadSpeed = traficBytes[3];
        networkInfo.uploadSpeed = traficBytes[2];
        return networkInfo;
    }

    @Override
    public AdhocSDCardInfo getSDCardInfo() {
        return new AdhocSDCardInfo(MonitorUtil.getSdcardInfo());
    }

    public JSONObject getDevInfoJson() throws JSONException {
        JSONObject data = new JSONObject();

        MdmWifiInfo wifiInfo = MdmWifiInfoManager.getInstance().getWifiInfo();
        data.put("ip", wifiInfo.getIp());
        data.put("ssid", wifiInfo.getSsid());
        data.put("rssi", wifiInfo.getRssi());
        data.put("mac", wifiInfo.getMac().replace(":", ""));
        data.put("link_speed", wifiInfo.getSpeed());
        data.put("ap_mac", wifiInfo.getApMac());

        MdmWifiVendor vendor = wifiInfo.getVendor();
        data.put("ap_factory", vendor == null ? "" : vendor.getVendorName());


        data.put("model", Build.MODEL);
        data.put("sys_version", Build.VERSION.RELEASE);
        data.put("battery", batteryLevel);
        data.put("charge", batteryIsCharging);
        data.put("cpu_rate", (int) MonitorUtil.getCpuInfo()[8]);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            data.put("imei", ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
        }

        long[] memInfo = MonitorUtil.getMemoryInfo();
        data.put("memory_total", memInfo[0]);
        data.put("memory_free", memInfo[0] - memInfo[4]);

        long[] sdcardInfo = MonitorUtil.getSdcardInfo();
        data.put("system_space_total", sdcardInfo[0]);
        data.put("system_space_free", sdcardInfo[1]);
        data.put("sd_total", sdcardInfo[2]);
        data.put("sd_free", sdcardInfo[3]);


        long[] traficBytes = MonitorUtil.getTraficByte();
//        pDeviceInfo.upload = traficBytes[2];
//        pDeviceInfo.download = traficBytes[3];
//        pDeviceInfo.AppVerCode = AdhocDeviceUtil.getPackageVerCode(mContext);
        data.put("upload", traficBytes[2]);
        data.put("download", traficBytes[3]);
        data.put("AppVerCode", AdhocDeviceUtil.getPackageVerCode(mContext));

        PackageInfo packageInfo = AdhocPackageUtil.getPackageInfo(mContext);
        data.put("AppVerName", packageInfo == null ? "" : packageInfo.versionName);

        data.put("AppSignedSys", AdhocDeviceUtil.getAppSignedSys());


        IControl_DeviceInfo deviceInfo = MdmControlFactory.getInstance().getControl(IControl_DeviceInfo.class);
        if (null != deviceInfo) {
//            pDeviceInfo.strRmoName = deviceInfo.getRomName();
//            pDeviceInfo.RomVer = deviceInfo.getRomVersion();
//            pDeviceInfo.strPannelId = deviceInfo.getSerialNumber();

            data.put("romName", deviceInfo.getRomName());
            data.put("romVersion", deviceInfo.getRomVersion());
            data.put("panelId", deviceInfo.getSerialNumber());
        }

        return data;
    }

//    private IDeviceInfoEvent mDeviceInfoEvent = new IDeviceInfoEvent() {
//        @Override
//        public void notifyDeviceInfo(String pSessionId, int pFrom) {
//
//        }
//    };


//    private void initDeviceInfo(JSONObject pDeviceInfo) {
//        pDeviceInfo.battery = batteryLevel;
//        pDeviceInfo.isCharging = batteryIsCharging;
//        pDeviceInfo.cpu = (int) MonitorUtil.getCpuInfo()[8];
//        long[] memInfo = MonitorUtil.getMemoryInfo();
//        pDeviceInfo.totalMemory = memInfo[0];
//        pDeviceInfo.usedMemory = memInfo[4];
//        pDeviceInfo.androidVersion = Build.VERSION.RELEASE;
//        pDeviceInfo.model = Build.MODEL;
//        long[] sdcardInfo = MonitorUtil.getSdcardInfo();
//        pDeviceInfo.totalSystemSpace = sdcardInfo[0];
//        pDeviceInfo.freeSystemSpace = sdcardInfo[1];
//        pDeviceInfo.totalExternalSdcard = sdcardInfo[2];
//        pDeviceInfo.freeExternalSdcard = sdcardInfo[3];
//        long[] traficBytes = MonitorUtil.getTraficByte();
//        pDeviceInfo.upload = traficBytes[2];
//        pDeviceInfo.download = traficBytes[3];
//        pDeviceInfo.AppVerCode = AdhocDeviceUtil.getPackageVerCode(mContext);
//
//        PackageInfo packageInfo = AdhocPackageUtil.getPackageInfo(mContext);
//        pDeviceInfo.AppVerName = packageInfo == null ? "" : packageInfo.versionName;

//        pDeviceInfo.AppSignedSys = AdhocDeviceUtil.getAppSignedSys();


//        IControl_DeviceInfo deviceInfo = MdmControlFactory.getInstance().getControl(IControl_DeviceInfo.class);
//        if (null != deviceInfo) {
//            pDeviceInfo.strRmoName = deviceInfo.getRomName();
//            pDeviceInfo.RomVer = deviceInfo.getRomVersion();
//            pDeviceInfo.strPannelId = deviceInfo.getSerialNumber();
//        } else {
//            pDeviceInfo.RomVer = AdhocDeviceUtil.getND3RomVersion();
//        }
//
//        MdmWifiInfo wifiInfo = MdmWifiInfoManager.getInstance().getWifiInfo();
//        if (wifiInfo != null) {
//            pDeviceInfo.ip = wifiInfo.getIp();
//            pDeviceInfo.ssid = wifiInfo.getSsid();
//            pDeviceInfo.apMac = wifiInfo.getApMac();
//            pDeviceInfo.rssi = wifiInfo.getRssi();
//            pDeviceInfo.mac = wifiInfo.getMac().replace(":", "");
//            pDeviceInfo.linkSpeed = wifiInfo.getSpeed();
//            MdmWifiVendor vendor = wifiInfo.getVendor();
//            pDeviceInfo.apFactory = vendor == null ? "" : vendor.getVendorName();
//        }
//    }

//    private String getVendorNameByMac(String pBssid) {
//        IMdmWifiVendorEntity vendorEntity = MdmWifiDataServiceFactory.getInstance().getMdmWifiVendorDbService().getMdmWifiVendorEntity(pBssid);
//        return vendorEntity == null ? "" : vendorEntity.getVendorName();
//    }

//    private IAdhocConnectListener mAdhocConnectListener = new IAdhocConnectListener() {
//        @Override
//        public void onConnectionAvaialble() {
//            MdmTransferFactory.getCommunicationModule().sendLoginInfo(DeviceHelper.getDeviceToken());
//
//            AdhocMainLooper.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    AdhocToastModule.getInstance().showToast("adhoc on Connection Avaialble.");
//                }
//            });
//
//            String mac = AdhocNetworkIpUtil.getLocalMacAddressFromIp(mContext, AdhocNetworkIpUtil.getCurrentIp(mContext));
//            // 发送电量改变消息
//            new BatteryChangeMessage(batteryLevel, batteryIsCharging, mac).send();
////            // 发送手机型号
//            new ModelMessage(mac).send();
////            // 发送穿戴信息
////            new VRWearMessage(mac).send();
////            // 发送usb接入状况
////            new UsbAttachMessage(mac, isUsbAttached()).send();
////            // 发送是否支持辅助功能
////            new AccessibilityMessage(mac, isAccessibilitySettingsOn()).send();
//        }
//    };

    private ILocationChangeListener mLocationChangeListener = new ILocationChangeListener() {
        @Override
        public void onLocationChange(ILocation location) {
            ResponseLocation responseLocation = new ResponseLocation(
                    "",
                    AdhocCmdFromTo.MDM_CMD_DRM.getValue(),
                    mContext.getString(R.string.cmd_location),
                    System.currentTimeMillis());
            responseLocation.mapType = location.getMapType();
            responseLocation.netEnvr = location.getNetEnv();
            responseLocation.lat = location.getLat();
            responseLocation.lon = location.getLon();
            responseLocation.address = location.getAddress();

            responseLocation.post();
        }

        @Override
        public void onException(int errCode, String errStr) {

        }
    };
}
