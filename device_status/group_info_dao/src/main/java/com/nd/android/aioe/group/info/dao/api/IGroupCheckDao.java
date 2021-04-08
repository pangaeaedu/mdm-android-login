package com.nd.android.aioe.group.info.dao.api;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.aioe.group.info.dao.api.bean.CheckGroupExitResult;

public interface IGroupCheckDao {

    CheckGroupExitResult checkGroupExit(@NonNull String pGroupcode) throws AdhocException;

    CheckGroupExitResult checkSchoolExit(@NonNull String groupcode, @NonNull String pLat, @NonNull String pLgn, int maptype, int pScope) throws AdhocException;

    CheckGroupExitResult checkSchoolExit(@NonNull String groupcode, @NonNull String pIp) throws AdhocException;

}
