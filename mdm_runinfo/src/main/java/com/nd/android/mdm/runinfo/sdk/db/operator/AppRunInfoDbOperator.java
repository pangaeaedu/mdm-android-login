package com.nd.android.mdm.runinfo.sdk.db.operator;

import android.support.annotation.NonNull;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.nd.android.adhoc.basic.common.util.AdhocDataCheckUtils;
import com.nd.android.adhoc.basic.db.factory.AdhocDatabaseFactory;
import com.nd.android.adhoc.basic.db.helper.AdhocDatabaseHelper;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.mdm.runinfo.sdk.db.entity.AppRunInfoEntityHelper;
import com.nd.android.mdm.runinfo.sdk.db.entity.IAppRunInfoEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by HuangYK on 2018/12/5.
 */

class AppRunInfoDbOperator implements IAppRunInfoDbOperator {

    private static final String TAG = "AppRunInfoDbOperator";

    private Dao<IAppRunInfoEntity, Long> mDbDao = null;

    @SuppressWarnings("unchecked")
    AppRunInfoDbOperator(@NonNull String pDbName) {
        AdhocDatabaseHelper databaseHelper = AdhocDatabaseFactory.getDbHelper(pDbName);
        mDbDao = databaseHelper.getEntityDao(AppRunInfoEntityHelper.getAppRunInfoEntityClass());
    }


    @Override
    public boolean saveOrUpdateEntity(@NonNull IAppRunInfoEntity pEntity) {
        try {
            mDbDao.createOrUpdate(pEntity);
        } catch (SQLException e) {
            Logger.e(TAG, "saveOrUpdateEntity error: " + e);
            return false;
        }
        return true;
    }

    @Override
    public boolean saveOrUpdateEntityList(@NonNull final List<IAppRunInfoEntity> pEntityList) {
        if (pEntityList.size() <= 0) {
            return false;
        }

        //创建事务管理器
        TransactionManager transactionManager = new TransactionManager(mDbDao.getConnectionSource());
        //一个调用的事件
        Callable<Boolean> callable = new Callable<Boolean>() {
            //java.util.concurrent.Callable;
            @Override
            public Boolean call() throws Exception {
                //如果异常被抛出 事件管理 就知道保存数据失败要回滚
                for (IAppRunInfoEntity entity : pEntityList) {
                    mDbDao.createOrUpdate(entity);
                }
                return true;
            }
        };

        boolean result;
        try {
            result = transactionManager.callInTransaction(callable);//执行事件
        } catch (SQLException e) {
            result = false;
            Logger.e(TAG, "saveOrUpdateEntityList: " + e);
        }
        return result;
    }

    @Override
    public boolean deleteEntity(@NonNull String pPackageName) {
        return false;
    }

    @Override
    public boolean deleteEntityList(@NonNull List<String> pPackageNameList) {
        return false;
    }

    @Override
    public List<IAppRunInfoEntity> getAllEntityList() {

        try {
            QueryBuilder<IAppRunInfoEntity, Long> queryBuilder = mDbDao.queryBuilder();
            return queryBuilder.query();
        } catch (SQLException e) {
            Logger.e(TAG, "getEntityList error: " + e);
        }
        return null;
    }

    @Override
    public IAppRunInfoEntity getEntity(String pPackageName, long pDayOfDate, int pHour) {

        try {
            QueryBuilder<IAppRunInfoEntity, Long> queryBuilder = mDbDao.queryBuilder();

            queryBuilder.where()
                    .eq(IAppRunInfoEntity.FIELD_PACKAGE_NAME, pPackageName)
                    .and()
                    .eq(IAppRunInfoEntity.FIELD_RUN_DATE, pDayOfDate)
                    .and()
                    .eq(IAppRunInfoEntity.FIELD_HOUR, pHour);

            List<IAppRunInfoEntity> entityList = queryBuilder.query();
            if (AdhocDataCheckUtils.isCollectionEmpty(entityList)) {
                return null;
            }
            return entityList.get(0);
        } catch (SQLException e) {
            Logger.e(TAG, "getEntity error: " + e);
        }


        return null;
    }
}
