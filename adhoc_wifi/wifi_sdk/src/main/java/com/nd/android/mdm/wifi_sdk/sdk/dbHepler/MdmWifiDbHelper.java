package com.nd.android.mdm.wifi_sdk.sdk.dbHepler;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.nd.android.adhoc.basic.db.helper.AdhocDbTemplateBase;
import com.nd.android.mdm.wifi_sdk.sdk.constant.MdmWifiDbConstant;
import com.nd.android.mdm.wifi_sdk.sdk.dataService.db.entity.MdmWifiEntityHelper;
import com.nd.sdp.android.serviceloader.annotation.Service;

import java.sql.SQLException;

/**
 * wifi 模块 数据库操作实现
 * <p>
 * Created by HuangYK on 2018/3/13.
 */
@Service(AdhocDbTemplateBase.class)
public class MdmWifiDbHelper extends AdhocDbTemplateBase {

    @NonNull
    @Override
    public String getDbName() {
        return MdmWifiDbConstant.MDM_WIFI_DB_NAME;
    }

    @Override
    public int getDbVersion() {
        return MdmWifiDbConstant.MDM_WIFI_DB_VERSION;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MdmWifiEntityHelper.getWifiPwdEntityClass());
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) throws SQLException {
    }
}
