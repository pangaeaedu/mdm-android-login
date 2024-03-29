package com.nd.android.adhoc.login;

import androidx.annotation.Nullable;

import com.nd.android.adhoc.router_api.facade.service.PathReplaceServiceBase;
import com.nd.sdp.android.serviceloader.annotation.Service;

@Service(PathReplaceServiceBase.class)
public class LoginApiPathReplacer extends PathReplaceServiceBase {
    @Nullable
    @Override
    public String forString(@Nullable String pPath) {
//        if(TextUtils.isEmpty(pPath)){
//            return super.forString(pPath);
//        }
//
//        if(pPath.equalsIgnoreCase(LoginApiRoutePathConstants.PATH_LOGINAPI_INIT)){
//            return LoginRoutePathConstants.PATH_LOGIN_INIT;
//        }
//
//        if(pPath.equalsIgnoreCase(LoginApiRoutePathConstants.PATH_LOGINAPI_LOGIN)){
//            return LoginRoutePathConstants.PATH_LOGIN_LOGIN;
//        }
//
//        if(pPath.equalsIgnoreCase(IUserInfoApi.PATH)){
//            return LoginRoutePathConstants.PATH_USER_INFO;
//        }

        return super.forString(pPath);
    }

    @Override
    public int priority() {
        return 1001;
    }
}
