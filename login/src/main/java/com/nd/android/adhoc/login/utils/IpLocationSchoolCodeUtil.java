package com.nd.android.adhoc.login.utils;


import android.content.Context;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.frame.factory.AdhocFrameFactory;
import com.nd.android.adhoc.basic.util.net.AdhocNetworkIpUtil;
import com.nd.android.adhoc.location.ILocationNavigation;
import com.nd.android.adhoc.location.dataDefine.ILocation;
import com.nd.android.adhoc.location.locationCallBack.ILocationChangeListener;
import com.nd.android.adhoc.login.basicService.http.school.IpLocationSchoolCodeDao;
import com.nd.android.adhoc.login.basicService.http.school.resp.IpLocationSchoolCodeResp;
import com.nd.android.mdm.biz.env.IMdmEnvModule;
import com.nd.android.mdm.biz.env.MdmEvnFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rx.Observable;
import rx.Subscriber;

public class IpLocationSchoolCodeUtil {

    private static ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static Observable<IpLocationSchoolCodeResp> getSchoolCodeByIP() {
        return Observable.create(new Observable.OnSubscribe<IpLocationSchoolCodeResp>() {
            @Override
            public void call(Subscriber<? super IpLocationSchoolCodeResp> pSubscriber) {
                try {
                    String ip = AdhocNetworkIpUtil.getCurrentIp(getContext());
                    if (TextUtils.isEmpty(ip)) {
                        pSubscriber.onError(new Exception("ip not found, is network connected"));
                        return;
                    }

                    IpLocationSchoolCodeDao dao = new IpLocationSchoolCodeDao(getBaseUrl());
                    IpLocationSchoolCodeResp resp = dao.getSchoolCodeByIp(ip);
                    pSubscriber.onNext(resp);
                    pSubscriber.onCompleted();
                } catch (Exception pE) {
                    pSubscriber.onError(pE);
                }
            }
        });
    }

    public static Observable<IpLocationSchoolCodeResp> getSchoolCodeByLocation(final int pRadius) {
        return Observable
                .create(new Observable.OnSubscribe<IpLocationSchoolCodeResp>() {
                    @Override
                    public void call(final Subscriber<? super IpLocationSchoolCodeResp> pSubscriber) {
                        try {
                            ILocationNavigation locationNavigation =
                                    (ILocationNavigation) AdhocFrameFactory.getInstance()
                                            .getAdhocRouter().build(ILocationNavigation.PATH).navigation();

                            if (locationNavigation == null) {
                                pSubscriber.onError(new Exception("location navigation not found"));
                                return;
                            }

                            locationNavigation.addLocationListener(new ILocationChangeListener() {
                                @Override
                                public void onLocationChange(final ILocation location) {
                                    executorService.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            IpLocationSchoolCodeDao dao = new IpLocationSchoolCodeDao(getBaseUrl());
                                            try {
                                                IpLocationSchoolCodeResp resp = dao
                                                        .getSchoolCodeByLocation("" + location.getLat(),
                                                                "" + location.getLon(), pRadius);
                                                pSubscriber.onNext(resp);
                                                pSubscriber.onCompleted();
                                            } catch (Exception pE) {
                                                pE.printStackTrace();
                                                pSubscriber.onError(pE);
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onException(int errCode, String errStr) {
                                    pSubscriber.onError(new Exception(errStr));
                                }
                            });
                        } catch (Exception pE) {
                            pSubscriber.onError(pE);
                        }

                    }
                });
    }

    private static Context getContext() {
        return AdhocBasicConfig.getInstance().getAppContext();
    }

    private static String getBaseUrl() {
        IMdmEnvModule module = MdmEvnFactory.getInstance().getCurEnvironment();
        return module.getUrl();
    }
}
