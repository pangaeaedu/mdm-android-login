package com.nd.android.mdm.runinfo.sdk.db.operator;

import android.support.annotation.NonNull;

import com.nd.android.mdm.runinfo.sdk.db.entity.IAppRunInfoEntity;

import java.util.List;

/**
 * Created by HuangYK on 2018/12/5.
 */

public interface IAppRunInfoDbOperator {


    boolean saveOrUpdateEntity(@NonNull IAppRunInfoEntity pEntity);

    boolean saveOrUpdateEntityList(@NonNull List<IAppRunInfoEntity> pEntityList);

    boolean deleteEntity(@NonNull String pPackageName);

    boolean deleteEntityList(@NonNull List<String> pPackageNameList);

    List<IAppRunInfoEntity> getAllEntityList();

    IAppRunInfoEntity getEntity(String pPackageName, long pDayOfDate, int pHour);

    boolean dropTable();
}
