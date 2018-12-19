package com.nd.android.mdm.runinfo.business.task;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.util.AdhocDataCheckUtils;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.util.system.AdhocPackageUtil;
import com.nd.android.adhoc.basic.util.thread.AdhocRxJavaUtil;
import com.nd.android.adhoc.basic.util.thread.rx.AdhocActionSubscriber;
import com.nd.android.mdm.monitor.SystemControFactory;
import com.nd.android.mdm.runinfo.business.operator.AppRunInfoBizOperatorFactory;
import com.nd.android.mdm.runinfo.sdk.db.entity.AppRunInfoEntityHelper;
import com.nd.android.mdm.runinfo.sdk.db.entity.IAppRunInfoEntity;
import com.nd.android.mdm.runinfo.sdk.db.operator.AppRunInfoDbOperatorFactory;
import com.nd.android.mdm.runinfo.sdk.db.operator.IAppRunInfoDbOperator;
import com.nd.pad.systemapp.ISystemControl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by HuangYK on 2018/12/7.
 */

public class AppRunInfoTaskModule {

    private static final String TAG = "AppRunInfoTaskModule";

    private static final long ONE_HOUR = 3600000;
    private static final int MIN_EXECUTE_TIME = 30000;


    private volatile static AppRunInfoTaskModule sInstance = null;

    public static AppRunInfoTaskModule getInstance() {
        if (sInstance == null) {
            synchronized (AppRunInfoTaskModule.class) {
                if (sInstance == null) {
                    sInstance = new AppRunInfoTaskModule();
                }
            }
        }
        return sInstance;
    }


    private PublishSubject<AppRunInfoTaskParams> mAppRunInfoPublish = PublishSubject.create();
    private Subscription mAppRunInfoSub;


