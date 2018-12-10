package com.nd.android.adhoc.loginapi;

import com.nd.android.adhoc.router_api.facade.template.IProvider;

public interface IUserInfoApi extends IProvider {
    String PATH = "/loginapi/userInfo";

    String getCmdUserID();

    void onCmdUserUpdate(String pUserID);

    void addCmdUserIDListener(ICdmUserIDListener pListener);

    void removeCmdUserIDListener(ICdmUserIDListener pListener);
}
