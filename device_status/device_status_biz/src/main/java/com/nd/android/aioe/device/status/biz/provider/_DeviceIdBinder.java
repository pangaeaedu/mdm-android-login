package com.nd.android.aioe.device.status.biz.provider;

import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.communicate.impl.MdmTransferFactory;
import com.nd.android.adhoc.communicate.push.listener.IAdhocPushConnectListener;
import com.nd.android.aioe.device.info.config.DeviceInfoSpConfig;
import com.nd.android.aioe.device.status.biz.api.cache.DeviceStatusCache;
import com.nd.android.aioe.device.status.biz.api.model.BindDeviceIDWithPushIDModel;
import com.nd.android.aioe.device.status.dao.api.IDeviceIdDao;
import com.nd.android.aioe.device.status.dao.impl.DeviceStatusDaoHelper;
import com.nd.android.aioe.device.status.dao.impl.constant.DeviceType;
import com.nd.android.mdm.biz.env.MdmEvnFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class _DeviceIdBinder {

    private static final String TAG = "DeviceStatus";

    private static String sPushsId;

    private static String sDeviceId;


    private static final ExecutorService sSingleThreadExecutor;

    private static final IAdhocPushConnectListener sPushConnectListener = new IAdhocPushConnectListener() {
        @Override
        public void onPushDeviceToken(String deviceToken) {
            Logger.i(TAG, "sPushConnectListener, onPushDeviceToken ");
            Logger.d(TAG, "sPushConnectListener, onPushDeviceToken: " + deviceToken);

            onConnected();
        }

        @Override
        public void onConnected() {
            Logger.i(TAG, "_DeviceIdBinder, sPushConnectListener onConnected");

            // 绑定 PushId
            setPushId(MdmTransferFactory.getPushModel().getDeviceId());

            long lastUpdateTime = DeviceStatusCache.getLastUpdateTime();

            // 如果距离最后一次检测成功的时间 >= 24小时，就检测一遍状态
            if (Math.abs(System.currentTimeMillis() - lastUpdateTime) >= 24 * 60 * 60 * 1000) {
                Logger.e(TAG, "The current time is one day away from the last update time, recheck device status");

                _DeviceStatusChecker.checkDeviceStatusFromServer(DeviceInfoSpConfig.getDeviceID());
            }

        }

        @Override
        public void onDisconnected() {

        }
    };

    static {
        sSingleThreadExecutor = Executors.newSingleThreadExecutor();

        MdmTransferFactory.getPushModel().addConnectListener(sPushConnectListener);

        if (MdmTransferFactory.getPushModel().isConnected()) {
            sPushConnectListener.onConnected();
        }
    }

    static void setPushId(@NonNull final String pPushId) {
        Logger.i(TAG, "_DeviceIdBinder, setPushId");

        sSingleThreadExecutor.submit(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(sPushsId)) {
                    sPushsId = pPushId;
                }

                if (!TextUtils.isEmpty(sDeviceId)) {
                    doBindId();
                }
            }
        });
    }

    static void setDeviceId(@NonNull final String pDeviceId) {
        Logger.i(TAG, "_DeviceIdBinder, setDeviceId");

        sSingleThreadExecutor.submit(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(sDeviceId)) {
                    sDeviceId = pDeviceId;
                }

                if (!TextUtils.isEmpty(sPushsId)) {
                    doBindId();
                }
            }
        });
    }


    private static void doBindId() {
        Logger.i(TAG, "_DeviceIdBinder, doBindId");

        if (TextUtils.isEmpty(sPushsId) || TextUtils.isEmpty(sDeviceId)) {
            Logger.w(TAG, "DeviceIdPushIdBinder, doBindId not work, pushid or device is empty");
            return;
        }

        IDeviceIdDao deviceIdDao = DeviceStatusDaoHelper.getDeviceIdDao(MdmEvnFactory.getInstance().getCurEnvironment().getUrl());

        try {
            BindDeviceIDWithPushIDModel model = deviceIdDao.bindDeviceIDWithPushID(BindDeviceIDWithPushIDModel.class, sDeviceId, DeviceType.getValue(), sPushsId);

            if (model.isSuccess() && model.isDeviceTokenNotFound()) {
                Logger.d(TAG, "DeviceIdPushIdBinder, doBindId completed:" + sPushsId);

                DeviceInfoSpConfig.savePushID(sPushsId);
            }

        } catch (Exception e) {
            Logger.i(TAG, "_DeviceIdBinder, bindDeviceIDWithPushID error: " + e);
            // TODO id 绑定失败，是否需要重试
        }

    }

}
