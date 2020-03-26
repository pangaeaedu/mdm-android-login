package com.nd.android.adhoc.reportAppRunInfoByDb;

import android.text.TextUtils;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceHelper;
import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.common.util.AdhocDataCheckUtils;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.net.dao.AdhocHttpDao;
import com.nd.android.adhoc.basic.net.exception.AdhocHttpException;
import com.nd.android.adhoc.db.entity.intfc.IMdmRunInfoEntity;
import com.nd.android.adhoc.db.operator.MdmRunInfoDbOperatorFactory;
import com.nd.android.mdm.biz.env.MdmEvnFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * @author Administrator
 * @name adhoc-login
 * @class name：com.nd.android.adhoc.reportAppRunInfoByDb
 * @class describe
 * @time 2019/8/30 15:04
 * @change
 * @chang time
 * @class describe
 */
public class RunInfoReportHelper {
    private static final String TAG = "RunInfoReportHelper";

    /**
     * 形成本次要上报的数据
     * @param listEntity
     * @return
     */
    private static JSONObject generateRespJson(final List<IMdmRunInfoEntity> listEntity){
        JSONObject resp = new JSONObject();
        try {
            resp.put("cmd", "appruninfo");
            resp.put("device_token", DeviceHelper.getDeviceToken());
            resp.put("sessionid", java.util.UUID.randomUUID().toString());
            resp.put("timestamp", String.valueOf(System.currentTimeMillis()));

            JSONObject jsonData = new JSONObject();

            JSONArray arrayRunInfo = new JSONArray();
            Map<Long, JSONArray> mapRunInfoList = getDayRunInfoMap(listEntity);
            for (Map.Entry<Long, JSONArray> entry : mapRunInfoList.entrySet()) {
                JSONObject objectRunInfo = new JSONObject();
                objectRunInfo.put("time", entry.getKey());
                objectRunInfo.put("info", entry.getValue());
                arrayRunInfo.put(objectRunInfo);
            }
            jsonData.put("runinfolist", arrayRunInfo);
            resp.put("data", jsonData);
            return resp;
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        return resp;
    }

    /**
     * 按天制造运行信息map
     * @param listEntity listEntity
     * @return
     */
    private static Map<Long, JSONArray> getDayRunInfoMap(final List<IMdmRunInfoEntity> listEntity){
        Map<Long, JSONArray> mapRet = new HashMap<>();
        try {
            for(IMdmRunInfoEntity entity : listEntity){
                if(!mapRet.containsKey(entity.getDayBeginTimeStamp())){
                    mapRet.put(entity.getDayBeginTimeStamp(), new JSONArray());
                }
                JSONObject objectApp = new JSONObject();
                objectApp.put("runtime", entity.getRunTime());
                objectApp.put("package", entity.getPackageName());
                objectApp.put("appname", entity.getAppName());
                objectApp.put("runcount", entity.getRunCount());
                mapRet.get(entity.getDayBeginTimeStamp()).put(objectApp);
            }
        }catch (JSONException e){
            e.printStackTrace();
            Logger.e(TAG, "getDayRunInfoMap ERR:"+e);
        }
        return mapRet;
    }

    private static String generateServerUrl(){
        String strHost = getHost();
//        String strHost = "http://192.168.252.45:8080";
        StringBuilder sb = new StringBuilder(strHost);
        sb.append("/v1/device/appruninfo");

        return sb.toString();
    }

    private static String getHost(){
        String strHost = "";
        try {
            strHost = MdmEvnFactory.getInstance().getCurEnvironment().getUrl();
            if(TextUtils.isEmpty(strHost)){
                strHost = "http://drms.dev.web.nd";
            }
        }catch (NullPointerException e){
            strHost = "http://drms.dev.web.nd";
        }

        return strHost;
    }

    public static void reportToServerBusiness(){
        List<IMdmRunInfoEntity> listEntity = MdmRunInfoDbOperatorFactory.getInstance().
                getRunInfoDbOperator().getToReportRunInfo();
        if(AdhocDataCheckUtils.isCollectionEmpty(listEntity)){
            Logger.i(TAG, "no app to report now");
            //没有可上报的数据了，删除当前时间戳之前的所有数据
            MdmRunInfoDbOperatorFactory.getInstance().
                    getRunInfoDbOperator().deleteUnUseableRunInfo();
            return;
        }

        reportToServer(generateRespJson(listEntity), listEntity);
    }

    private static void reportToServer(final JSONObject jsonData, final List<IMdmRunInfoEntity> listEntity){
        Logger.i(TAG, "call reportToServer");
        if(null == jsonData || 0 == jsonData.length()){
            Logger.i(TAG, "json empty");
            return;
        }

        Observable.create(new Observable.OnSubscribe<RunInfoReportResult>() {
            @Override
            public void call(Subscriber<? super RunInfoReportResult> subscriber) {
                try {
                    RunInfoReportResult result = new AdhocHttpDao(getHost()).postAction().post(generateServerUrl(),
                            RunInfoReportResult.class, jsonData.toString());
                    if (null == result || 0 != result.getMiErrorCode()) {
                        subscriber.onError(new AdhocException("report not success"));
                    }else {
                        Logger.i(TAG, "上报成功");
                        //成功了，清除需要补报的缓存
                        MdmRunInfoDbOperatorFactory.getInstance().getRunInfoDbOperator().deleteRunInfo(listEntity);
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    }
                } catch (AdhocHttpException e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<RunInfoReportResult>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(TAG, "上报失败"+e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(RunInfoReportResult result) {
                        //一次只上报1000条数据，如果还有的话，要继续上报，直到没有可上报的，并删除无用数据
                        Logger.i(TAG, "recursive call reportToServerBusiness");
                        reportToServerBusiness();
                    }
                });
    }
}
