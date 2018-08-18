package com.nd.android.mdm.wifi_sdk.sdk.dataService.db.service.intfc;

import com.nd.android.mdm.wifi_sdk.sdk.dataService.db.entity.intfc.IMdmWifiVendorEntity;

import java.util.List;

/**
 * 厂商信息 数据库操作服务
 * <p>
 * Created by HuangYK on 2018/3/14.
 */
public interface IMdmWifiVendorDbService {

    boolean saveOrUpdateMdmWifiVendor(IMdmWifiVendorEntity pEntity);

    boolean saveOrUpdateMdmWifiVendorList(List<IMdmWifiVendorEntity> pEntityList);

    IMdmWifiVendorEntity getMdmWifiVendorEntity(String pMacPrefix);

}
