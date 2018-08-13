package com.nd.android.adhoc.login.basicService.cmdProcessor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nd.android.adhoc.login.basicService.BasicServiceFactory;
import com.nd.android.adhoc.login.basicService.data.UserActivateResult;
import com.nd.android.adhoc.loginapi.IUserActivateProcessor;

public class UserActivateProcessor extends BaseCmdProcessor implements IUserActivateProcessor {
    @Override
    public void onReceived(String pCmd) {
        try {
            Gson gson = new GsonBuilder().create();
            UserActivateResult result = gson.fromJson(pCmd, UserActivateResult.class);

            BasicServiceFactory.getInstance().notifyActivateResponse(result);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
