package com.nd.android.mdm.runinfo.sdk.db.helper;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.nd.android.adhoc.basic.db.helper.AdhocDbTemplateBase;
import com.nd.android.mdm.runinfo.sdk.db.constant.AppRunInfoConstant;
import com.nd.android.mdm.runinfo.sdk.db.entity.AppRunInfoEntityHelper;
import com.nd.sdp.android.serviceloader.annotation.Service;

import java.sql.SQLException;

/**
 * Created by HuangYK on 2018/11/30.
 */
@Service(AdhocDbTemplateBase.class)
public class AppRunInfoDbTemplate extends AdhocDbTemplateBase {

    @NonNull
    @Override
    public String getDbName() {
        return AppRunInfoConstant.DB_NAME;
    }

    @Override
    public int getDbVersion() {
        return AppRunInfoConstant.DB_VERSION;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, AppRunInfoEntityHelper.getAppRunInfoEntityClass());
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) throws SQLException {
        TableUtils.dropTable(connectionSource, AppRunInfoEntityHelper.getAppRunInfoEntityClass(), true);
        onCreate(database, connectionSource);
    }
}
