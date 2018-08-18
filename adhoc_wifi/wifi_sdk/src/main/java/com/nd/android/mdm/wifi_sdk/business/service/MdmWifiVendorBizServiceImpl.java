package com.nd.android.mdm.wifi_sdk.business.service;

import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.mdm.wifi_sdk.business.basic.constant.MdmVendorInfo;
import com.nd.android.mdm.wifi_sdk.business.bean.MdmWifiVendor;
import com.nd.android.mdm.wifi_sdk.business.service.intfc.IMdmWifiVendorBizService;
import com.nd.android.mdm.wifi_sdk.sdk.dataService.MdmWifiDataServiceFactory;
import com.nd.android.mdm.wifi_sdk.sdk.dataService.db.entity.intfc.IMdmWifiVendorEntity;
import com.nd.android.mdm.wifi_sdk.sdk.dataService.db.service.intfc.IMdmWifiVendorDbService;

import rx.Observable;
import rx.Subscriber;

/**
 * wifi 厂商信息业务服务接口实现
 * <p>
 * Created by HuangYK on 2018/3/14.
 */
class MdmWifiVendorBizServiceImpl implements IMdmWifiVendorBizService {

    @Override
    public Observable<MdmWifiVendor> getWifiVendor(final String pBSSID) {

        return Observable.create(new Observable.OnSubscribe<MdmWifiVendor>() {
            @Override
            public void call(Subscriber<? super MdmWifiVendor> subscriber) {

                //以：分割的mac地址需要处理
                String[] items = pBSSID.split(":");
                if (items.length < 3) {
                    subscriber.onError(new AdhocException("bssid is invalid"));
                    return;
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < 3; ++i) {
                    sb.append(items[i]);
                }
                String macPrefix = sb.toString().toUpperCase();

                IMdmWifiVendorEntity vendorEntity = getDbService().getMdmWifiVendorEntity(macPrefix);
                if (vendorEntity == null) {
                    subscriber.onNext(null);
                } else {
                    MdmWifiVendor vendor = new MdmWifiVendor(vendorEntity);
//                int resId = VendorUtil.getResIdByVendorName(vendor.getName().toLowerCase());
                    vendor.setLogoName(MdmVendorInfo.getIconByName(vendor.getVendorName().toLowerCase()).getLogoResName());
                    subscriber.onNext(vendor);
                }

                subscriber.onCompleted();
            }
        });
    }


    private IMdmWifiVendorDbService getDbService() {
        return MdmWifiDataServiceFactory.getInstance().getMdmWifiVendorDbService();
    }

}
