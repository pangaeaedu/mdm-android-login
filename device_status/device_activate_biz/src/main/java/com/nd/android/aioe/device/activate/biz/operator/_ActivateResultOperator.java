package com.nd.android.aioe.device.activate.biz.operator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nd.android.adhoc.basic.frame.factory.AdhocFrameFactory;
import com.nd.android.aioe.device.activate.biz.api.model.CheckActivateModel;
import com.nd.android.aioe.device.info.config.DeviceInfoSpConfig;
import com.nd.android.aioe.device.status.biz.api.provider.IDeviceStatusNotifier;

class _ActivateResultOperator {

    private static final String TAG = "DeviceActivateResultOperator";


    public static void operateActivateResult(@NonNull CheckActivateModel pModel) {

        saveLoginInfo(pModel.getUsername(), pModel.getNick_name());
        DeviceInfoSpConfig.saveUserID(pModel.getUserid());
//        notifyLogin(pModel.getUsername(), pModel.getNick_name());

        // 这个广播是从上面挪下来的，是给 OMO 用的，不知道当初为什么 上面已经有 登录的通知了，这里又要加一个 广播？
        // 反正现在统一都发，OMO 那边如果没注册 也不会收到，所以没有影响，否则 可能发出去以后，
//            DeviceActivateBroadcastUtils.sendActivateSuccessBroadcast();

        IDeviceStatusNotifier statusNotifier = (IDeviceStatusNotifier) AdhocFrameFactory.getInstance()
                .getAdhocRouter().build(IDeviceStatusNotifier.ROUTE_PATH).navigation();
        if (statusNotifier != null) {
            statusNotifier.notifyDeviceStatus(pModel.getDeviceStatus());
        }
    }


    private static void saveLoginInfo(String pUserName, String pNickName) {
        DeviceInfoSpConfig.saveAccountNum(pUserName);
        DeviceInfoSpConfig.addAccountNameToPreviousList(pUserName);
        DeviceInfoSpConfig.saveNickname(pNickName);
    }

}
