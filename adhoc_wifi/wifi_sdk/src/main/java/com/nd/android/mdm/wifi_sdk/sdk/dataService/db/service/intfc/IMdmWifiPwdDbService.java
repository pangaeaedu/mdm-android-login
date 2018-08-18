package com.nd.android.mdm.wifi_sdk.sdk.dataService.db.service.intfc;

import com.nd.android.mdm.wifi_sdk.sdk.dataService.db.entity.intfc.IMdmWifiPwdEntity;

/**
 * wifi 帐号密码数据库服务接口
 * <p>
 * Created by HuangYK on 2018/3/13.
 */
public interface IMdmWifiPwdDbService {

    boolean saveOrUpdateMdmWifiPw(IMdmWifiPwdEntity pEntity);

    IMdmWifiPwdEntity getWifiPwEntity(String pSsid);

    boolean deleteWifiPwEntity(String pSsid);

}
