package com.nd.android.mdm.wifi_sdk.sdk.dataService.db.service;

import android.text.TextUtils;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.nd.android.adhoc.basic.common.util.AdhocDataCheckUtils;
import com.nd.android.adhoc.basic.db.factory.AdhocDatabaseFactory;
import com.nd.android.adhoc.basic.db.helper.AdhocDatabaseHelper;
import com.nd.android.mdm.wifi_sdk.sdk.dataService.db.entity.MdmWifiEntityHelper;
import com.nd.android.mdm.wifi_sdk.sdk.dataService.db.entity.intfc.IMdmWifiPwdEntity;
import com.nd.android.mdm.wifi_sdk.sdk.dataService.db.service.intfc.IMdmWifiPwdDbService;

import java.sql.SQLException;
import java.util.List;

/**
 * wifi 帐号密码数据库服务实现类
 * <p>
 * Created by HuangYK on 2018/3/13.
 */
class MdmWifiPwdDbServiceImpl implements IMdmWifiPwdDbService {

    private static final String TAG = "MdmWifiPwdDbServiceImpl";


    private Dao<IMdmWifiPwdEntity, String> mMdmWifiPwDao = null;

    /**
     * 创建服务实体
     *
     * @param pDbName 数据库名称
     */
    @SuppressWarnings("unchecked")
    MdmWifiPwdDbServiceImpl(String pDbName) {
        AdhocDatabaseHelper databaseHelper = AdhocDatabaseFactory.getDbHelper(pDbName);
        mMdmWifiPwDao = databaseHelper.getEntityDao(MdmWifiEntityHelper.getWifiPwdEntityClass());
    }


    @Override
    public boolean saveOrUpdateMdmWifiPw(IMdmWifiPwdEntity pEntity) {
        if (pEntity == null) {
            return false;
        }

        try {
            if (TextUtils.isEmpty(pEntity.getSsid())) {
                throw new SQLException("ssid cannot be empty");
            }
            Dao.CreateOrUpdateStatus status = mMdmWifiPwDao.createOrUpdate(pEntity);
            return status != null && (status.isCreated() || status.isUpdated());
        } catch (SQLException e) {
            Log.e(TAG, "saveOrUpdateMdmWifiPwd: " + e);
        }
        return false;
    }

    @Override
    public IMdmWifiPwdEntity getWifiPwEntity(String pSsid) {
        try {
            QueryBuilder<IMdmWifiPwdEntity, String> queryBuilder = mMdmWifiPwDao.queryBuilder();
            queryBuilder.where().eq(IMdmWifiPwdEntity.FIELD_SSID, pSsid);
            List<IMdmWifiPwdEntity> entityList = queryBuilder.query();
            if (AdhocDataCheckUtils.isCollectionEmpty(entityList)) {
                return null;
            }
            return entityList.get(0);
        } catch (SQLException e) {
            Log.e(TAG, "getWifiPwdEntity: " + e);
        }
        return null;
    }

    @Override
    public boolean deleteWifiPwEntity(String pSsid) {
        try {
            DeleteBuilder<IMdmWifiPwdEntity, String> deleteBuilder = mMdmWifiPwDao.deleteBuilder();
            deleteBuilder.where().eq(IMdmWifiPwdEntity.FIELD_SSID, pSsid);
            return deleteBuilder.delete() > 0;
        } catch (SQLException e) {
            Log.e(TAG, "deleteWifiPwdEntity: " + e);
        }
        return false;
    }
}
