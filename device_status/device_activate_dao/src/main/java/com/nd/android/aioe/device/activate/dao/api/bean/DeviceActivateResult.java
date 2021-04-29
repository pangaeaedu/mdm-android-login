package com.nd.android.aioe.device.activate.dao.api.bean;


/**
 * 成功情况下返回的结构
 * {
 * "errcode":0   //0=成功   -1, 代表需要等待
 * "result":"success"   //表示mdm收到请求，最终能否激活通过push通知 ，如果放的是字符串类型的整型值，那就是等待时间，最大7200，代表7200秒
 * "requestid":"xcxxxxxxxxxxxxxxxxxxxxxxxxxxx"   //唯一标识，等于sessionid
 * }
 * <p>
 * <p>
 * 失败的情况下 返回的 结构
 * {"code":400,"message":"get groupCode is fail!","msgtype":1}
 */
public class DeviceActivateResult {

    // ===== 成功返回的 =====
    private int errcode = 0;
    private String result = "";
    private String requestid = "";


    // ===== 失败返回的 =====
    private int code;
    private String message;
    private int msgtype;

    public int getErrcode() {
        return errcode;
    }

    public String getResult() {
        return result;
    }

    public String getRequestid() {
        return requestid;
    }


    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getMsgtype() {
        return msgtype;
    }
}
