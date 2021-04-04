package com.nd.android.aioe.device.activate.biz.api.model;

import com.nd.android.aioe.device.activate.dao.api.bean.CheckActivateResult;
import com.nd.android.aioe.device.status.biz.api.constant.DeviceStatus;

/**
 *  "errcode":0   ////激活成功，-1=激活失败
 *     "msgcode" :"002" //失败原因,010=激活进行中，0030=自动激活失效
 *     "status":1 //成功会带上状态
 *      "nickname":"xxxxx" //成功会带上用户名
 *     "requestid":"xcxxxxxxxxxxxxxxxxxxxxxxxxxxx"   //唯一标识，等于sessionid
 */

public class CheckActivateModel extends CheckActivateResult {

    public boolean isSuccess() {
        return getErrcode() == 0;
    }

    public String getUsername() {
        return "";
    }

    public DeviceStatus getDeviceStatus() {
        return DeviceStatus.fromValue(getStatus());
    }

    public boolean isActivateStillProcessing() {
        return "010".equalsIgnoreCase(getMsgcode())
                || "020".equalsIgnoreCase(getMsgcode());
    }

    /**
     * 是否是 组（学校）不存在
     */
    public boolean isGroupNotFound() {
        return "060".equalsIgnoreCase(getMsgcode());
    }

}
