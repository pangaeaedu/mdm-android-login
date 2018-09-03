package com.nd.android.adhoc.login.utils;

import com.nd.smartcan.accountclient.UCEnv;
import com.nd.smartcan.accountclient.UCManager;

public class EnvUtils {

    public static void setUcEnv(int pIndex){
        switch (pIndex) {
            case 3:
                UCManager.getInstance().setEnv(UCEnv.AWS);
                break;
            case 0:
            case 1:
            case 2:
            default:
                UCManager.getInstance().setEnv(UCEnv.PreProduct);
                break;
        }
    }
}
