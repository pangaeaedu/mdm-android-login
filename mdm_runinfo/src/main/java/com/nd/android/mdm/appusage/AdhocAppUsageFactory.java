package com.nd.android.mdm.appusage;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Build;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.net.dao.AdhocHttpDao;
import com.nd.android.adhoc.basic.net.exception.AdhocHttpException;
import com.nd.android.adhoc.basic.sp.ISharedPreferenceModel;
import com.nd.android.adhoc.basic.sp.SharedPreferenceFactory;
import com.nd.android.adhoc.basic.timing.AdhocTimingApi;
import com.nd.android.adhoc.basic.timing.AdhocTimingCallbackAbs;
import com.nd.android.adhoc.basic.timing.AdhocTimingParams;
import com.nd.android.adhoc.basic.util.app.AdhocPackageUtil;
import com.nd.android.adhoc.basic.util.thread.AdhocRxJavaUtil;
import com.nd.android.adhoc.control.define.IControl_AppUsage;
import com.nd.android.adhoc.reportAppRunInfoByDb.RunInfoReportResult;
import com.nd.android.aioe.device.info.cache.DeviceIdCache;
import com.nd.android.mdm.basic.ControlFactory;
import com.nd.android.mdm.biz.env.MdmEvnFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Created by HuangYK on 2020/7/14.
 */

public class AdhocAppUsageFactory {

    private static final String TAG = "AdhocAppUsageFactory";

    private static final String KEY_LAST_RESPONSE_TIME = "LAST_RESPONSE_TIME";

    private static Subscription sTimeSub;

    private static long sLastResponseTime;

    private static long sReportEndTime;

    public static void start() {
        sLastResponseTime = getLastResponseTime();

        // 如果从来都没有上报过，就把当前时间设置为最后上报时间，方便下次进行比对
        long curTime = System.currentTimeMillis();
        if (sLastResponseTime == 0 || sLastResponseTime > curTime) {
            setLastResponseTime(curTime);
        }

        startTimer();
    }

    public static void cancel() {
        AdhocRxJavaUtil.doUnsubscribe(sTimeSub);

//        sLastResponseTime = 0;
//        // 清除时间
//        ISharedPreferenceModel model = SharedPreferenceFactory.getInstance().getModel(AdhocBasicConfig.getInstance().getAppContext());
//        model.putLong(KEY_LAST_RESPONSE_TIME, 0);
    }

    private static final String APP_USAGE_TIMER_ID = "APP_USAGE_TIMER_ID";
    private static void randomReport(final JSONObject jsonObject, final long lCurTimeMs){
        AdhocTimingApi.removeTimingCallback(APP_USAGE_TIMER_ID);
        AdhocTimingApi.unRegisterTiming(AdhocBasicConfig.getInstance().getAppContext(), APP_USAGE_TIMER_ID);

        AdhocTimingParams beginTimingParams = new AdhocTimingParams(APP_USAGE_TIMER_ID, lCurTimeMs + getRandomMs());
        final AdhocTimingCallbackAbs callback = new AdhocTimingCallbackAbs(APP_USAGE_TIMER_ID) {
            @Override
            public void onTrigger() {
                String strTimeId = getTimingId();
                Logger.i(TAG, "timing " + strTimeId + " now calls back");

                try {
                    RunInfoReportResult result = new AdhocHttpDao(getHost()).postAction().post(generateServerUrl(),
                            RunInfoReportResult.class, jsonObject.toString());

                    int errorCode;
                    if (result == null) {
                        errorCode = -111;
                    } else {
                        errorCode = result.getMiErrorCode();
                    }

                    if (errorCode != 0) {
                        Logger.e(TAG, "report app usage failed: errorCode = " + errorCode);
                        //上报失败，重试随机上报
                        if(isTwoTimeTheSameDay(sReportEndTime, System.currentTimeMillis())){
                            randomReport(jsonObject, System.currentTimeMillis());
                            return;
                        }
                    }else {
                        //上报成功，写个日志，设置成功时间
                        Logger.i(TAG, "上报成功");
                        setLastResponseTime(sReportEndTime);
                    }
                } catch (AdhocHttpException e) {
                    Logger.e(TAG, "report app usage request error: " + e);
                }

                AdhocTimingApi.removeTimingCallback(strTimeId);
                //重新开始两分钟定时器
                startTimer();
            }
        };

        AdhocTimingApi.addTimingCallback(callback);
        AdhocTimingApi.registerTiming(AdhocBasicConfig.getInstance().getAppContext(), beginTimingParams);
    }

    private static void startTimer() {
        AdhocRxJavaUtil.doUnsubscribe(sTimeSub);
        sTimeSub = Observable.interval(0, 2, TimeUnit.MINUTES)
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        startTimer();
                        Logger.e(TAG, "report app usage request onError: " + e);
                    }

