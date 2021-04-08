package com.nd.android.adhoc.login.utils;


public class IpLocationSchoolCodeUtil {
//
//    private static ExecutorService executorService = Executors.newSingleThreadExecutor();
//
//    public static Observable<IpLocationSchoolCodeResp> getSchoolCodeByIP(final String pRootCode) {
//        return Observable.create(new Observable.OnSubscribe<IpLocationSchoolCodeResp>() {
//            @Override
//            public void call(Subscriber<? super IpLocationSchoolCodeResp> pSubscriber) {
//                try {
//                    String ip = AdhocNetworkIpUtil.getCurrentIp(getContext());
//                    if (TextUtils.isEmpty(ip) || "0.0.0.0".equals(ip)) {
//                        pSubscriber.onError(new Exception("ip not found, is network connected"));
//                        return;
//                    }
//
//                    IpLocationSchoolCodeDao dao = new IpLocationSchoolCodeDao(getBaseUrl());
//                    IpLocationSchoolCodeResp resp = dao.getSchoolCodeByIp(pRootCode,ip);
//                    pSubscriber.onNext(resp);
//                    pSubscriber.onCompleted();
//                } catch (Exception pE) {
//                    pSubscriber.onError(pE);
//                }
//            }
//        });
//    }
//
//    public static Observable<IpLocationSchoolCodeResp> getSchoolCodeByLocation(final String pRootCode, final int pRadius) {
//        return Observable
//                .create(new Observable.OnSubscribe<IpLocationSchoolCodeResp>() {
//                    @Override
//                    public void call(final Subscriber<? super IpLocationSchoolCodeResp> pSubscriber) {
//                        final ILocationNavigation locationNavigation =
//                                (ILocationNavigation) AdhocFrameFactory.getInstance()
//                                        .getAdhocRouter().build(ILocationNavigation.PATH).navigation();
//
//                        if (locationNavigation == null) {
//                            pSubscriber.onError(new Exception("location navigation not found"));
//                            return;
//                        }
//
//                        final AtomicBoolean hasRun = new AtomicBoolean();
//                        locationNavigation.addLocationListener(new ILocationChangeListener() {
//                            @Override
//                            public void onLocationChange(final ILocation location) {
//
//                                if (hasRun.compareAndSet(false, true)) {
//                                    final ILocationChangeListener listener = this;
//                                    new Thread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            if (location == null) {
//                                                pSubscriber.onError(new Exception("checkSchoolExitByLocation failed, location result is null"));
//                                                return;
//                                            }
//
//                                            try {
//                                                IpLocationSchoolCodeDao dao = new IpLocationSchoolCodeDao(getBaseUrl());
//                                                IpLocationSchoolCodeResp resp = dao
//                                                        .getSchoolCodeByLocation(pRootCode, "" + location.getLat(),
//                                                                "" + location.getLon(), location.getMapType(), pRadius);
//
//                                                locationNavigation.removeLocationListener(listener);
//
//                                                pSubscriber.onNext(resp);
//                                                pSubscriber.onCompleted();
//
//                                            } catch (Exception pE) {
//                                                locationNavigation.removeLocationListener(listener);
//                                                pSubscriber.onError(pE);
//                                            }
//                                        }
//                                    }).start();
//                                }
//                            }
//
//                            @Override
//                            public void onException(int errCode, String errStr) {
//                                if (hasRun.compareAndSet(false, true)) {
//                                    locationNavigation.removeLocationListener(this);
//                                    pSubscriber.onError(new Exception(errStr));
//                                }
//                            }
//                        });
//
//                    }
//                });
//    }
//
//    public static Observable<CheckSchoolExitResult> checkSchoolExitByIp(@NonNull final String groupcode) {
//        return Observable.create(new Observable.OnSubscribe<CheckSchoolExitResult>() {
//            @Override
//            public void call(Subscriber<? super CheckSchoolExitResult> subscriber) {
//
//                String ip = AdhocNetworkIpUtil.getCurrentIp(getContext());
//                if (TextUtils.isEmpty(ip) || "0.0.0.0".equals(ip)) {
//                    subscriber.onError(new Exception("checkSchoolExitByIp cannot work, ip not found"));
//                    return;
//                }
//
//                try {
//                    CheckSchoolExitResult result = new IpLocationSchoolCodeDao(getBaseUrl()).checkSchoolExit(groupcode, ip);
//                    subscriber.onNext(result);
//                    subscriber.onCompleted();
//                } catch (Exception e) {
//                    subscriber.onError(e);
//                }
//            }
//        });
//    }
//
//    public static Observable<CheckSchoolExitResult> checkSchoolExitByLocation(@NonNull final String groupcode, @NonNull final int pScope) {
//
//        return Observable.create(new Observable.OnSubscribe<CheckSchoolExitResult>() {
//            @Override
//            public void call(final Subscriber<? super CheckSchoolExitResult> subscriber) {
//
//                final ILocationNavigation locationNavigation =
//                        (ILocationNavigation) AdhocFrameFactory.getInstance()
//                                .getAdhocRouter().build(ILocationNavigation.PATH).navigation();
//
//                if (locationNavigation == null) {
//                    subscriber.onError(new Exception("location navigation not found"));
//                    return;
//                }
//
//
//                final AtomicBoolean hasRun = new AtomicBoolean();
//                locationNavigation.addLocationListener(new ILocationChangeListener() {
//                    @Override
//                    public void onLocationChange(final ILocation location) {
//
//                        if (hasRun.compareAndSet(false, true)) {
//
//                            final ILocationChangeListener listener = this;
//
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if (location == null) {
//                                        subscriber.onError(new Exception("checkSchoolExitByLocation failed, location result is null"));
//                                        return;
//                                    }
//
//                                    try {
//                                        CheckSchoolExitResult result =
//                                                new IpLocationSchoolCodeDao(getBaseUrl())
//                                                        .checkSchoolExit(groupcode,
//                                                        String.valueOf(location.getLat()),
//                                                        String.valueOf(location.getLon()),
//                                                                location.getMapType(),
//                                                                pScope);
//
//                                        locationNavigation.removeLocationListener(listener);
//                                        subscriber.onNext(result);
//                                        subscriber.onCompleted();
//                                    } catch (Exception e) {
//                                        locationNavigation.removeLocationListener(listener);
//                                        subscriber.onError(e);
//                                    }
//                                }
//                            }).start();
//                        }
//                    }
//
//                    @Override
//                    public void onException(int errCode, String errStr) {
//                        if (hasRun.compareAndSet(false, true)) {
//                            locationNavigation.removeLocationListener(this);
//                            subscriber.onError(new Exception(errStr));
//                        }
//                    }
//                });
//
//            }
//        });
//    }
//
//    private static Context getContext() {
//        return AdhocBasicConfig.getInstance().getAppContext();
//    }
//
//    private static String getBaseUrl() {
//        IMdmEnvModule module = MdmEvnFactory.getInstance().getCurEnvironment();
//        return module.getUrl();
//    }
}
