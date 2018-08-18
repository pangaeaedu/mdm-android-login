package com.nd.android.mdm.wifi_sdk.business.service.intfc;

import com.nd.android.mdm.wifi_sdk.business.bean.MdmWifiPwd;

import rx.Observable;

/**
 * wifi 密码业务服务接口
 * <p>
 * Created by HuangYK on 2018/3/14.
 */

public interface IMdmWifiPwdBizService {

    Observable<Boolean> saveOrUpdateMdmWifiPw(MdmWifiPwd pWifiPwd);

    Observable<MdmWifiPwd> getWifiPwEntity(String pSsid);

    Observable<Boolean> deleteWifiPwEntity(String pSsid);
}
