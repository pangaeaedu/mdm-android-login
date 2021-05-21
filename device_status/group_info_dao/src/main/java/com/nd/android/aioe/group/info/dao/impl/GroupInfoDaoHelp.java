package com.nd.android.aioe.group.info.dao.impl;

import androidx.annotation.NonNull;

import com.nd.android.aioe.group.info.dao.api.IGroupCheckDao;
import com.nd.android.aioe.group.info.dao.api.IGroupNodeDao;
import com.nd.android.aioe.group.info.dao.api.IGroupGetDao;

public class GroupInfoDaoHelp {

    public static IGroupNodeDao getGroupNodeDao(@NonNull String pBaseUrl) {
        return new GroupNodeDaoImpl(pBaseUrl);
    }

    public static IGroupGetDao getGroupSearchDao(@NonNull String pBaseUrl) {
        return new GroupGetDaoImpl(pBaseUrl);
    }

    public static IGroupCheckDao getGroupCheckDao(@NonNull String pBaseUrl) {
        return new GroupCheckDaoImpl(pBaseUrl);
    }

}
