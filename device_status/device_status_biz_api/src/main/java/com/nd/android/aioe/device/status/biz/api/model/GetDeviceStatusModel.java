package com.nd.android.aioe.device.status.biz.api.model;

import com.nd.android.aioe.device.status.biz.api.constant.DeviceStatus;
import com.nd.android.aioe.device.status.dao.api.bean.GetDeviceStatusResult;

/**
 * {
 *  "errcode":0 //0=成功
 *  "status":1 //0=未知，1=入库，3=丢失,4=在用，5=故障，6=锁定，7=淘汰
 *  "login_auto":1 //1=自动登录，0=非自动登录(uc)
 *  "nick_name":"dddd" // 在用的情况下会返回NickName
 *  "jobnum":"120124" //工号
 *  "requestid":"xcxxxxxxxxxxxxxxxxxxxxxxxxxxx" //唯一标识，等于sessionid
 *  "need_group" : 1
 *  "groupcode":"0020"  //根节点的groupcode
 *  "nodecode":"009089" //所在的节点
 *  "nodename":"default" //所在的节点名称
 *  "delete_status":true     //true =设备被注销或删除（此时受控端如果是自动激活模式则不进入自动激活等待用户操作才进入），false 或这个值不存在=设备没有注销或删除（此时受控端如果是自动激活模式则开始自动激活）
 * }
 */
public class GetDeviceStatusModel extends GetDeviceStatusResult {

    public boolean isSuccess() {
        return getErrcode() == 0;
    }

    public DeviceStatus getDevicesStatus() {
        DeviceStatus deviceStatus = DeviceStatus.fromValue(getStatus());
        deviceStatus.setIsDeleted(isDelete_status());
        return deviceStatus;
    }

    public boolean isAutoLogin() {
        return getLogin_auto() == 1;
    }

    public boolean isNeedGroup() {
        return getNeed_group() == 1;
    }

}
