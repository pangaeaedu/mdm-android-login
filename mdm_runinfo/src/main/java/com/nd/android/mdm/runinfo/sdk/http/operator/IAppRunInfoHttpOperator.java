package com.nd.android.mdm.runinfo.sdk.http.operator;

import org.json.JSONObject;

/**
 * Created by HuangYK on 2018/12/7.
 */

public interface IAppRunInfoHttpOperator {

    String postAppRunInfo(JSONObject pAppRunInfoJson);
}
