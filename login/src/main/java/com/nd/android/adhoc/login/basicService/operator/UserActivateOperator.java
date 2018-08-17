package com.nd.android.adhoc.login.basicService.operator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nd.android.adhoc.login.basicService.BasicServiceFactory;
import com.nd.android.adhoc.login.basicService.data.push.UserActivateResult;
import com.nd.android.mdm.mdm_feedback_biz.IFeedbackOperator;

public class UserActivateOperator implements IFeedbackOperator{
    @Override
    public String getCmdName() {
        return "activateenable";
    }

    @Override
    public void operate(String pContent) {
        try {
            Gson gson = new GsonBuilder().create();
            UserActivateResult result = gson.fromJson(pContent, UserActivateResult.class);

            BasicServiceFactory.getInstance().notifyActivateResponse(result);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
