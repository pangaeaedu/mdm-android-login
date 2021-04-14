package com.nd.android.aioe.device.status.biz.provider;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.frame.factory.AdhocFrameFactory;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.policy.api.provider.IAdhocPolicyLifeCycleProvider;
import com.nd.android.adhoc.warning.api.provider.IAdhocWarningLifeCycleProvider;
import com.nd.android.aioe.device.info.config.DeviceInfoSpConfig;
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

    static {
        sSingleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    static void setPushId(@NonNull final String pPushId) {

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
//                DeviceInfoManager.getInstance().notifyPushID(sPushsId);
                updatePolicy();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private static void updatePolicy() {
        IAdhocPolicyLifeCycleProvider policyLifeCycleProvider =
                (IAdhocPolicyLifeCycleProvider) AdhocFrameFactory.getInstance()
                        .getAdhocRouter().build(IAdhocPolicyLifeCycleProvider.ROUTE_PATH).navigation();
        if (policyLifeCycleProvider == null) {
            return;
        }
        policyLifeCycleProvider.updatePolicy();

        IAdhocWarningLifeCycleProvider warningLifeCycleProvider =
                (IAdhocWarningLifeCycleProvider) AdhocFrameFactory.getInstance()
                        .getAdhocRouter().build(IAdhocWarningLifeCycleProvider.ROUTE_PATH).navigation();
        if (warningLifeCycleProvider == null) {
            return;
        }
        warningLifeCycleProvider.updateWarning();
    }

}
