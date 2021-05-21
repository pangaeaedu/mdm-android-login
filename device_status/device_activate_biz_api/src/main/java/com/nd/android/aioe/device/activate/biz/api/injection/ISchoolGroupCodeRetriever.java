package com.nd.android.aioe.device.activate.biz.api.injection;

import androidx.annotation.NonNull;

public interface ISchoolGroupCodeRetriever {

    @NonNull
    String retrieveGroupCode(String pRootCode) throws Exception;

    @NonNull
    String onGroupNotFound(String pRootCode) throws Exception;
}
