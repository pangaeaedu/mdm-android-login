package com.nd.android.aioe.group.info.dao.api;

import androidx.annotation.NonNull;

import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.aioe.group.info.dao.api.bean.GroupNodeInfo;
import com.nd.android.aioe.group.info.dao.api.bean.GroupPageResult;

import java.util.List;

public interface IGroupNodeDao {

    List<GroupNodeInfo> getSubNodes(@NonNull String pGroupId) throws AdhocException;

    List<GroupNodeInfo> getAllSubNodes(@NonNull String pGroupId) throws AdhocException;

    GroupPageResult getSubNodesByPage(@NonNull String pGroupID, int pOffset, int pLimit) throws AdhocException;


}