                    @Override
                    public void onNext(Long aLong) {
                        final long curTime = System.currentTimeMillis();
                        //如果上次上报成功时间与当前是同一天，那就不用干活了
                        if(isTwoTimeTheSameDay(sLastResponseTime, curTime)){
                            Logger.i(TAG, "the same day as last report day");
                            return;
                        }

                        Logger.i(TAG, "begin to collect app usages");
                        JSONArray runinfolist = getUsageStatsList(
                                AdhocBasicConfig.getInstance().getAppContext(),
                                sLastResponseTime,
                                curTime);
                        if (runinfolist == null) {
                            Logger.d(TAG, "runinfolist = null");
                            return;
                        }
                        Logger.d(TAG, "runinfolist.size " + runinfolist.length());

                        JSONObject jsonObject = generateRespJson(runinfolist);
                        if (TextUtils.isEmpty(jsonObject.toString())) {
                            // 整个数据都为空就不报了
                            return;
                        }
                        //先暂停后台统计定时器，待上报后恢复
                        AdhocRxJavaUtil.doUnsubscribe(sTimeSub);
                        sReportEndTime = curTime;
                        randomReport(jsonObject, curTime);
                    }
                });
    }

    //统计当天的应用使用时间
    @TargetApi(Build.VERSION_CODES.M)
    private static JSONArray getUsageStatsList(Context context, long pStartTime, long pEndTime) {
        UsageStatsManager m = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        if (m == null) {
            return null;
        }

        // 将开始时间设置为 当天的 00:00:00
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTimeInMillis(pStartTime);
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTimeInMillis(pEndTime);

        int startDay = startCalendar.get(Calendar.DAY_OF_YEAR);

        int endDay = endCalendar.get(Calendar.DAY_OF_YEAR);

        //最多只报30天，以防push数据包爆了
        if(endDay - startDay > 30){
            startCalendar.add(Calendar.DATE, endDay - startDay - 30);
            startDay = startCalendar.get(Calendar.DAY_OF_YEAR);
        }

        // 计算当前和开始上报时间差几天
        int days = endDay - startDay;
        if (days <= 0) {
            Logger.d(TAG, "getUsageStatsList: days <=0 , return null");
            return null;
        }

        long begintime = startCalendar.getTimeInMillis();

        final long oneDay = 24 * 60 * 60 * 1000; // 23:59:59 的时间
        JSONArray runinfolist = new JSONArray();

        for (int i = 1; i <= days; i++) {
            long oneDayEndTime = begintime + oneDay - 1000;

            List<UsageStats> usageStats = m.queryUsageStats(UsageStatsManager.INTERVAL_BEST, begintime, oneDayEndTime);
            List<AppInformation> informations = getAccurateDailyStatsList(context, usageStats, m, begintime, oneDayEndTime);

            IControl_AppUsage usage = ControlFactory.getInstance().getControl(IControl_AppUsage.class);
            JSONObject runinfoitem ;
            try {
                if (usage != null) {
                    String statsList = usage.getUsageStatsList(begintime, oneDayEndTime);
                    runinfoitem = new JSONObject(statsList);
                } else {
                    runinfoitem = new JSONObject();
                    runinfoitem.put("time", begintime);

                    JSONArray infoArray = new JSONArray();
                    for (AppInformation information : informations) {
                        Logger.d(TAG, "==========》AppInformation: " + information.getPackageName() + "《==========");

                        // 1、自身应用 不上报
                        // 2、时长小于3分钟 不上报
                        if (context.getPackageName().equals(information.getPackageName())
                                || information.getUsedTimebyDay() < 3 * 60 * 1000) {

                            Logger.d(TAG, "used time by day < 3 min, throw away...");
                            continue;
                        }

                        // 3、包信息取不到，或者是系统应用，不上报
                        PackageInfo packageInfo = AdhocPackageUtil.getPackageInfo(context, information.getPackageName());
                        if (packageInfo == null || (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                            Logger.d(TAG, "can not get package info, throw away...");
                            continue;
                        }

                        JSONObject infoItem = new JSONObject();
                        infoItem.put("runtime", information.getUsedTimebyDay());
                        infoItem.put("package", information.getPackageName());
                        infoItem.put("appname", information.getLabel());
                        infoItem.put("runcount", information.getTimes());
                        Logger.d(TAG, "put infoitem");

                        infoArray.put(infoItem);
                    }
                    runinfoitem.put("info", infoArray);
                }
            } catch (JSONException e) {
                Logger.e(TAG, "getUsageStatsList: make runinfolist error:" + e);
                return null;
            }
            runinfolist.put(runinfoitem);

            begintime += oneDay;
        }
        return runinfolist;

    }

    /**
     * 根据UsageEvents来对当天的操作次数和开机后运行时间来进行精确计算
     */
    @SuppressLint("NewApi")
    private static ArrayList<AppInformation> getAccurateDailyStatsList(Context context, List<UsageStats> result, UsageStatsManager m, long begintime, long now) {
        //针对每个packageName建立一个  使用信息
        HashMap<String, AppInformation> mapData = new HashMap<>();
        //得到包名
        for (UsageStats stats : result) {
            if (stats.getLastTimeUsed() > begintime && stats.getTotalTimeInForeground() > 0) {
                if (mapData.get(stats.getPackageName()) == null) {
                    AppInformation information = new AppInformation(stats, context);
                    //重置总运行时间  开机操作次数
                    information.setTimes(0);
                    information.setUsedTimebyDay(0);
                    mapData.put(stats.getPackageName(), information);
                }
            }
        }

        UsageEvents events = m.queryEvents(begintime, now);

        UsageEvents.Event e = new UsageEvents.Event();
        while (events.hasNextEvent()) {
            events.getNextEvent(e);
            String packageName = e.getPackageName();

            AppInformation information = mapData.get(packageName);
            if (information == null) {
                continue;
            }

            //这里在同时计算开机后的操作次数和运行时间，所以如果获取到的时间戳是昨天的话就得过滤掉 continue

            if (e.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                information.timesPlusPlus();
                if (e.getTimeStamp() < begintime) {
                    continue;
                }
                information.setTimeStampMoveToForeground(e.getTimeStamp());
            } else if (e.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                if (e.getTimeStamp() < begintime) {
                    continue;
                }
                information.setTimeStampMoveToBackGround(e.getTimeStamp());
                //当前应用是在昨天进入的前台，0点后转入了后台，所以会先得到MOVE_TO_BACKGROUND 的timeStamp
                if (information.getTimeStampMoveToForeground() < 0) {
                    //从今天开始计算即可
                    information.setTimeStampMoveToForeground(begintime);
                }
            }
            information.calculateRunningTime();
        }

        //再计算一次当前应用的运行时间，因为当前应用，最后得不到MOVE_TO_BACKGROUND 的timeStamp
        AppInformation information = mapData.get(context.getPackageName());
        if (information != null) {
            information.setTimeStampMoveToBackGround(now);
            information.calculateRunningTime();
        }

        return new ArrayList<>(mapData.values());
    }

    private static JSONObject generateRespJson(@NonNull JSONArray runinfolist) {
        JSONObject resp = new JSONObject();
        try {
            resp.put("cmd", "appruninfo");
            resp.put("device_token", DeviceIdCache.getDeviceId());
            resp.put("sessionid", java.util.UUID.randomUUID().toString());
            resp.put("timestamp", String.valueOf(System.currentTimeMillis()));

            JSONObject jsonData = new JSONObject();

            jsonData.put("runinfolist", runinfolist);
            resp.put("data", jsonData);

            return resp;
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        return resp;
    }

    private static String getHost() {
        String strHost = "";
        try {
            strHost = MdmEvnFactory.getInstance().getCurEnvironment().getUrl();
            if (TextUtils.isEmpty(strHost)) {
                strHost = "http://drms.dev.web.nd";
            }
        } catch (NullPointerException e) {
            strHost = "http://drms.dev.web.nd";
        }

        return strHost;
    }

    private static String generateServerUrl() {
        String strHost = getHost();
//        String strHost = "http://192.168.252.45:8080";
        StringBuilder sb = new StringBuilder(strHost);
        sb.append("/v1/device/appruninfo");

        return sb.toString();
    }

    private static boolean isTwoTimeTheSameDay(long lFirstTimeMs, long lSecondTimeMs){
        Calendar firstCal = Calendar.getInstance();
        firstCal.setTimeInMillis(lFirstTimeMs);

        Calendar secondCal = Calendar.getInstance();
        secondCal.setTimeInMillis(lSecondTimeMs);

        return firstCal.get(Calendar.DAY_OF_YEAR) == secondCal.get(Calendar.DAY_OF_YEAR);
    }

    private static long getRandomMs(){
        Random random = new Random(System.currentTimeMillis());
        /*随机3000秒内上报*/
        int RANDOM_MS_BOUND = 3000 * 1000;
        long lSecondsDelay = random.nextInt(RANDOM_MS_BOUND);
        Logger.i(TAG, "will report "+ lSecondsDelay + " milli seconds later");
        return lSecondsDelay;
    }

    private static long getLastResponseTime() {
        ISharedPreferenceModel model = SharedPreferenceFactory.getInstance().getModel(AdhocBasicConfig.getInstance().getAppContext());
        return model.getLong(KEY_LAST_RESPONSE_TIME, 0);
    }

    private static void setLastResponseTime(long pLastResponseTime) {
        sLastResponseTime = pLastResponseTime;
        ISharedPreferenceModel model = SharedPreferenceFactory.getInstance().getModel(AdhocBasicConfig.getInstance().getAppContext());
        model.putLong(KEY_LAST_RESPONSE_TIME, pLastResponseTime);
    }

}
