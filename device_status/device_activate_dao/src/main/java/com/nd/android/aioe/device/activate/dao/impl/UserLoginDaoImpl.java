package com.nd.android.aioe.device.activate.dao.impl;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.net.dao.AdhocHttpDao;
import com.nd.android.aioe.device.activate.dao.api.IUserLoginDao;
import com.nd.android.aioe.device.activate.dao.api.bean.ILoginUserResult;
import com.nd.android.aioe.device.activate.dao.api.bean.LoginUcUserResult;
import com.nd.android.aioe.device.activate.dao.api.bean.LoginUserResult;
import com.nd.android.aioe.device.activate.dao.util.AuthorityUtils;
import com.nd.android.aioe.device.activate.dao.util.HttpMethod;
import com.nd.uc.account.NdUc;
import com.nd.uc.account.NdUcSdkException;
import com.nd.uc.account.OtherParamsBuilder;
import com.nd.uc.account.interfaces.IAuthenticationManager;
import com.nd.uc.account.interfaces.ICurrentUser;

import java.util.HashMap;
import java.util.Map;

class UserLoginDaoImpl extends AdhocHttpDao implements IUserLoginDao {

    private static final String TAG = "DeviceActivate";

    private final String mBaseUrl;

    public UserLoginDaoImpl(String pBaseUrl) {
        super(pBaseUrl);
        mBaseUrl = pBaseUrl;
    }

    @Override
    public ILoginUserResult login(@NonNull String pUsername, @NonNull String pPassword) throws Exception {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("username", pUsername);
            map.put("passwd", pPassword);

            Gson gson = new GsonBuilder().create();
            String content = gson.toJson(map);

            return postAction().post("/v1.1/enroll/login/", LoginUserResult.class,
                    content, null);
        } catch (Exception pE) {
            Logger.e(TAG, "UserLoginDao, login error: " + pE.getMessage());
            throw new AdhocException(pE.getMessage());
        }
    }


    @Override
    public ILoginUserResult loginUC(@NonNull String pUsername, @NonNull String pPassword, String pValidationCode, @NonNull String pUcOrgCode) throws NdUcSdkException {

        String orgCode = "";
        String userName = pUsername;
//        IMdmEnvModule module = MdmEvnFactory.getInstance().getCurEnvironment();
        if (pUsername.contains("@")) {
            userName = pUsername.substring(0, pUsername.indexOf("@"));
            orgCode = pUsername.substring(pUsername.indexOf("@") + 1);
        } else {
            orgCode = pUcOrgCode;
        }

        Map<String, Object> map = OtherParamsBuilder.create()
                .withAccountType(ICurrentUser.ACCOUNT_TYPE_ORG)
                .withOrgCode(orgCode)
                .withLoginNameType(IAuthenticationManager.LOGIN_NAME_TYPE_ORG_USER_CODE)
                .build();
        NdUc.getIAuthenticationManager().login(userName, pPassword, pValidationCode, map);

        ICurrentUser user = NdUc.getIAuthenticationManager().getCurrentUser();
        if (user == null) {
            throw new NdUcSdkException("uc current user is null, login failed");
        }

        String accessToken = user.getMacToken().getAccessToken();
        String macKey = user.getMacToken().getMacKey();
        //用于UC那边校验时候的host地址，这里郭大爷说他那边就是写死下面这个地址，而非我们在assets里面配置的服务端地址
        //并且他说跟实际用新host还是旧host没有关系，就是说两端协商用以下这个地址。
        final String strHostForUcCheck = "https://mdm.ndmdm.site";
        String url = strHostForUcCheck + "/v1.1/enroll/activate/";
        String loginToken = AuthorityUtils.mac(url, HttpMethod.POST, accessToken, macKey);

        return new LoginUcUserResult(pUsername,
                user.getCurrentUserInfo(OtherParamsBuilder.create().withForceNet(true).
                        withAllowDegrade(true).build()).getNickName(), loginToken);
    }
}
