package com.nd.android.mdm.monitor;

import android.Manifest;
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

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceHelper;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceInfoManager;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.net.dao.AdhocHttpDao;
import com.nd.android.adhoc.basic.util.app.AdhocPackageUtil;
import com.nd.android.adhoc.basic.util.net.AdhocNetworkUtil;
import com.nd.android.adhoc.basic.util.net.speed.NetSpeedBean;
import com.nd.android.adhoc.basic.util.net.speed.NetSpeedUtil;
import com.nd.android.adhoc.basic.util.root.AdhocNewRootUtils;
import com.nd.android.adhoc.basic.util.system.AdhocDeviceUtil;
import com.nd.android.adhoc.basic.util.thread.AdhocRxJavaUtil;
import com.nd.android.adhoc.basic.util.time.AdhocTimeUtil;
import com.nd.android.adhoc.command.basic.constant.AdhocCmdFromTo;
import com.nd.android.adhoc.command.basic.response.ResponseBase;
import com.nd.android.adhoc.control.define.IControl_AppList;
import com.nd.android.adhoc.control.define.IControl_CpuUsageRate;
import com.nd.android.adhoc.control.define.IControl_DeviceRomName;
import com.nd.android.adhoc.control.define.IControl_DeviceRomVersion;
import com.nd.android.mdm.basic.ControlFactory;
import com.nd.android.mdm.biz.env.MdmEvnFactory;
import com.nd.android.mdm.monitor.info.AdhocBatteryInfo;
import com.nd.android.mdm.monitor.info.AdhocCpuInfo;
import com.nd.android.mdm.monitor.info.AdhocMemoryInfo;
import com.nd.android.mdm.monitor.info.AdhocNetworkInfo;
import com.nd.android.mdm.monitor.info.AdhocSDCardInfo;
import com.nd.android.mdm.monitor.message.BatteryChangeMessage;
import com.nd.android.mdm.monitor.message.UsbAttachMessage;
import com.nd.android.mdm.wifi_sdk.business.MdmWifiInfoManager;
import com.nd.android.mdm.wifi_sdk.business.basic.constant.MdmWifiStatus;
import com.nd.android.mdm.wifi_sdk.business.basic.listener.IMdmWifiStatusChangeListener;
import com.nd.android.mdm.wifi_sdk.business.bean.MdmWifiInfo;
import com.nd.android.mdm.wifi_sdk.business.bean.MdmWifiVendor;
import com.nd.eci.sdk.utils.MonitorUtil;
import com.nd.screen.Screenshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
//        ILocationNavigation locationNavigation =
//                (ILocationNavigation) AdhocFrameFactory.getInstance().getAdhocRouter().build(ILocationNavigation.PATH).navigation();
//        if (locationNavigation != null) {
//            locationNavigation.addLocationListener(mLocationChangeListener);
//        }

