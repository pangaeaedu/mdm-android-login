package com.nd.android.adhoc.login.processOptimization;

public class UserAuthenticator {//extends BaseAuthenticator implements IUserAuthenticator {
//
//    private static final String TAG = "UserAuthenticator";
//
//    public UserAuthenticator(IDeviceStatusListener pProcessor) {
//        super(pProcessor);
//    }
//
//    public void logout() {
//        Logger.i("yhq", "logout");
//
//        if (!_clearData()) {
//            return;
//        }
//
//        ActivityStackManager.INSTANCE.closeAllActivitys();
//        enterLogoutUI();
//
//    }
//
//    @Override
//    public void clearData() {
//        _clearData();
//    }
//
//    private boolean _clearData(){
//        Logger.i("yhq", "_clearData");
//
//        IAdhocLoginStatusNotifier api = (IAdhocLoginStatusNotifier) AdhocFrameFactory.getInstance().getAdhocRouter()
//                .build(AdhocRouteConstant.PATH_LOGIN_STATUS_NOTIFIER).navigation();
//        if (api == null) {
//            Logger.w("yhq", "_clearData failed, IAdhocLoginStatusNotifier not found");
//            return false;
//        }
//
//        api.onLogout();
//
//        clearPolicy();
//        getConfig().clearData();
//
//        //登出的时候，不要清掉DeviceID。DeviceID只有在切换环境的时候才会被清理
//        DeviceInfoManager.getInstance().resetStatusAndPushIDSubject();
//        mDeviceStatusListener.onDeviceStatusChanged(DeviceStatus.Init);
//        return true;
//    }
//
//    private void enterLogoutUI() {
//        //TODO 嫩模情人杨亿万：这段代码是为了防止自动登录的情况下，后台注销会跳到账号登录页而存在，为临时策略，麻烦找机会改掉
//        if(LoginWayUtils.getIsAutoLogin()){
//            //非空即自动登录，不跳登录页了
//            Logger.i(TAG, "auto login way, do not need to jump to login activity");
//            return;
//        }
//        Context context = AdhocBasicConfig.getInstance().getAppContext();
//        AdhocFrameFactory.getInstance().getAdhocRouter().build(AdhocRouteConstant.PATH_AFTER_LOGOUT)
//                .navigation(context, new NavCallback() {
//                    @Override
//                    public void onInterrupt(@NonNull Postcard postcard) {
//                        super.onInterrupt(postcard);
//                    }
//
//                    @Override
//                    public void onLost(@NonNull Postcard postcard) {
//                        super.onLost(postcard);
//                    }
//
//                    @Override
//                    public void onArrival(@NonNull Postcard postcard) {
//                    }
//                });
//    }
//
//
//    public Observable<DeviceStatus> login(@NonNull final String pUserName,
//                                          @NonNull final String pPassword,
//                                          final String pValidationCode) {
//        Logger.i("yhq", "login");
//        final String deviceID = DeviceInfoManager.getInstance().getDeviceID();
//        if (TextUtils.isEmpty(deviceID)) {
//            return Observable.error(new DeviceIDNotSetException());
//        }
//
//        IPushModule module = MdmTransferFactory.getPushModel();
//        String pushID = module.getDeviceId();
//        if(!TextUtils.isEmpty(pushID)){
//            DeviceInfoManager.getInstance().notifyPushID(pushID);
//        }
//
//        Context context = AdhocBasicConfig.getInstance().getAppContext();
//        if (!AdhocNetworkUtil.isNetWrokAvaiable(context)) {
//            return Observable.error(new NetworkUnavailableException());
//        }
//
//        DeviceStatus status = DeviceInfoManager.getInstance().getCurrentStatus();
//        if (status == DeviceStatus.Init) {
//            return queryDeviceStatusThenLogin(deviceID, pUserName, pPassword);
//        }
//
//        if (TextUtils.isEmpty(pUserName) || TextUtils.isEmpty(pPassword)) {
//            return Observable.error(new LoginUserOrPwdEmptyException());
//        }
//
//        Logger.i("yhq", "direct login");
//        return getLogin().login(pUserName, pPassword, pValidationCode)
//                .flatMap(new Func1<IUserLoginResult, Observable<DeviceStatus>>() {
//                    @Override
//                    public Observable<DeviceStatus> call(IUserLoginResult pResult) {
//                        return activeUser(ActivateUserType.Uc, "","", pResult.getLoginToken());
//                    }
//                });
//    }
//
//    @Override
//    public Observable<DeviceStatus> login(@NonNull final String pRootCode, @NonNull String pSchoolCode) {
//        Log.e("yhq", "login");
//        final String deviceID = DeviceInfoManager.getInstance().getDeviceID();
//        if (TextUtils.isEmpty(deviceID)) {
//            return Observable.error(new DeviceIDNotSetException());
//        }
//
//        IPushModule module = MdmTransferFactory.getPushModel();
//        String pushID = module.getDeviceId();
//
//        if(!TextUtils.isEmpty(pushID)){
//            DeviceInfoManager.getInstance().notifyPushID(pushID);
//        }
//
//        Context context = AdhocBasicConfig.getInstance().getAppContext();
//        if (!AdhocNetworkUtil.isNetWrokAvaiable(context)) {
//            return Observable.error(new NetworkUnavailableException());
//        }
//
////        DeviceStatus status = DeviceInfoManager.getInstance().getCurrentStatus();
////        if (status == DeviceStatus.Init) {
//            return queryDeviceStatusThenLoginByCode(deviceID, pRootCode, pSchoolCode);
////        }
////        return null;
//    }
//
//    private Observable<DeviceStatus> queryDeviceStatusThenLogin(String pDeviceID,
//                                                                final String pUserName,
//                                                                final String pPassword){
//        Logger.i("yhq", "queryDeviceStatusThenLogin");
//        return queryDeviceStatusFromServer(pDeviceID)
//                .flatMap(new Func1<QueryDeviceStatusResponse, Observable<DeviceStatus>>() {
//                    @Override
//                    public Observable<DeviceStatus> call(final QueryDeviceStatusResponse pResponse) {
//                        if (pResponse.isAutoLogin() && pResponse.getStatus() == DeviceStatus.Enrolled) {
//                            return activeUser(ActivateUserType.AutoLogin,
//                                    pResponse.getSelSchoolGroupCode(),
//                                    pResponse.getRootCode(),"")
//                                    .flatMap(new Func1<DeviceStatus, Observable<DeviceStatus>>() {
//                                        @Override
//                                        public Observable<DeviceStatus> call(DeviceStatus pStatus) {
//                                            if (TextUtils.isEmpty(pResponse.getJobnum())) {
//                                                return Observable.error(new
//                                                        AutoLoginMeetUserLoginException(""));
//                                            } else {
//                                                return Observable.error(new
//                                                        AutoLoginMeetUserLoginException
//                                                        ("" + pResponse.getJobnum()));
//                                            }
//                                        }
//                                    });
//                        }
//
//                        return getLogin().login(pUserName, pPassword)
//                                .flatMap(new Func1<IUserLoginResult, Observable<DeviceStatus>>() {
//                                    @Override
//                                    public Observable<DeviceStatus> call(IUserLoginResult pResult) {
//                                        return activeUser(ActivateUserType.Uc, "","", pResult
//                                                .getLoginToken());
//                                    }
//                                });
//                    }
//                });
//    }
//
//    private Observable<DeviceStatus> queryDeviceStatusThenLoginByCode(String pDeviceID,
//                                                                final String pRootCode,
//                                                                final String pSchoolCode){
//        Log.e("yhq", "queryDeviceStatusThenLogin");
//        return queryDeviceStatusFromServer(pDeviceID)
//                .flatMap(new Func1<QueryDeviceStatusResponse, Observable<DeviceStatus>>() {
//                    @Override
//                    public Observable<DeviceStatus> call(final QueryDeviceStatusResponse pResponse) {
//
//                        if (pResponse.getStatus() == DeviceStatus.Activated) {
//                            return Observable.just(DeviceStatus.Activated);
//                        }
//
////                        if (pResponse.getStatus() == DeviceStatus.Enrolled) {
//                            return activeUser(ActivateUserType.AutoLogin,
//                                    pSchoolCode,
//                                    pRootCode, "")
//                                    .flatMap(new Func1<DeviceStatus, Observable<DeviceStatus>>() {
//                                        @Override
//                                        public Observable<DeviceStatus> call(DeviceStatus pStatus) {
//                                            return Observable.just(pStatus);
//                                        }
//                                    });
////                        }
//
//                    }
//                });
//    }

}
