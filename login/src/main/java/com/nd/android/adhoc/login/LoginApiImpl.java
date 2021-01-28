package com.nd.android.adhoc.login;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceInfoManager;
import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;
import com.nd.android.adhoc.basic.frame.factory.AdhocFrameFactory;
import com.nd.android.adhoc.basic.log.CrashAnalytics;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.login.basicService.data.http.GetUserInfoResponse;
import com.nd.android.adhoc.login.exception.GetUserInfoServerException;
import com.nd.android.adhoc.login.processOptimization.AssistantAuthenticSystem;
import com.nd.android.adhoc.login.processOptimization.BaseAbilityProvider;
import com.nd.android.adhoc.login.processOptimization.IDeviceInitiator;
import com.nd.android.adhoc.login.processOptimization.IUserAuthenticator;
import com.nd.android.adhoc.loginapi.ILoginApi;
import com.nd.android.adhoc.loginapi.LoginApiRoutePathConstants;
import com.nd.android.adhoc.loginapi.UserInfo;
import com.nd.android.adhoc.loginapi.exception.AutoLoginMeetUserLoginException;
import com.nd.android.adhoc.router_api.facade.Postcard;
import com.nd.android.adhoc.router_api.facade.annotation.Route;
import com.nd.android.adhoc.router_api.facade.callback.NavCallback;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

@Route(path = LoginRoutePathConstants.PATH_LOGIN_LOGIN)
public class LoginApiImpl extends BaseAbilityProvider implements ILoginApi {
    private static final String TAG = "LoginApiImpl";

    @Override
    public void enterLoginUI(@NonNull Context pContext) {
        AdhocFrameFactory.getInstance().getAdhocRouter()
                .build(LoginApiRoutePathConstants.PATH_LOGINAPI_LOGINUI)
                .navigation(pContext, new NavCallback() {
                    @Override
                    public void onInterrupt(@NonNull Postcard postcard) {
                        super.onInterrupt(postcard);
                        Logger.w(TAG, "onInterrupt");
                    }

                    @Override
                    public void onLost(@NonNull Postcard postcard) {
                        super.onLost(postcard);
                        Logger.e(TAG, "onLost");
                    }

                    @Override
                    public void onArrival(@NonNull Postcard postcard) {
                    }
                });
    }

    @Override
    public void logout() {
        IUserAuthenticator authenticator = AssistantAuthenticSystem.getInstance()
                .getUserAuthenticator();
        if (authenticator == null) {
            return;
        }

        authenticator.logout();
    }

    @Override
    public void clearData() {
        IUserAuthenticator authenticator = AssistantAuthenticSystem.getInstance()
                .getUserAuthenticator();
        if (authenticator == null) {
            return;
        }

        authenticator.clearData();
    }

    @Override
    public Observable<DeviceStatus> login(@NonNull final String pUserName, @NonNull final String
            pPassword, final String pValidationCode) {
        //如果device id没有设置上去，说明初始化没有完成，则要先走一次初始化的动作
        if (TextUtils.isEmpty(DeviceInfoManager.getInstance().getDeviceID())) {
            IDeviceInitiator initiator = AssistantAuthenticSystem.getInstance()
                    .getDeviceInitiator();

            return initiator.init()
                    .flatMap(new Func1<DeviceStatus, Observable<DeviceStatus>>() {
                        @Override
                        public Observable<DeviceStatus> call(DeviceStatus pStatus) {
                            if (DeviceStatus.isStatusUnLogin(pStatus)) {
                                IUserAuthenticator authenticator = AssistantAuthenticSystem.getInstance()
                                        .getUserAuthenticator();
                                return authenticator.login(pUserName, pPassword, pValidationCode);
                            }

                            return Observable.error(new AutoLoginMeetUserLoginException("unknown"));
                        }
                    });
        }


        IUserAuthenticator authenticator = AssistantAuthenticSystem.getInstance()
                .getUserAuthenticator();
        return authenticator.login(pUserName, pPassword, pValidationCode);
    }

