package com.nd.android.adhoc.login.processOptimization.login;

import android.util.Log;

import com.nd.android.adhoc.loginapi.ISchoolGroupCodeRetriever;

import java.util.concurrent.CountDownLatch;

public abstract class BaseSchoolGroupCodeRetriever implements ISchoolGroupCodeRetriever {
    private static final String TAG = "GroupCodeRetriever";
    private CountDownLatch mCountDownLatch = new CountDownLatch(1);
    private String mGroupCode = null;


    @Override
    public String retrieveGroupCode(String pRootCode) throws Exception {
        showUI(pRootCode);
        try{
            mCountDownLatch.await();
        }catch (Exception ignored){

        }
        return mGroupCode;
    }

    @Override
    public String onGroupNotFound(String pRootCode) throws Exception {
        groupNotFound(pRootCode);
        try{
            mCountDownLatch.await();
        }catch (Exception ignored){

        }
        return mGroupCode;
    }

    protected abstract void showUI(String pRootCode);

    protected abstract void groupNotFound(String pRootCode);

    protected void setGroupCode(String sGroupCode){
        Exception e = new Exception("this is a log");
        e.printStackTrace();

        Log.e(TAG, "setGroupCode:"+sGroupCode);
        mGroupCode = sGroupCode;
        mCountDownLatch.countDown();
    }

}
