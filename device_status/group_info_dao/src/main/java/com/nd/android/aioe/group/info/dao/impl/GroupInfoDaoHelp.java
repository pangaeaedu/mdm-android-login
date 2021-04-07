package com.nd.android.aioe.group.info.dao.impl;

import android.support.annotation.NonNull;

import com.nd.android.aioe.group.info.dao.api.IGroupNodeDao;
import com.nd.android.aioe.group.info.dao.api.IGroupSearchDao;

public class GroupInfoDaoHelp {

    public static IGroupNodeDao getGroupNodeDao(@NonNull String pBaseUrl) {
        return new GroupNodeDaoImpl(pBaseUrl);
    }

    public static IGroupSearchDao getGroupSearchDao(@NonNull String pBaseUrl) {
        return new GroupSearchDaoImpl(pBaseUrl);
    }

}