    public void init(@NonNull Context pContext) {
        ISystemControl control = SystemControFactory.getInstance().getSystemControl();
        // 接口为空，或 5.0 以上，直接返回
        if (control == null || Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        initAppExecutionCollectSub(pContext);
        initSubscription();
    }

    private void initAppExecutionCollectSub(Context pContext) {
        PackageInfo info = AdhocPackageUtil.getPackageInfo(pContext, "com.nd.sdp.demo");
        if (info == null || info.versionCode < 27) {
            return;
        }

        Observable.interval(10, TimeUnit.SECONDS)
                .onBackpressureBuffer()
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(TAG, "get activity info error: " + e.getMessage());
                    }

                    @Override
                    public void onNext(Long aLong) {
                        updateExecuteProcess();
                    }
                });
    }

    private Map<String, Long> mAppRunTimeMap = new ConcurrentHashMap<>();
    private long mLastUpdateTime;
    private long mLastHour;

    private void updateExecuteProcess() {
        try {
            ISystemControl control = SystemControFactory.getInstance().getSystemControl();
            // 接口为空，或 5.0 以上，直接返回
            if (control == null || Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return;
            }

            String strRunnApps = control.invokeMethod("getRunnApps", null);
            if (TextUtils.isEmpty(strRunnApps)) {
                return;
            }

            JSONObject jsonRunnAppsJson = new JSONObject(strRunnApps);
            JSONArray arrayProcess = jsonRunnAppsJson.getJSONArray("process");
            JSONArray arrayUid = jsonRunnAppsJson.getJSONArray("id");
            int length = arrayUid.length() < arrayProcess.length() ? arrayUid.length() : arrayProcess.length();


//            Calendar calendar = Calendar.getInstance();
//            long now = System.currentTimeMillis();
//            int hour = calendar.get(Calendar.HOUR_OF_DAY);
//            String jsonStr = control.invokeMethod("getRunnApps", null);
//            JSONObject jsonObject = new JSONObject(jsonStr);
//            JSONArray processArr = jsonObject.getJSONArray("process");
//            JSONArray uidArr = jsonObject.getJSONArray("id");
//            int len = uidArr.length() < processArr.length() ? uidArr.length() : processArr.length();

            Set<String> oldPackages = mAppRunTimeMap.keySet(); // 已有的运行数据
            Set<String> copyPackages = new HashSet<>(); // 已有的运行数据的copy
            copyPackages.addAll(oldPackages);

            Set<String> newPackages = new HashSet<>(); // 新增的数据


            Calendar curCalendar = Calendar.getInstance();
            long curTime = curCalendar.getTimeInMillis();
            int curHour = curCalendar.get(Calendar.HOUR_OF_DAY);

            // 遍历，判断是新增的，就添加一条数据
            for (int i = 0; i < length; i++) {
                String processName = arrayProcess.getString(i);
                int uid = arrayUid.getInt(i);

                // 如果是  xxxxxx:xxx 这种格式，标示子进程，不计算
                if (processName.lastIndexOf(":") != -1 || uid == 1000) {
                    // 过滤掉系统应用与子进程
                    continue;
                }

                // 原先不存在，认为是新增
                if (!copyPackages.contains(processName)) {
                    // 新增的
                    newPackages.add(processName);
//                    mOpenInfo.addRecord(processName, now);
                    mAppRunInfoPublish.onNext(new AppRunInfoTaskParams(processName, curTime, true));
                } else {
                    // 原先已有的就移除，剩下已经被关闭的包
                    copyPackages.remove(processName);
                }
            }

            // 遍历已关闭的包，去更新时间，然后从缓存中移除
            for (String packageName : copyPackages) {
//                mOpenInfo.updateRecord(packageName, mExecuteTime.get(packageName));
                mAppRunInfoPublish.onNext(new AppRunInfoTaskParams(packageName, mAppRunTimeMap.get(packageName), false));
                mAppRunTimeMap.remove(packageName);
            }

            // 把新增的 包 加到缓存中
            for (String packageName : newPackages) {
                mAppRunTimeMap.put(packageName, curTime);
            }

            // 过了一小时，更新一次时间，保存一次数据
            if (curHour != mLastHour) {
                mLastHour = curHour;
                curCalendar.set(Calendar.MINUTE, 0);
                curCalendar.set(Calendar.SECOND, 0);
                curCalendar.set(Calendar.MILLISECOND, 0);
                long firstSecond = curCalendar.getTimeInMillis();
                for (String packageName : oldPackages) {
//                    mOpenInfo.updateRecord(packageName, mExecuteTime.get(packageName));
                    mAppRunInfoPublish.onNext(new AppRunInfoTaskParams(packageName, mAppRunTimeMap.get(packageName), false));
                    mAppRunTimeMap.put(packageName, firstSecond);
                }
            }

            // 上报或记录
            if (curTime - mLastUpdateTime >= ONE_HOUR) {

                // 这里去通知服务端
                if (AppRunInfoBizOperatorFactory.getInstance().getAppRunInfoBizOperator().postAppRunInfo()) {
                    // post 成功
                    Logger.d(TAG, "post app run info success");
                } else {
                    // post 失败
                    for (String packageName : oldPackages) {
//                        mOpenInfo.updateRecord(packageName, mExecuteTime.get(packageName));
                        mAppRunInfoPublish.onNext(new AppRunInfoTaskParams(packageName, mAppRunTimeMap.get(packageName), false));
                    }
                    Logger.d(TAG, "post ap run info  failed");
                }
                for (String packageName : oldPackages) {
                    long firstSecond = System.currentTimeMillis();
                    firstSecond -= Calendar.getInstance(TimeZone.getDefault()).get(Calendar.SECOND);
                    mAppRunTimeMap.put(packageName, firstSecond);
                }
                mLastUpdateTime = curTime;
            }
        } catch (RemoteException | JSONException e) {
            e.printStackTrace();
        }
    }


    private void initSubscription() {
        AdhocRxJavaUtil.doUnsubscribe(mAppRunInfoSub);
        mAppRunInfoSub = mAppRunInfoPublish.buffer(3, TimeUnit.SECONDS, 10)
                .filter(new Func1<List<AppRunInfoTaskParams>, Boolean>() {
                    @Override
                    public Boolean call(List<AppRunInfoTaskParams> paramList) {
                        return !AdhocDataCheckUtils.isCollectionEmpty(paramList);
                    }
                })
                .onBackpressureBuffer()
                .map(new Func1<List<AppRunInfoTaskParams>, Void>() {
                    @Override
                    public Void call(List<AppRunInfoTaskParams> params) {
                        for (AppRunInfoTaskParams param : params) {
                            saveOrUpdateData(param.getPackageName(), param.getUpdateTime(), param.isNewInfo());
                        }
                        return null;
                    }
                })
                .subscribe(new AdhocActionSubscriber<>(
                        new Action1<Void>() {
                            @Override
                            public void call(Void aVoid) {

                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Logger.e(TAG, "saveOrUpdateData error: " + throwable);
                                // 异常之后重新订阅，防止异常之后就停止收集的动作了
                                initSubscription();
                            }
                        }
                ));
    }


    /**
     * @param packageName 包名
     * @param startTime   开始时间
     * @param addData     true:新增app运行记录  false:整点刷新保存旧数据
     */
    private void saveOrUpdateData(String packageName, long startTime, boolean addData) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int curHour = calendar.get(Calendar.HOUR_OF_DAY);
        long curTime = calendar.getTimeInMillis();

        long duration = curTime - startTime;

        if (!addData && duration <= MIN_EXECUTE_TIME) {
            // 运行时间太短不计算
            return;
        }

        long todayTime = getDateExactToTheDay(curTime);

        IAppRunInfoEntity runInfoEntity = getAppRunInfoDbOperator().getEntity(packageName, todayTime, curHour);

        if (runInfoEntity == null) {
            runInfoEntity =
                    AppRunInfoEntityHelper.newAppRunInfoEntity(
                            packageName,
                            curHour,
                            duration,
                            addData ? 0 : 1,
                            todayTime);
        } else {
            if (addData) {
                runInfoEntity.setCount(runInfoEntity.getCount() + 1);
            }
            runInfoEntity.setTime(runInfoEntity.getTime() + duration);
        }
        getAppRunInfoDbOperator().saveOrUpdateEntity(runInfoEntity);
    }

    private IAppRunInfoDbOperator getAppRunInfoDbOperator() {
        return AppRunInfoDbOperatorFactory.getInstance().getAppExecutionDbOperator();
    }


    private long getDateExactToTheDay(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}
