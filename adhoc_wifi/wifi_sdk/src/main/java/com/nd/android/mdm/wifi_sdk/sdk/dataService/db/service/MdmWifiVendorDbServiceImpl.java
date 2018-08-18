package com.nd.android.mdm.wifi_sdk.sdk.dataService.db.service;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.common.util.AdhocDataCheckUtils;
import com.nd.android.adhoc.basic.db.factory.AdhocDatabaseFactory;
import com.nd.android.adhoc.basic.db.helper.AdhocDatabaseHelper;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.util.storage.AdhocAssetsFileUtil;
import com.nd.android.mdm.wifi_sdk.sdk.dataService.db.entity.MdmWifiEntityHelper;
import com.nd.android.mdm.wifi_sdk.sdk.dataService.db.entity.intfc.IMdmWifiVendorEntity;
import com.nd.android.mdm.wifi_sdk.sdk.dataService.db.service.intfc.IMdmWifiVendorDbService;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * wifi 厂商信息 数据库服务实现类
 * <p>
 * Created by HuangYK on 2018/3/14.
 */
class MdmWifiVendorDbServiceImpl implements IMdmWifiVendorDbService {

    private static final String TAG = "MdmWifiVendorDbService";

    private Dao<IMdmWifiVendorEntity, String> mMdmWifiVendorDao = null;

    /**
     * 创建服务实体
     *
     * @param pDbName 数据库名称
     */
    @SuppressWarnings("unchecked")
    MdmWifiVendorDbServiceImpl(@NonNull String pDbName) {
        initDbFile(pDbName);
        AdhocDatabaseHelper databaseHelper = AdhocDatabaseFactory.getDbHelper(pDbName);
        mMdmWifiVendorDao = databaseHelper.getEntityDao(MdmWifiEntityHelper.getWifiVendorEntityClass());
    }

    private void initDbFile(String pDbName) {
        File dbFile = AdhocBasicConfig.getInstance().getAppContext().getDatabasePath(pDbName);
        if (dbFile == null || dbFile.exists()) {
            return;
        }

        AdhocAssetsFileUtil.copyFile2SdCard(
                AdhocBasicConfig.getInstance().getAppContext(),
                "vendor/" + pDbName,
                dbFile.getPath());
    }

    @Override
    public boolean saveOrUpdateMdmWifiVendor(IMdmWifiVendorEntity pEntity) {
        if (pEntity == null) {
            return false;
        }

        try {
            if (TextUtils.isEmpty(pEntity.getMacPrefix())) {
                throw new SQLException("mac prefix cannot be empty");
            }
            Dao.CreateOrUpdateStatus status = mMdmWifiVendorDao.createOrUpdate(pEntity);
            return status != null && (status.isCreated() || status.isUpdated());
        } catch (SQLException e) {
            Logger.e(TAG, "saveOrUpdateMdmWifiVendor: " + e);
        }
        return false;

    }

    @Override
    public boolean saveOrUpdateMdmWifiVendorList(final List<IMdmWifiVendorEntity> pEntityList) {
        if (pEntityList == null || pEntityList.size() <= 0) {
            return false;
        }

        //创建事务管理器
        TransactionManager transactionManager = new TransactionManager(mMdmWifiVendorDao.getConnectionSource());
        //一个调用的事件
        Callable<Boolean> callable = new Callable<Boolean>() {
            //java.util.concurrent.Callable;
            @Override
            public Boolean call() throws Exception {
                //如果异常被抛出 事件管理 就知道保存数据失败要回滚
                for (IMdmWifiVendorEntity entity : pEntityList) {
                    mMdmWifiVendorDao.createOrUpdate(entity);
                }
                return true;
            }
        };

        boolean result;
        try {
            result = transactionManager.callInTransaction(callable);//执行事件
        } catch (SQLException e) {
            result = false;
            Logger.e(TAG, "saveOrUpdateMdmWifiVendorList: " + e);
        }
        return result;
    }

    @Override
    public IMdmWifiVendorEntity getMdmWifiVendorEntity(String pMacPrefix) {
        try {
            QueryBuilder<IMdmWifiVendorEntity, String> queryBuilder = mMdmWifiVendorDao.queryBuilder();
            queryBuilder.where().eq(IMdmWifiVendorEntity.FIELD_MAC_PREFIX, pMacPrefix);
            List<IMdmWifiVendorEntity> entityList = queryBuilder.query();
            if (AdhocDataCheckUtils.isCollectionEmpty(entityList)) {
                return null;
            }
            return entityList.get(0);
        } catch (SQLException e) {
            Logger.e(TAG, "getMdmWifiVendorEntity: " + e);
        }
        return null;
    }
}
