package com.nd.android.adhoc.login.eventListener;

import com.nd.android.adhoc.login.basicService.data.push.UserActivateResult;

public interface IUserActivateListener {
    void onUserActivateResult(UserActivateResult pResult);
}
