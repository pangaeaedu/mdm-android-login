package com.nd.android.adhoc.login.processOptimization.login;

import com.nd.android.mdm.biz.env.IMdmEnvModule;
import com.nd.android.mdm.biz.env.MdmEvnFactory;
import com.nd.uc.account.NdUc;
import com.nd.uc.account.NdUcSdkException;
import com.nd.uc.account.OtherParamsBuilder;
import com.nd.uc.account.interfaces.IAuthenticationManager;
import com.nd.uc.account.interfaces.ICurrentUser;

import java.util.Map;

import rx.Observable;
import rx.Subscriber;

public class UserLoginThoughUC11 implements IUserLogin {
    @Override
    public Observable<IUserLoginResult> login(final String pUserName, final String pPassword) {

        return Observable.create(new Observable.OnSubscribe<IUserLoginResult>() {
            @Override
            public void call(Subscriber<? super IUserLoginResult> pSubscriber) {
                String orgCode = "";
                String identifyCode = "";

                IMdmEnvModule module = MdmEvnFactory.getInstance().getCurEnvironment();
                if (pUserName.contains("@")) {
                    orgCode = pUserName.substring(pUserName.indexOf("@"), pUserName.length());
                } else {
                    orgCode = module.getUcOrgCode();
                }

                Map<String, Object> map = OtherParamsBuilder.create()
                        .withAccountType(ICurrentUser.ACCOUNT_TYPE_ORG)
                        .withOrgCode(orgCode)
                        .withLoginNameType(IAuthenticationManager.LOGIN_NAME_TYPE_ORG_USER_CODE)
                        .build();
                try {
                    NdUc.getIAuthenticationManager().login(pUserName, pPassword, identifyCode, map);

                    ICurrentUser user = NdUc.getIAuthenticationManager().getCurrentUser();
                    if (user == null) {
                        pSubscriber.onError(new RuntimeException("login failed"));
                        return;
                    }

                    IUserLoginResult result = new UcLoginResultImp(pUserName,
                            user.getCurrentUserInfo().getNickName(), user.getLoginToken());
                    pSubscriber.onNext(result);
                    pSubscriber.onCompleted();
                } catch (NdUcSdkException pE) {
                    pSubscriber.onError(pE);
                }
            }
        });


    }
}