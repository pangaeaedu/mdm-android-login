package com.nd.android.mdm.runinfo.business.bean;

import androidx.annotation.NonNull;

import com.nd.android.mdm.runinfo.sdk.db.entity.IAppRunInfoEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by HuangYK on 2018/12/7.
 */

public class AppRunInfoBean {

    private Long mUpdateTime;

    private Map<Long, Map<String, AppRunPackageInfoBean>> mPackageInfoMap;


    public AppRunInfoBean(Long updateTime) {
        mUpdateTime = updateTime;
        mPackageInfoMap = new ConcurrentHashMap<>();
    }

    public Long getUpdateTime() {
        return mUpdateTime;
    }

    public Map<Long, Map<String, AppRunPackageInfoBean>> getPackageInfoMap() {
        return mPackageInfoMap;
    }


    public void putPackageEntity(@NonNull IAppRunInfoEntity infoEntity) {
        Map<String, AppRunPackageInfoBean> infoBeanMap = mPackageInfoMap.get(infoEntity.getRunDate());

        if (infoBeanMap == null) {
            infoBeanMap = new ConcurrentHashMap<>();
            mPackageInfoMap.put(infoEntity.getRunDate(), infoBeanMap);
        }

        AppRunPackageInfoBean infoBean = infoBeanMap.get(infoEntity.getPackageName());
        if (infoBean == null) {
            infoBean = new AppRunPackageInfoBean(infoEntity.getPackageName());
        }
        infoBean.addPackageInfo(infoEntity.getHour(), infoEntity.getRunDate(), infoEntity.getCount());
    }
}
