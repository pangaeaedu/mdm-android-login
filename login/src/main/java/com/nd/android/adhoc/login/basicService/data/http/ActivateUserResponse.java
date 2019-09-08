package com.nd.android.adhoc.login.basicService.data.http;

/**
 "errcode":0   //0=成功
 "result":"success"   //表示mdm收到请求，最终能否激活通过push通知
 "requestid":"xcxxxxxxxxxxxxxxxxxxxxxxxxxxx"   //唯一标识，等于sessionid
 */

public class ActivateUserResponse {
    private int errcode = 0;
    private String result = "";
    private String requestid = "";

    public boolean isSuccess(){
        if(errcode != 0){
            return false;
        }

//        if(!result.equalsIgnoreCase("success")){
//            return false;
//        }

        return true;
    }

    public void setErrcode(int pErrcode){
        errcode = pErrcode;
    }

    public void setResult(String pResult){
        result = pResult;
    }

    public String getResult(){
        return result;
    }

    public int getDelayTime(){
        try {
            return Integer.parseInt(result);
        }catch (Exception pE){

        }

        return 7200;

    }
    public int getErrcode(){
        return errcode;
    }

    public String getRequestid(){
        return requestid;
    }
}
