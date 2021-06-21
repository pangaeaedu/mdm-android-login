package com.nd.android.aioe.device.activate.biz.api.injection;

import androidx.annotation.NonNull;

import com.nd.android.adhoc.basic.log.Logger;
import com.nd.sdp.android.serviceloader.AnnotationServiceLoader;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;

public abstract class BaseSchoolGroupCodeRetriever implements ISchoolGroupCodeRetriever {

    private static final String TAG = "DeviceActivate";
    private final CountDownLatch mCountDownLatch = new CountDownLatch(1);
    private String mGroupCode = null;


    @NonNull
    @Override
    public String retrieveGroupCode(String pRootCode) throws Exception {

        Iterator<IGroupCodeRetrieverChecker> interceptors = AnnotationServiceLoader
                .load(IGroupCodeRetrieverChecker.class).iterator();
        while (interceptors.hasNext()) {
            IGroupCodeRetrieverChecker checker = interceptors.next();

            // 有一个条件不允许就不行，直接返回空
            if (!checker.checlAllow()) {
                Logger.w(TAG, "SchoolGroupCodeRetriever [" + checker.getClass().getCanonicalName() + "] checkAllow return false");
                return "";
            }
        }

        // 改为 小米control， 或者 上层业务自行去限制
//        boolean isXiaomi = AdhocRomFactory.getInstance().getRomStrategy() instanceof AdhocRomStrategy_Xiaomi;
//
//        Logger.d(TAG, "Settings.Global.DEVICE_PROVISIONED value = " + Settings.Global.getInt(AdhocBasicConfig.getInstance().getAppContext().getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 0));
//        while (isXiaomi) {
//            if (Settings.Global.getInt(AdhocBasicConfig.getInstance().getAppContext().getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 0) == 1) {
//                break;
//            }
//            Thread.sleep(3 * 1000);
//        }

        showUI(pRootCode);
        try {
            mCountDownLatch.await();
        } catch (Exception ignored) {

        }
        return mGroupCode;
    }

    @NonNull
    @Override
    public String onGroupNotFound(String pRootCode) throws Exception {
        groupNotFound(pRootCode);
        try {
            mCountDownLatch.await();
        } catch (Exception ignored) {

        }
        return mGroupCode;
    }

    protected abstract void showUI(String pRootCode);

    protected abstract void groupNotFound(String pRootCode);

    protected void setGroupCode(String sGroupCode) {
        Exception e = new Exception("this is a log");
        e.printStackTrace();

        Logger.i(TAG, "setGroupCode:" + sGroupCode);
        mGroupCode = sGroupCode;
        mCountDownLatch.countDown();
    }
}