package com.nd.android.adhoc.login.basicService.cmdProcessor;

import com.nd.android.adhoc.login.basicService.BasicServiceFactory;
import com.nd.android.adhoc.loginapi.IForceLogoutProcessor;

public class ForceLogoutProcessor extends BaseCmdProcessor implements IForceLogoutProcessor {
    @Override
    public void onReceived(String pCmd) {
        BasicServiceFactory.getInstance().notifyForceLogout();
    }

}
