package com.nd.android.adhoc.login.processOptimization.login;

import android.provider.Settings;
import android.util.Log;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.util.system.rom.AdhocRomFactory;
import com.nd.android.adhoc.control.xiaomi.rom.AdhocRomStrategy_Xiaomi;
import com.nd.android.adhoc.loginapi.ISchoolGroupCodeRetriever;

import java.util.concurrent.CountDownLatch;

public abstract class BaseSchoolGroupCodeRetriever implements ISchoolGroupCodeRetriever {
    private static final String TAG = "GroupCodeRetriever";
    private CountDownLatch mCountDownLatch = new CountDownLatch(1);
    private String mGroupCode = null;

    @Override
    public String retrieveGroupCode(String pRootCode) throws Exception {
        boolean isXiaomi = AdhocRomFactory.getInstance().getRomStrategy() instanceof AdhocRomStrategy_Xiaomi;

        Logger.d(TAG, "Settings.Global.DEVICE_PROVISIONED value = " + Settings.Global.getInt(AdhocBasicConfig.getInstance().getAppContext().getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 0));
        while (isXiaomi) {
            if (Settings.Global.getInt(AdhocBasicConfig.getInstance().getAppContext().getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 0) == 1) {
                break;
            }
            Thread.sleep(3 * 1000);
        }

        showUI(pRootCode);
        try {
            mCountDownLatch.await();
        } catch (Exception ignored) {

        }
        return mGroupCode;
    }

    protected abstract void showUI(String pRootCode);

    protected void setGroupCode(String sGroupCode) {
        Exception e = new Exception("this is a log");
        e.printStackTrace();

        Log.e(TAG, "setGroupCode:" + sGroupCode);
        mGroupCode = sGroupCode;
        mCountDownLatch.countDown();
    }

}
