package com.nd.android.aioe.device.status.biz.provider;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.aioe.device.info.util.DeviceInfoHelper;
import com.nd.android.aioe.device.status.biz.api.model.GetDeviceStatusModel;
import com.nd.android.aioe.device.status.dao.api.IDeviceStatusDao;
import com.nd.android.aioe.device.status.dao.impl.DeviceStatusDaoHelper;
import com.nd.android.mdm.biz.env.MdmEvnFactory;

class _DeviceStatusGetter {

    private static final String TAG = "DeviceStatusOperator";

    @Nullable
    public static GetDeviceStatusModel queryDeviceStatusFromServer(@NonNull String pDeviceID) throws AdhocException {
        Logger.i(TAG, " start run queryDeviceStatusFromServer");
        String serialNum = DeviceInfoHelper.getSerialNumberThroughControl();

        if (TextUtils.isEmpty(pDeviceID) || TextUtils.isEmpty(serialNum)) {
            Logger.i(TAG, " queryDeviceStatusFromServer error 1");
            throw new AdhocException("queryDeviceStatusFromServer failed, serial number is empty");
        }

        GetDeviceStatusModel result = null;

        //这里增加的orgId非空的判断，是为了AP7设备激活时上报orgId这个逻辑能够正常走下去，并且不跳到选择组织的界面

        try {
            // 为什么查询状态还要关心  needgroup、是否自动激活？ 直接查询全部的信息回来就好了？
            result = getDeviceStatusDao().getDeviceStatus(GetDeviceStatusModel.class, pDeviceID, serialNum, 1);

//                if (ActivateConfig.getInstance().isAutoLogin() && TextUtils.isEmpty(sOrgId)) {
//                    Logger.i(TAG, " queryDeviceStatusFromServer isAutoLogin 1");
//                    // 自动登录的情况下，要把autoLogin的值1带上去
//                    Logger.d(TAG, "user auto login queryDeviceStatusFromServer:" + result.toString());
//
//                } else {
//                    result = getDeviceStatusDao().getDeviceStatus(GetDeviceStatusModel.class, pDeviceID, serialNum);
//                    Logger.i(TAG, "queryDeviceStatusFromServer, status: " + result.getStatus() + ", errcode: " + result.getErrcode());
//                }

        } catch (Exception e) {
            Logger.e(TAG, "queryDeviceStatus error 2 : " + e);
        }

        return result;

        // 到这里实际上就已经查询完毕了，剩下后面的逻辑都和查询状态没有什么鸟关系，目前迁移到 激活模块中处理，暂时先放着备用
//                if (status.isUnActivated()) {
//                    // 如果服务端状态是未登录的，但是本地还是登录的，那么这里需要清除一下本地的数据
//                    if (DeviceInfoSpConfig.isActivated()) {
//                        Logger.i(TAG, "queryDeviceStatusFromServer, server status is unActivated, but local status is activated, need clear local data ");
//                        IUserAuthenticator authenticator = AssistantAuthenticSystem.getInstance()
//                                .getUserAuthenticator();
//                        if (authenticator != null) {
//                            authenticator.clearData();
//                        }
//                    }
//                    // 封装了 通过注入 回调上层 去获取 schoolcode 的代码，改为以下写法 -- by hyk 20200318
//                    String schoolGroupCode = getSchoolCode(result.getRootCode(), false);
//                    // 因为切换用户的时候，retrieveGroupCode有可能为空，这种情况下，重新拉一遍设备状态
//                    // 如果状态是已登录的，就直接进去了，未登录的，还要再走一遍retrieveGroupCode
//                    if (TextUtils.isEmpty(schoolGroupCode)) {
//                        Logger.i("yhq", "school group code is empty");
//                        result = getDeviceStatusDao().getDeviceStatus(pDeviceID, serialNum,
//                                loginConfig.getAutoLogin(), loginConfig.getNeedGroup());
//                        status = result.getStatus();
//                        if (!DeviceStatus.isStatusUnLogin(status)) {
//                            //已登录，直接向下走，
//                            result.setSelSchoolGroupCode(schoolGroupCode);
//                            onQueryResultReturn(pSubscriber, result);
//                            return;
//                        }
//                        // 未登录，再弹出retrieveGroupCode界面，再获取一次groupCode
//                        schoolGroupCode = getSchoolCode(result.getRootCode(), false);
//                    }
//                    //偶发异常，强行杀进程
//                    if (schoolGroupCode.equalsIgnoreCase(result.getRootCode())) {
//                        Logger.d("yhq", "retrieveGroupCode not work root " +
//                                "code:" + result.getRootCode() + " selected:" + schoolGroupCode
//                                + " quit app");
//                        sendFailedAndQuitApp(120);
//                        return;
//                    }
//                    result.setSelSchoolGroupCode(schoolGroupCode);
//                    onQueryResultReturn(pSubscriber, result);
//                    return;
//                }
    }


    private static IDeviceStatusDao getDeviceStatusDao() {
        return DeviceStatusDaoHelper.getDeviceStatusDao(MdmEvnFactory.getInstance().getCurEnvironment().getUrl());
    }

//    private static String getOrgId() {
//        if (TextUtils.isEmpty(sOrgId)) {
//            sOrgId = DeviceInfoHelper.getOrgIdThroughControl();
//        }
//        return sOrgId;
//    }

}
