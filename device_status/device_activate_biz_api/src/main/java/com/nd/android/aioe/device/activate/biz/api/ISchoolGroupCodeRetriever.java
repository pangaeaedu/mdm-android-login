package com.nd.android.aioe.device.activate.biz.api;

public interface ISchoolGroupCodeRetriever {

    String retrieveGroupCode(String pRootCode) throws Exception;

    String onGroupNotFound(String pRootCode) throws Exception;
}
