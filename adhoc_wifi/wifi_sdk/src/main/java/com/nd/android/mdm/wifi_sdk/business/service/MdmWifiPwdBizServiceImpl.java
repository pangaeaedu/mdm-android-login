package com.nd.android.mdm.wifi_sdk.business.service;

import com.nd.android.mdm.wifi_sdk.business.bean.MdmWifiPwd;
import com.nd.android.mdm.wifi_sdk.business.service.intfc.IMdmWifiPwdBizService;
import com.nd.android.mdm.wifi_sdk.sdk.dataService.MdmWifiDataServiceFactory;
import com.nd.android.mdm.wifi_sdk.sdk.dataService.db.entity.MdmWifiEntityHelper;
import com.nd.android.mdm.wifi_sdk.sdk.dataService.db.entity.intfc.IMdmWifiPwdEntity;
import com.nd.android.mdm.wifi_sdk.sdk.dataService.db.service.intfc.IMdmWifiPwdDbService;

import rx.Observable;
import rx.Subscriber;

/**
 * wifi 密码业务服务接口实现
 * <p>
 * Created by HuangYK on 2018/3/19.
 */
class MdmWifiPwdBizServiceImpl implements IMdmWifiPwdBizService {


    MdmWifiPwdBizServiceImpl() {
    }


    @Override
    public Observable<Boolean> saveOrUpdateMdmWifiPw(final MdmWifiPwd pWifiPwd) {
        if (pWifiPwd == null) {
            return Observable.just(false);
        }

        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                boolean result =
                        getMdmWifiPwdDbService().saveOrUpdateMdmWifiPw(wifiPwd2Entity(pWifiPwd));
                subscriber.onNext(result);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<MdmWifiPwd> getWifiPwEntity(final String pSsid) {


        return Observable.create(new Observable.OnSubscribe<MdmWifiPwd>() {
            @Override
            public void call(Subscriber<? super MdmWifiPwd> subscriber) {
                IMdmWifiPwdEntity entity = getMdmWifiPwdDbService().getWifiPwEntity(pSsid);
                if (entity == null) {
                    subscriber.onNext(null);
                } else {
                    subscriber.onNext(new MdmWifiPwd(entity));
                }
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> deleteWifiPwEntity(final String pSsid) {


        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                boolean result = getMdmWifiPwdDbService().deleteWifiPwEntity(pSsid);
                subscriber.onNext(result);
                subscriber.onCompleted();
            }
        });
    }


    private IMdmWifiPwdDbService getMdmWifiPwdDbService() {
        return MdmWifiDataServiceFactory.getInstance().getMdmWifiPwdDbService();
    }

    private IMdmWifiPwdEntity wifiPwd2Entity(MdmWifiPwd wifiPwd) {
        IMdmWifiPwdEntity entity = MdmWifiEntityHelper.newWifiPwdEntity();
        entity.setPwd(wifiPwd.getPwd());
        entity.setSsid(wifiPwd.getSsid());
        return entity;
    }

}
