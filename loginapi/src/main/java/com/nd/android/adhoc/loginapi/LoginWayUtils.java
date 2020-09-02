package com.nd.android.adhoc.loginapi;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.nd.android.adhoc.basic.common.AdhocBasicConfig;


public class LoginWayUtils {
    public enum LoginWay{
        LOGIN_WAY_UNKNOWN,
        LOGIN_WAY_ACCOUNT_PWD,
        LOGIN_WAY_AUTO_LOGIN
    }

    private static LoginWay mLoginWay = LoginWay.LOGIN_WAY_UNKNOWN;

    public static boolean getIsAutoLogin(){
        if(LoginWay.LOGIN_WAY_UNKNOWN == mLoginWay){
            String strGroupCode = getAutoLoginRootGroupCode(AdhocBasicConfig.getInstance().getAppContext());
            if(TextUtils.isEmpty(strGroupCode)){
                mLoginWay = LoginWay.LOGIN_WAY_ACCOUNT_PWD;
            }else {
                mLoginWay = LoginWay.LOGIN_WAY_AUTO_LOGIN;
            }
        }

        return mLoginWay == LoginWay.LOGIN_WAY_AUTO_LOGIN;
    }

    private static final String KeyGroupCode = "AUTO_LOGIN_ROOT_GROUP_CODE";
    public static String getAutoLoginRootGroupCode(Context pContext) {
        try {
            ApplicationInfo appInfo = pContext.getPackageManager()
                    .getApplicationInfo(pContext.getPackageName(),
                            PackageManager.GET_META_DATA);

            if (!appInfo.metaData.containsKey(KeyGroupCode)) {
                return "";
            }

            return appInfo.metaData.getString(KeyGroupCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return "";
    }
}
