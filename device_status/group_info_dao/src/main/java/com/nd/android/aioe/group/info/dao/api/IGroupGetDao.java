package com.nd.android.aioe.group.info.dao.api;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.aioe.group.info.dao.api.bean.GroupPathsResult;
import com.nd.android.aioe.group.info.dao.api.bean.IpLocationGroupResult;
import com.nd.android.aioe.group.info.dao.api.bean.SearchGroupPathResult;
import com.nd.android.aioe.group.info.dao.api.bean.SearchSubGroupNodeResult;

import java.util.List;

public interface IGroupGetDao {

    SearchSubGroupNodeResult searchSubGroupNodes(@NonNull String pGroupCode, int pOffset, int pLimit) throws AdhocException;

    List<SearchGroupPathResult> searchGroupPath(@NonNull String pGroupId, int pPageSize) throws AdhocException;

    GroupPathsResult getAllGroupPaths(@NonNull String pDeviceId) throws AdhocException;

    IpLocationGroupResult getSchoolCodeByIp(@NonNull String pRootCode, @NonNull String pIp) throws AdhocException;

    IpLocationGroupResult getSchoolCodeByLocation(@NonNull String pRootCode, @NonNull String pLat, @NonNull String pLgn, int pMapType,
                                                  int pScope) throws AdhocException;
}