//        MdmWifiInfoManager.getInstance().getWifiListenerManager().addInfoUpdateListener(
//                new IMdmWifiInfoUpdateListener() {
//                    @Override
//                    public void onInfoUpdated(MdmWifiInfo pWifiInfo) {
//                        responseDevInfo();
//                    }
//                }
//        );

        MdmWifiInfoManager.getInstance().getWifiListenerManager().addStatusChangeListener(
                new IMdmWifiStatusChangeListener() {
                    @Override
                    public void onWifiStatusChange(MdmWifiStatus pStatus) {
                        if(MdmWifiStatus.CONNECTED != pStatus){
                            return;
                        }

                        //会出现wifi connect事件到达的时候，deviceID还没有确认的情况，这种情况下，不要上报DevInfo
                        String deviceToken = DeviceInfoManager.getInstance().getDeviceID();
                        if(TextUtils.isEmpty(deviceToken)){
                            return;
                        }

                        responseDevInfo();
                    }
                }
        );
    }

    private void responseDevInfo() {
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
//                    responseBase.postAsync();
//                    HttpUtil.post(MdmEvnFactory.getInstance().getCurEnvironment().getUrl() + "/v1/device/deviceinfo", responseBase.toString());
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
//        ILocationNavigation locationNavigation =
//                (ILocationNavigation) AdhocFrameFactory.getInstance().getAdhocRouter().build(ILocationNavigation.PATH).navigation();
//        if (locationNavigation != null) {
//            locationNavigation.removeLocationListener(mLocationChangeListener);
//        }
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
//        boolean curCharging = intent.getIntExtra("status", -1) == BatteryManager.BATTERY_STATUS_CHARGING;
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean curCharging = plugged != -1 && plugged != 0;

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
            IControl_AppList control_appList = ControlFactory.getInstance().getControl(IControl_AppList.class);
            if(null != control_appList){
                return control_appList.getRunningAppList();
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
                return NetSpeedUtil.getNetSpeed(mContext.getApplicationInfo().uid).getTxSpeed();
            case IMonitor.DOWNLOAD:
                return NetSpeedUtil.getNetSpeed(mContext.getApplicationInfo().uid).getRxSpeed();
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
        IControl_CpuUsageRate control_cpuUsageRate = ControlFactory.getInstance().getControl(IControl_CpuUsageRate.class);
        if (control_cpuUsageRate != null) {
            return new AdhocCpuInfo((int)control_cpuUsageRate.getUsageRate());
        }else {
            return new AdhocCpuInfo(0);
        }
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
        putJsonData(data,"netType",  AdhocNetworkUtil.getNetWorkStateString());

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
        putJsonData(data,"battery", batteryLevel);

//        data.put("charge", batteryIsCharging);
        putJsonData(data,"charge", batteryIsCharging);

//        data.put("cpu_rate", (int) MonitorUtil.getCpuInfo()[8]);
        putJsonData(data,"cpu_rate", (int) MonitorUtil.getCpuInfo()[8]);

//        data.put("serialnum", DeviceHelper.getSerialNumberThroughControl());
        putJsonData(data,"serialnum", DeviceHelper.getSerialNumberThroughControl());

//        data.put("terminaltype", AdhocDeviceUtil.isTabletDevice(mContext) ? 2 : 1);
        putJsonData(data,"terminaltype", AdhocDeviceUtil.isTabletDevice(mContext) ? 2 : 1);

//        data.put("resolution", AdhocDeviceUtil.getRealScreenWidth() + "*" + AdhocDeviceUtil.getRealScreenHeight());
        putJsonData(data,"resolution", AdhocDeviceUtil.getRealScreenWidth() + "*" + AdhocDeviceUtil.getRealScreenHeight());

//        data.put("bluetoothmac", AdhocDeviceUtil.retrieveThenCacheBluetoothMacAddressViaReflection());
        putJsonData(data,"bluetoothmac", AdhocDeviceUtil.retrieveThenCacheBluetoothMacAddressViaReflection());

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
//            data.put("imei", ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
            putJsonData(data,"imei", ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
        }

        //系统、软件信息
//        data.put("sys_version", Build.VERSION.RELEASE);
        putJsonData(data,"sys_version", Build.VERSION.RELEASE);

//        data.put("language", Locale.getDefault().getDisplayLanguage());
        putJsonData(data,"language", Locale.getDefault().getDisplayLanguage());

//        data.put("isRooted", AdhocNewRootUtils.retrieveRootStatusViaExecuteSuCommand() ? 1 : 0);
        putJsonData(data,"isRooted", AdhocNewRootUtils.retrieveRootStatusViaExecuteSuCommand() ? 1 : 0);

//        data.put("panelId", DeviceHelper.getSerialNumberThroughControl());
        putJsonData(data,"panelId", DeviceHelper.getSerialNumberThroughControl());

        IControl_DeviceRomName control_deviceRomName = ControlFactory.getInstance().getControl(IControl_DeviceRomName.class);
        if (control_deviceRomName != null) {
//            data.put("romName", control_deviceRomName.getRomName());
            putJsonData(data,"romName", control_deviceRomName.getRomName());

        }
        IControl_DeviceRomVersion control_deviceRomVersion = ControlFactory.getInstance().getControl(IControl_DeviceRomVersion.class);
        if (control_deviceRomVersion != null) {
//            data.put("romVersion", control_deviceRomVersion.getRomVersion());
            putJsonData(data,"romVersion", control_deviceRomVersion.getRomVersion());

        }

        //硬件信息
        long[] memInfo = MonitorUtil.getMemoryInfo();
//        data.put("memory_total", memInfo[0]);
        putJsonData(data,"memory_total", memInfo[0]);

//        data.put("memory_free", memInfo[0] - memInfo[4]);
        putJsonData(data,"memory_free", memInfo[0] - memInfo[4]);

        long[] sdcardInfo = MonitorUtil.getSdcardInfo();
//        data.put("system_space_total", sdcardInfo[0]);
        putJsonData(data,"system_space_total", sdcardInfo[0]);

//        data.put("system_space_free", sdcardInfo[1]);
        putJsonData(data,"system_space_free", sdcardInfo[1]);

//        data.put("sd_total", sdcardInfo[2]);
        putJsonData(data,"sd_total", sdcardInfo[2]);

//        data.put("sd_free", sdcardInfo[3]);
        putJsonData(data,"sd_free", sdcardInfo[3]);

        NetSpeedBean speedBean = NetSpeedUtil.getNetSpeed(mContext.getApplicationInfo().uid);
        long rxBytes = speedBean.getRxSpeed();
        long txBytes = speedBean.getTxSpeed();
//        data.put("upload", traficBytes[2]);
        putJsonData(data,"upload", txBytes);

//        data.put("download", traficBytes[3]);
        putJsonData(data,"download", rxBytes);

//        data.put("AppVerCode", AdhocDeviceUtil.getPackageVerCode(mContext));
        putJsonData(data,"AppVerCode", AdhocDeviceUtil.getPackageVerCode(mContext));

        PackageInfo packageInfo = AdhocPackageUtil.getPackageInfo(mContext);
//        data.put("AppVerName", packageInfo == null ? "" : packageInfo.versionName);
        putJsonData(data,"AppVerName", packageInfo == null ? "" : packageInfo.versionName);

//        data.put("AppSignedSys", AdhocDeviceUtil.getAppSignedSys());
        putJsonData(data,"AppSignedSys", AdhocDeviceUtil.getAppSignedSys());
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

//    private ILocationChangeListener mLocationChangeListener = new ILocationChangeListener() {
//        @Override
//        public void onLocationChange(final ILocation location) {
//            AdhocRxJavaUtil.safeSubscribe(Observable.create(new Observable.OnSubscribe<Void>() {
//                @Override
//                public void call(Subscriber<? super Void> subscriber) {
//                    try {
//                        IResponse_MDM response_mdm = MdmResponseHelper.createResponseBase(
//                                "location",
//                                "",
//                                "",
//                                AdhocCmdFromTo.MDM_CMD_DRM.getValue(),
//                                System.currentTimeMillis());
//                        JSONObject data = new JSONObject();
//                        try {
//                            data.put("net_envrm", location.getNetEnv());
//                            data.put("maptype", location.getMapType());
//                            data.put("lat", location.getLat());
//                            data.put("lon", location.getLon());
//                            data.put("address", location.getAddress());
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        response_mdm.setJsonData(data).post();
//
//                        HttpUtil.post(MdmEvnFactory.getInstance().getCurEnvironment().getUrl() + "/v1/device/location", response_mdm.toString());
//                    } catch (Exception e) {
//                        Logger.e(TAG, "responseLocation, do response error: " + e.getMessage());
//                    }
//                    subscriber.onCompleted();
//                }
//            }).subscribeOn(Schedulers.io()));
//        }
//
//        @Override
//        public void onException(int errCode, String errStr) {
//
//        }
//    };
}
