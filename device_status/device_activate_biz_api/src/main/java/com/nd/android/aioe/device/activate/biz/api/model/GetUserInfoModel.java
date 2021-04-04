package com.nd.android.aioe.device.activate.biz.api.model;


//        "requestid":"xxxxxxxxx"
//        "errcode":0
//        "msgcode": 10015 //当errcode=-1时，10015表示没找到
//        "nick_name":"老王"
//        "user_id":"xxxxxx"
//        "device_code": "11" //获取设备编码，需要移动端补齐位数
//        "groupname":"xxxxx" //组名
//        "groupcode":"0090" //组code
//        "school_name":"xxxx学校" //学校名称，如果有配置的话有

import com.nd.android.aioe.device.activate.dao.api.bean.GetUserInfoResult;

public class GetUserInfoModel extends GetUserInfoResult {

    public boolean isSuccess() {
        return getErrcode() == 0;
    }
}
