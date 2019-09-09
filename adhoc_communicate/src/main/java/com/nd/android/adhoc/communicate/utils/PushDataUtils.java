package com.nd.android.adhoc.communicate.utils;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019/9/9 0009.
 */

public class PushDataUtils {

    private static Gson mGson = new GsonBuilder().create();
    //doRequest: msgid = d791d562-b1a9-4425-ba22-1cb4dce758a5,
    // content = {"action":"\/v1\/device\/cmdresult\/","header":"{}",
    // "content":"{\"cmd\":\"applist\",\"timestamp\":0,\"type\":1,
    // \"device_token\":\"V3a31ab4f5d0734f8c81d6835c5c11e37c\",
    // \"user_token\":\"V3a31ab4f5d0734f8c81d6835c5c11e37c\",
    // \"sessionid\":\"76a22d7c-db16-497d-8f10-4cad6ed8eed2\",\"errcode\":999,\"msgcode\":999}"}
    public static String getAction(String pContent){
        try {
            Map maps = (Map) JSON.parse(pContent);
            return (String) maps.get("action");
        }catch (Exception pE){
            pE.printStackTrace();
        }

        return "";
    }


    //{"content":"{\"device_token\":\"V3d3b64f69e19b4b5db6a01517394196c5\",
    // \"errcode\":0}","msgtype":1,"message_id":"d194c1b0-94b8-456c-a2b4-69424b14f412","code":200}
    public static String getMessageID(String pData){
        try {
            JSONObject jsonObject = new JSONObject(pData);
            return jsonObject.optString("message_id");
        }catch (Exception pE){
            pE.printStackTrace();
        }

        return "";
    }

    //{"code":200,"message_id":"d791d562-b1a9-4425-ba22-1cb4dce758a5","msgtype":1,
    // "content":"{\"errcode\":0,\"device_token\":\"V3a31ab4f5d0734f8c81d6835c5c11e37c\"}"}
    public static  String generateResponse(int pCode, String pMsgID, int pMsgType,
                                    int pErrorCode, String pDeviceToken){
        Map<String, Object> data = new HashMap<>();
        data.put("errcode", pErrorCode);
        data.put("device_token", pDeviceToken);

        Map<String, Object> msg = new HashMap<>();
        msg.put("code", pCode);
        msg.put("message_id", pMsgID);
        msg.put("msgtype", pMsgType);
        msg.put("content", data);

        return mGson.toJson(msg);
    }

}
