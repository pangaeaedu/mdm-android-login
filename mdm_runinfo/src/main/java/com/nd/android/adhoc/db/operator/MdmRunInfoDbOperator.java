package com.nd.android.adhoc.db.operator;

import android.support.annotation.NonNull;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.DatabaseConnection;
import com.nd.android.adhoc.basic.common.util.AdhocDataCheckUtils;
import com.nd.android.adhoc.basic.db.factory.AdhocDatabaseFactory;
import com.nd.android.adhoc.basic.db.helper.AdhocDatabaseHelper;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.db.entity.MdmRunInfoEntity;
import com.nd.android.adhoc.db.entity.intfc.IMdmRunInfoEntity;
import com.nd.android.adhoc.db.operator.intfc.IMdmRunInfoDbOperator;
import com.nd.android.adhoc.utils.AppRunInfoReportUtils;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by linsj on 2018/3/28.
 */
class MdmRunInfoDbOperator implements IMdmRunInfoDbOperator {

    private static final String TAG = "MdmDeliverH5DbOperator";


    private Dao<IMdmRunInfoEntity, Void> mRunInfoDbDao = null;

    private AdhocDatabaseHelper mDbHelper;

    @SuppressWarnings("unchecked")
    MdmRunInfoDbOperator(@NonNull String pDbName) {
        mDbHelper = AdhocDatabaseFactory.getDbHelper(pDbName);
        mRunInfoDbDao = mDbHelper.getEntityDao(MdmRunInfoEntity.class);
    }

    @Override
    public List<IMdmRunInfoEntity> getCurDayRunInfo() {
        try {
            QueryBuilder<IMdmRunInfoEntity, Void> queryBuilder = mRunInfoDbDao.queryBuilder();
            return queryBuilder.where().eq(
                    MdmRunInfoEntity.DAY_TIME_STAMP, AppRunInfoReportUtils.getCurrentDayTimeStamp()).query();
        } catch (SQLException e) {
            Logger.e(TAG, "getFileInfo error: " + e);
        }
        return null;
    }

    @Override
    public List<IMdmRunInfoEntity> getToReportRunInfo() {
        try {
            QueryBuilder<IMdmRunInfoEntity, Void> queryBuilder = mRunInfoDbDao.queryBuilder();
            queryBuilder.limit(1000L);
            return queryBuilder.where().lt(MdmRunInfoEntity.DAY_TIME_STAMP, AppRunInfoReportUtils.getCurrentDayTimeStamp()).query();
        } catch (SQLException e) {
            Logger.e(TAG, "getFileInfo error: " + e);
        }
        return null;
    }

    @Override
    public boolean deleteUnUseableRunInfo() {
        DatabaseConnection connection = new AndroidDatabaseConnection(mDbHelper.getReadableDatabase(), true);
        Savepoint savepoint = null;
        try {
            savepoint = connection.setSavePoint("start");
            connection.setAutoCommit(false);

            DeleteBuilder<IMdmRunInfoEntity, Void> deleteBuilder = mRunInfoDbDao.deleteBuilder();
            deleteBuilder.where().lt(MdmRunInfoEntity.DAY_TIME_STAMP, System.currentTimeMillis());
            deleteBuilder.delete();

            connection.commit(savepoint);
            return true;
        } catch (Exception e) {
            Logger.e(TAG,  "deleteRunInfo error: " + e);
            try {
                connection.rollback(savepoint);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean deleteRunInfo(final List<IMdmRunInfoEntity> listEntity) {
        if (AdhocDataCheckUtils.isCollectionEmpty(listEntity)) {
            return true;
        }

        DatabaseConnection connection = new AndroidDatabaseConnection(mDbHelper.getReadableDatabase(), true);
        Savepoint savepoint = null;
        try {
            savepoint = connection.setSavePoint("start");
            connection.setAutoCommit(false);

            mRunInfoDbDao.callBatchTasks(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    Logger.i(TAG, "begin to call batch tasks");
                    DeleteBuilder<IMdmRunInfoEntity, Void> deleteBuilder = mRunInfoDbDao.deleteBuilder();
                    for (IMdmRunInfoEntity entity : listEntity){
                        deleteBuilder.where().eq(MdmRunInfoEntity.DAY_TIME_STAMP, entity.getDayBeginTimeStamp())
                        .and().eq(MdmRunInfoEntity.PACKAGE_NAME, entity.getPackageName());
                        deleteBuilder.delete();
                    }
                    Logger.i(TAG, "end call batch tasks");
                    return null;
                }
            });

            connection.commit(savepoint);
            return true;
        } catch (Exception e) {
            Logger.e(TAG,  "deleteRunInfo error: " + e);
            try {
                connection.rollback(savepoint);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean saveOrUpdateRunInfo(final List<IMdmRunInfoEntity> listEntity) {
        if (AdhocDataCheckUtils.isCollectionEmpty(listEntity)) {
            return true;
        }

        DatabaseConnection connection = new AndroidDatabaseConnection(mDbHelper.getReadableDatabase(), true);
        Savepoint savepoint = null;
        try {
            savepoint = connection.setSavePoint("start");
            connection.setAutoCommit(false);

            mRunInfoDbDao.callBatchTasks(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    Logger.i(TAG, "begin to call batch tasks");

                    for (IMdmRunInfoEntity entity : listEntity){
                        mRunInfoDbDao.createOrUpdate(entity);
                    }
                    Logger.i(TAG, "end call batch tasks");
                    return null;
                }
            });

            connection.commit(savepoint);
            return true;
        } catch (Exception e) {
            Logger.e(TAG,  "saveOrUpdateRunInfo error: " + e);
            try {
                connection.rollback(savepoint);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }
}