    @Override
    public Observable<DeviceStatus> login(@NonNull final String pRootCode, @NonNull final String pSchoolCode) {
        if (TextUtils.isEmpty(DeviceInfoManager.getInstance().getDeviceID())) {

            IDeviceInitiator initiator = AssistantAuthenticSystem.getInstance()
                    .getDeviceInitiator();

            return initiator.init()
                    .flatMap(new Func1<DeviceStatus, Observable<DeviceStatus>>() {
                        @Override
                        public Observable<DeviceStatus> call(DeviceStatus pStatus) {
                            if (DeviceStatus.isStatusUnLogin(pStatus)) {
                                IUserAuthenticator authenticator = AssistantAuthenticSystem.getInstance()
                                        .getUserAuthenticator();
                                return authenticator.login(pRootCode, pSchoolCode);
                            }

                            return Observable.error(new AutoLoginMeetUserLoginException("unknown"));
                        }
                    });
        }

        IUserAuthenticator authenticator = AssistantAuthenticSystem.getInstance()
                .getUserAuthenticator();
        return authenticator.login(pRootCode, pSchoolCode);
    }


    @Override
    public Observable<String> getNickName() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> pSubscriber) {
                String nickname = getConfig().getNickname();
                if (!TextUtils.isEmpty(nickname)) {
                    pSubscriber.onNext(nickname);
                    pSubscriber.onCompleted();
                    return;
                }

                try {
                    GetUserInfoResponse response = fetchUserInfoAndSaveToSp();
                    if (response.isSuccess()) {
                        pSubscriber.onNext(response.getNickName());
                        pSubscriber.onCompleted();
                        return;
                    }

                    GetUserInfoServerException exception = new GetUserInfoServerException("" +
                            response.getMsgcode());
                    CrashAnalytics.INSTANCE.reportException(exception);
                    pSubscriber.onError(exception);
                } catch (Exception pE) {
                    CrashAnalytics.INSTANCE.reportException(pE);
                    pSubscriber.onError(pE);
                }

            }
        });
    }

    private GetUserInfoResponse fetchUserInfoAndSaveToSp() throws Exception {
        String deviceID = DeviceInfoManager.getInstance().getDeviceID();
        GetUserInfoResponse response = getHttpService().getUserInfo(deviceID);

        if (response.isSuccess()) {
            getConfig().saveNickname(response.getNickName());
            getConfig().saveUserID(response.getUser_id());
            getConfig().saveDeviceCode(response.getDevice_code());
            getConfig().saveGroupCode(response.getGroupcode());

        }

        return response;

    }

    @Override
    public Observable<String> getUserID() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> pSubscriber) {
                String userID = getConfig().getUserID();
                if (!TextUtils.isEmpty(userID)) {
                    pSubscriber.onNext(userID);
                    pSubscriber.onCompleted();
                    return;
                }

                try {
                    GetUserInfoResponse response = fetchUserInfoAndSaveToSp();
                    if (response.isSuccess()) {
                        pSubscriber.onNext(response.getUser_id());
                        pSubscriber.onCompleted();
                        return;
                    }

                    GetUserInfoServerException exception = new GetUserInfoServerException("" +
                            response.getMsgcode());
                    pSubscriber.onError(exception);
                } catch (Exception pE) {
                    pSubscriber.onError(pE);
                }

            }
        });
    }

    @Override
    public Observable<UserInfo> getUserInfo() {
        return Observable.create(new Observable.OnSubscribe<UserInfo>() {
            @Override
            public void call(Subscriber<? super UserInfo> pSubscriber) {
                String userID = getConfig().getUserID();
                String username = getConfig().getNickname();
                if (!TextUtils.isEmpty(userID) && !TextUtils.isEmpty(userID)) {
                    UserInfo info = new UserInfo(username, userID, "");
                    pSubscriber.onNext(info);
                    pSubscriber.onCompleted();
                    return;
                }

                try {
                    GetUserInfoResponse response = fetchUserInfoAndSaveToSp();
                    if (response.isSuccess()) {
                        UserInfo info = new UserInfo(response.getNickName(), response.getUser_id(), "");
                        pSubscriber.onNext(info);
                        pSubscriber.onCompleted();
                        return;
                    }

                    GetUserInfoServerException exception = new GetUserInfoServerException("" +
                            response.getMsgcode());
                    pSubscriber.onError(exception);
                } catch (Exception pE) {
                    pSubscriber.onError(pE);
                }

            }
        });
    }

    @Override
    public void init(@NonNull Context pContext) {

    }
}
