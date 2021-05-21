package com.nd.android.adhoc.db.dbHelper;

import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.nd.android.adhoc.basic.db.helper.AdhocDbTemplateBase;
import com.nd.android.adhoc.db.constant.MdmRunInfoDbConstant;
import com.nd.android.adhoc.db.entity.MdmRunInfoEntity;
import com.nd.sdp.android.serviceloader.annotation.Service;

import java.sql.SQLException;

/**
 * Created by linsj on 2018/3/28.
 */
@Service(AdhocDbTemplateBase.class)
public class MdmRunInfoDbTemplate extends AdhocDbTemplateBase {

    @NonNull
    @Override
    public String getDbName() {
        return MdmRunInfoDbConstant.MDM_RUNINFO_DB_NAME;
    }

    @Override
    public int getDbVersion() {
        return MdmRunInfoDbConstant.MDM_RUNINFO_DB_VERSION;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MdmRunInfoEntity.class);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) throws SQLException {
        TableUtils.dropTable(connectionSource, MdmRunInfoEntity.class, true);
        onCreate(database, connectionSource);
    }
}
